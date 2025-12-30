 6
import React, { useState, useEffect } from 'react';
import apiClient from '../api/client';

/**
 * 图书搜索组件
 * 提供根据书名搜索图书的功能，并展示搜索结果或全部图书列表
 */
const BookSearch = () => {
  const [searchTerm, setSearchTerm] = useState(''); // 搜索关键词状态
  const [books, setBooks] = useState([]);           // 图书数据状态
  const [loading, setLoading] = useState(false);    // 加载状态
  const [error, setError] = useState('');           // 错误信息状态

  /**
   * 防抖搜索副作用钩子
   * 当搜索词变化时，延迟执行搜索请求以避免频繁调用API
   * 延迟时间为500毫秒
   */
  useEffect(() => {
    const delayDebounceFn = setTimeout(async () => {
      if (searchTerm.trim() !== '') {
        await performSearch(searchTerm.trim());
      } else {
        // 如果搜索词为空，则获取所有书籍
        await fetchAllBooks();
      }
    }, 500); // 延迟500毫秒执行

    return () => clearTimeout(delayDebounceFn);
  }, [searchTerm]);

  /**
   * 执行图书搜索请求
   * @param {string} term - 要搜索的图书标题
   */
  const performSearch = async (term) => {
    setLoading(true);
    setError('');

    try {
      const response = await apiClient.post('/books/getBookByTitle', { title: term });
      setBooks(response.data);
    } catch (err) {
      if (err.response?.status === 404) {
        setBooks([]);
      } else {
        setError('搜索失败，请稍后重试');
      }
    } finally {
      setLoading(false);
    }
  };

  /**
   * 获取所有图书列表
   */
  const fetchAllBooks = async () => {
    setLoading(true);
    setError('');

    try {
      const response = await apiClient.get('/books/getAllBooks');
      setBooks(response.data);
    } catch (err) {
      setError('获取书籍列表失败，请稍后重试');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="book-search">
      <h2>图书查询</h2>

      <div className="search-form">
        <div className="form-group">
          <input
            type="text"
            placeholder="请输入书名"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {error && <div className="error-message">{error}</div>}

      <div className="books-list">
        {books.length > 0 ? (
          <div className="books-grid">
            {books.map((book) => (
              <div key={book.id} className="book-card">
                  <h3>{book.title}</h3>
                  <p>Id: {book.id}</p>
                  <p>作者: {book.author}</p>
                  <p>ISBN: {book.isbn}</p>
                  <p>描述: {book.description}</p>
              </div>
            ))}
          </div>
        ) : !loading && searchTerm && (
          <p>未找到相关图书</p>
        )}
      </div>
    </div>
  );
};

export default BookSearch;
