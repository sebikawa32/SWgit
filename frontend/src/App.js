import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import Header from "./Header/Header";

import HomePage from './Home/HomePage';
import CategoryPage from './Ticket/TicketDetailPage';
import TicketDetailPage from './Ticket/TicketDetailPage';
import SearchPage from './Search/SearchPage';
import LoginPage from './Login/LoginPage';
import SignupPage from './Login/SignupPage'; 
import './App.css';

function App() {
  return (
    <Router>
      <Header />
      <Routes>
        {/* ✅ 로그인 페이지 */}
        <Route path="/login" element={
          <div className="login-page-wrapper">
            <LoginPage />
          </div>
        } />

        {/* ✅ 회원가입 페이지 */}
        <Route path="/signup" element={
          <div className="signup-page-wrapper">
            <SignupPage />
          </div>
        } />

        {/* ✅ 홈 페이지 */}
        <Route path="/" element={
          <main className="content">
            <HomePage />
          </main>
        } />

        {/* ✅ 카테고리별 페이지 */}
        <Route path="/category" element={
          <main className="content">
            <CategoryPage />
          </main>
        } />

        {/* ✅ 공연 상세 페이지 */}
        <Route path="/ticket/:id" element={
          <main className="content">
            <TicketDetailPage />
          </main>
        } />

        {/* ✅ 검색 페이지 */}
        <Route path="/search" element={
          <main className="content">
            <SearchPage />
          </main>
        } />
      </Routes>
    </Router>
  );
}

export default App;
