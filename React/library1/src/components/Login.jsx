 6
import React, { useState } from 'react';
import apiClient from '../api/client';

/**
 * 登录/注册组件
 *
 * @param {Object} props - 组件属性
 * @param {Function} props.onLogin - 登录成功后的回调函数，接收用户数据作为参数
 * @returns {JSX.Element} 登录/注册表单界面
 */
const Login = ({ onLogin }) => {
  // 用户凭证状态：包括用户名和密码
  const [credentials, setCredentials] = useState({ name: '', password: '' });
  // 错误信息状态
  const [error, setError] = useState('');
  // 注册模式切换状态：true表示注册模式，false表示登录模式
  const [isRegistering, setIsRegistering] = useState(false);

  /**
   * 处理表单输入框变化事件
   *
   * @param {Event} e - 输入框change事件对象
   */
  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  /**
   * 处理表单提交事件
   * 根据当前模式（登录或注册）调用相应的API接口
   *
   * @param {Event} e - 表单submit事件对象
   */
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    try {
      if (isRegistering) {
        const response = await apiClient.post('/users/register', credentials);
        onLogin(response.data);
      } else {
        const response = await apiClient.post('/users/login', credentials);
        onLogin(response.data);
      }
    } catch (err) {
      setError(err.response?.data || '操作失败');
    }
  };

  return (
    <div className="login-wrapper">
      <div className="login-container">
        <form onSubmit={handleSubmit} className="login-form">
          <h2>{isRegistering ? '用户注册' : '用户登录'}</h2>

          {/* 显示错误信息 */}
          {error && <div className="error-message">{error}</div>}

          <div className="form-group">
            <label htmlFor="name">用户名:</label>
            <input
              type="text"
              id="name"
              name="name"
              value={credentials.name}
              onChange={handleChange}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="password">密码:</label>
            <input
              type="password"
              id="password"
              name="password"
              value={credentials.password}
              onChange={handleChange}
              required
            />
          </div>

          <button type="submit" className="submit-btn">
            {isRegistering ? '注册' : '登录'}
          </button>

          {/* 切换登录/注册模式按钮 */}
          <button
            type="button"
            onClick={() => setIsRegistering(!isRegistering)}
            className="toggle-btn"
          >
            {isRegistering ? '已有账户？点击登录' : '没有账户？点击注册'}
          </button>
        </form>
      </div>
    </div>
  );
};

export default Login;

