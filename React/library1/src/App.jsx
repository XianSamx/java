import React, { useState } from 'react';
import Login from './components/Login.jsx';
import BookSearch from './components/BookSearch.jsx';
import BorrowReturn from './components/BorrowReturn';
import Reservation from './components/Reservation';
import OverdueReminder from './components/OverdueReminder';
import BorrowHistory from './components/BorrowHistory'; // 新增导入
import './App.css';

function App() {
  const [currentUser, setCurrentUser] = useState(null);
  const [activeTab, setActiveTab] = useState('search');

  const handleLogin = (user) => {
    setCurrentUser(user);
  };

  const handleLogout = () => {
    setCurrentUser(null);
  };

  if (!currentUser) {
    return <Login onLogin={handleLogin} />;
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>在线图书借阅系统</h1>
        <div>
          <span>欢迎, {currentUser.name}</span>
          <button onClick={handleLogout}>退出登录</button>
        </div>
      </header>

      <nav className="app-nav">
        <button
          className={activeTab === 'search' ? 'active' : ''}
          onClick={() => setActiveTab('search')}
        >
          图书查询
        </button>
        <button
          className={activeTab === 'borrow' ? 'active' : ''}
          onClick={() => setActiveTab('borrow')}
        >
          借还操作
        </button>
        <button
          className={activeTab === 'reserve' ? 'active' : ''}
          onClick={() => setActiveTab('reserve')}
        >
          预约管理（未实现）
        </button>
        <button
          className={activeTab === 'overdue' ? 'active' : ''}
          onClick={() => setActiveTab('overdue')}
        >
          逾期提醒
        </button>
        {/* 新增借阅历史按钮 */}
        <button
          className={activeTab === 'history' ? 'active' : ''}
          onClick={() => setActiveTab('history')}
        >
          借阅历史
        </button>
      </nav>

      <main className="app-main">
        {activeTab === 'search' && <BookSearch />}
        {activeTab === 'borrow' && <BorrowReturn currentUser={currentUser} />}
        {activeTab === 'reserve' && <Reservation currentUser={currentUser} />}
        {activeTab === 'overdue' && <OverdueReminder currentUser={currentUser} />} {/* 添加 currentUser */}
        {activeTab === 'history' && <BorrowHistory currentUser={currentUser} />} {/* 新增历史组件 */}
      </main>
    </div>
  );
}

export default App;
