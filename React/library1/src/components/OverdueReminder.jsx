 6
import React, { useState, useEffect } from 'react';
import apiClient from '../api/client';

/**
 * OverdueReminder 组件用于展示当前用户逾期未还的图书列表。
 *
 * @param {Object} props - 组件接收的属性对象
 * @param {Object} props.currentUser - 当前登录用户的信息对象，必须包含 id 字段
 * @returns {JSX.Element} 渲染逾期提醒界面
 */
const OverdueReminder = ({ currentUser }) => {
  const [overdueBooks, setOverdueBooks] = useState([]); // 存储逾期书籍数据
  const [loading, setLoading] = useState(false);       // 加载状态标识
  const [error, setError] = useState('');             // 错误信息存储

  /**
   * 使用 useEffect 在组件挂载或 currentUser 变更时获取用户的逾期书籍信息。
   * 首先请求用户的逾期借阅记录，然后根据 bookId 获取每本书的详细信息。
   */
  useEffect(() => {
    const fetchOverdueBooks = async () => {
      if (!currentUser || !currentUser.id) return;

      setLoading(true);
      setError('');

      try {
        // 第一步：获取用户的逾期借阅记录
        const response = await apiClient.post('/borrow/overdue', {
          userId: currentUser.id
        });

        // 检查是否有逾期记录
        if (Array.isArray(response.data) && response.data.length > 0) {
          // 第二步：获取每本书的详细信息
          const booksWithDetails = await Promise.all(
            response.data.map(async (record) => {
              try {
                // 根据bookId获取书籍详细信息
                const bookResponse = await apiClient.post('/books/getBookById', {
                  id: record.bookId
                });
                return {
                  ...record,
                  ...bookResponse.data
                };
              } catch (bookErr) {
                console.warn(`Failed to fetch book details for ID ${record.bookId}:`, bookErr);
                // 如果获取书籍详情失败，仍然保留记录信息
                return {
                  ...record,
                  title: `书籍ID: ${record.bookId}`,
                  author: '未知'
                };
              }
            })
          );
          setOverdueBooks(booksWithDetails);
        } else {
          setOverdueBooks([]);
        }
      } catch (err) {
        console.error('Error fetching overdue books:', err);
        setError(err.response?.data || '获取逾期书籍信息失败');
        setOverdueBooks([]);
      } finally {
        setLoading(false);
      }
    };

    fetchOverdueBooks();
  }, [currentUser]);

  /**
   * 将 ISO 时间字符串格式化为中文本地日期格式（YYYY/MM/DD）
   *
   * @param {string} dateString - 待格式化的日期字符串
   * @returns {string} 格式化后的日期字符串
   */
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN');
  };

  return (
    <div className="overdue-reminder">
      <h2>逾期提醒</h2>

      {loading && <p>加载中...</p>}
      {error && <div className="error-message">{error}</div>}

      <div className="overdue-list">
        {!loading && !error && (
          <>
            {overdueBooks.length > 0 ? (
              <>
                <p className="warning-text">您有以下图书已逾期，请尽快归还：</p>
                {overdueBooks.map((book) => (
                  <div key={book.id} className="overdue-item">
                    <h3>{book.title}</h3>
                    <p>作者: {book.author}</p>
                    <p>应还日期: {book.returnDate ? formatDate(book.returnDate) : '未知'}</p>
                    <p className="overdue-text">已逾期</p>
                  </div>
                ))}
              </>
            ) : (
              <p>恭喜！您当前没有逾期图书。</p>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default OverdueReminder;
