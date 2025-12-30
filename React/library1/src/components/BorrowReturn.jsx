 6
import React, { useState, useEffect } from 'react';
import apiClient from '../api/client';

/**
 * 借还书功能组件
 *
 * @param {Object} props - 组件属性
 * @param {Object} props.currentUser - 当前登录用户信息对象
 * @returns {JSX.Element} 返回借还书界面的 JSX 元素
 */
const BorrowReturn = ({ currentUser }) => {
  const [bookId, setBookId] = useState('');
  const [action, setAction] = useState('borrow'); // 'borrow' or 'return'
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [borrowedBooks, setBorrowedBooks] = useState([]); // 存储已借书籍
  const [availableBooks, setAvailableBooks] = useState([]); // 存储可借书籍
  const [loading, setLoading] = useState(false); // 加载状态
  const [bookDetails, setBookDetails] = useState({});

  /**
   * 根据书籍 ID 获取书籍详细信息
   *
   * @param {string|number} bookId - 要查询的书籍 ID
   * @returns {Promise<Object|null>} 返回书籍详情对象或 null（如果失败）
   */
  const fetchBookDetails = async (bookId) => {
    try {
      const response = await apiClient.post('/books/getBookById', { id: bookId });
      return response.data;
    } catch (err) {
      console.error('获取书籍详情失败:', err);
      return null;
    }
  };

  /**
   * 获取当前用户的已借书籍列表
   *
   * @returns {Promise<void>}
   */
  const fetchBorrowedBooks = async () => {
    if (action === 'return' && currentUser) {
      setLoading(true);
      try {
        // 使用 POST 请求并通过 JSON 发送请求体
        const response = await apiClient.post('/borrow/user', {
          userId: currentUser.id
        });
        setBorrowedBooks(response.data || []);
      } catch (err) {
        setError(err.response?.data || '获取已借书籍失败');
        setBorrowedBooks([]);
      } finally {
        setLoading(false);
      }
    }
  };

  /**
   * 获取系统中所有未被借出的书籍列表
   *
   * @returns {Promise<void>}
   */
  const fetchAvailableBooks = async () => {
    if (action === 'borrow') {
      setLoading(true);
      try {
        // 并行获取所有书籍和所有借阅记录
        const [booksResponse, borrowRecordsResponse] = await Promise.all([
          apiClient.get('/books/getAllBooks'),
          apiClient.get('/borrow/all')
        ]);

        const allBooks = booksResponse.data || [];
        const allBorrowRecords = borrowRecordsResponse.data || [];

        // 创建已借书籍ID的集合
        const borrowedBookIds = new Set(allBorrowRecords.map(record => record.bookId));

        // 过滤出未被借出的书籍
        const availableBooksList = allBooks.filter(book => !borrowedBookIds.has(book.id));
        setAvailableBooks(availableBooksList);
      } catch (err) {
        setError(err.response?.data || '获取可借书籍失败');
        setAvailableBooks([]);
      } finally {
        setLoading(false);
      }
    }
  };

  // 当切换操作类型时获取相应数据
  useEffect(() => {
    if (action === 'borrow') {
      fetchAvailableBooks();
      setBorrowedBooks([]); // 清空已借书籍列表
    } else if (action === 'return') {
      fetchBorrowedBooks();
      setAvailableBooks([]); // 清空可借书籍列表
    }
  }, [action, currentUser]);

  /**
   * 执行借书或还书操作
   *
   * @param {React.FormEvent} e - 表单提交事件对象
   * @returns {Promise<void>}
   */
  const handleBorrowReturn = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    if (!bookId) {
      setError('请输入书籍ID');
      return;
    }

    try {
      if (action === 'borrow') {
        // 借书操作
        const response = await apiClient.post('/borrow/add', {
          bookId: parseInt(bookId),
          userId: currentUser.id
        });
        setMessage(response.data);
        // 借书成功后刷新可借书籍列表
        fetchAvailableBooks();
      } else {
        // 还书操作
        const response = await apiClient.delete('/borrow/back', {
          data: { bookId: parseInt(bookId) }
        });
        setMessage(response.data);
        // 还书成功后刷新已借书籍列表
        fetchBorrowedBooks();
      }
      // 清空输入框
      setBookId('');
    } catch (err) {
      setError(err.response?.data || '操作失败');
    }
  };

  /**
   * 处理点击书籍卡片选择书籍的操作
   *
   * @param {string|number} bookId - 被选中的书籍 ID
   * @returns {void}
   */
  const handleBookClick = (bookId) => {
    setBookId(bookId.toString());
  };

  return (
    <div className="borrow-return">
      <h2>借还操作</h2>

      <form onSubmit={handleBorrowReturn} className="operation-form">
        <div className="form-group">
          <label>操作类型:</label>
          <div className="button-group">
            <button
              type="button"
              className={action === 'borrow' ? 'active' : ''}
              onClick={() => setAction('borrow')}
            >
              借书
            </button>
            <button
              type="button"
              className={action === 'return' ? 'active' : ''}
              onClick={() => setAction('return')}
            >
              还书
            </button>
          </div>
        </div>

        {/* 显示可借书籍网格 (仅在借书模式下) */}
        {action === 'borrow' && (
          <div className="form-group">
            <label>可借书籍:</label>
            {loading ? (
              <p>加载中...</p>
            ) : availableBooks.length > 0 ? (
              <div className="books-grid">
                {availableBooks.map((book) => (
                  <div
                    key={book.id}
                    className={`book-card ${bookId === book.id.toString() ? 'selected' : ''}`}
                    onClick={() => handleBookClick(book.id)}
                  >
                    <h3>{book.title}</h3>
                    <p>作者: {book.author}</p>
                    <p>ID: {book.id}</p>
                  </div>
                ))}
              </div>
            ) : (
              <p>暂无可借书籍</p>
            )}
          </div>
        )}

        {action === 'return' && (
          <div className="form-group">
            <label>已借书籍:</label>
            {loading ? (
              <p>加载中...</p>
            ) : borrowedBooks.length > 0 ? (
              <div className="books-grid">
                {borrowedBooks.map((book) => {
                  // 如果还没有获取到书籍详情，则异步获取
                  if (!bookDetails[book.bookId]) {
                    const details = fetchBookDetails(book.bookId);
                    details.then(data => {
                      if (data) {
                        setBookDetails(prev => ({ ...prev, [book.bookId]: data }));
                      }
                    });
                  }

                  const bookInfo = bookDetails[book.bookId] || book;

                  return (
                    <div
                      key={book.bookId}
                      className={`book-card ${bookId === book.bookId.toString() ? 'selected' : ''}`}
                      onClick={() => handleBookClick(book.bookId)}
                    >
                      <h3>{bookInfo.title}</h3>
                      <p>作者: {bookInfo.author}</p>
                      <p>ID: {book.bookId}</p>
                    </div>
                  );
                })}
              </div>
            ) : (
              <p>暂无已借书籍</p>
            )}
          </div>
        )}

        <div className="form-group">
          <label htmlFor="bookId">书籍ID:</label>
          <input
            type="number"
            id="bookId"
            value={bookId}
            onChange={(e) => setBookId(e.target.value)}
            placeholder="请输入书籍ID"
          />
        </div>

        <button type="submit">执行操作</button>
      </form>

      {message && <div className="success-message">{message}</div>}
      {error && <div className="error-message">{error}</div>}
    </div>
  );
};

export default BorrowReturn;
