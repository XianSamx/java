 6
import React, { useState, useEffect } from 'react';
import apiClient from '../api/client';

/**
 * 借阅历史组件，用于展示当前用户的图书借阅记录。
 *
 * @param {Object} currentUser - 当前登录用户对象，必须包含 id 字段以标识用户。
 * @returns {JSX.Element} 渲染借阅历史列表或相关提示信息。
 */
const BorrowHistory = ({ currentUser }) => {
  const [history, setHistory] = useState([]);       // 存储借阅历史数据
  const [loading, setLoading] = useState(false);    // 加载状态标志
  const [error, setError] = useState('');           // 错误消息

  /**
   * 组件挂载时以及 currentUser 变化时获取用户的借阅历史。
   * 获取完成后根据操作时间对历史进行降序排序。
   */
  useEffect(() => {
    const fetchBorrowHistory = async () => {
      if (!currentUser || !currentUser.id) return;

      setLoading(true);
      setError('');

      try {
        const response = await apiClient.post('/borrowhistory/getBorrowHistoryByUserId', {
          userId: currentUser.id
        });

        // 对获取到的历史记录按时间降序排列
        const sortedHistory = Array.isArray(response.data)
          ? response.data.sort((a, b) => new Date(b.date) - new Date(a.date))
          : [];

        setHistory(sortedHistory);
      } catch (err) {
        setError(err.response?.data || '获取借阅历史失败');
        setHistory([]);
      } finally {
        setLoading(false);
      }
    };

    fetchBorrowHistory();
  }, [currentUser]);

  /**
   * 格式化日期字符串为中文本地格式的时间显示。
   *
   * @param {string} dateString - ISO 格式的日期字符串。
   * @returns {string} 格式化后的本地时间字符串。
   */
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
  };

  return (
    <div className="borrow-history">
      <h2>借阅历史</h2>

      {/* 显示加载状态 */}
      {loading && <p>加载中...</p>}

      {/* 显示错误信息 */}
      {error && <div className="error-message">{error}</div>}

      <div className="history-list">
        {!loading && !error && (
          <>
            {history.length > 0 ? (
              <table className="history-table">
                <thead>
                  <tr>
                    <th>书籍ID</th>
                    <th>操作类型</th>
                    <th>操作时间</th>
                  </tr>
                </thead>
                <tbody>
                  {/* 遍历并渲染每条借阅记录 */}
                  {history.map((record) => (
                    <tr key={record.id}>
                      <td>{record.bookId}</td>
                      <td>{record.behavour}</td>
                      <td>{record.date ? formatDate(record.date) : '未知时间'}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p>暂无借阅历史记录</p>
            )}
          </>
        )}
      </div>
    </div>
  );
};

export default BorrowHistory;
