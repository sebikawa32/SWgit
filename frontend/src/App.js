// App.js
import React from "react";
import { Routes, Route } from "react-router-dom";
import Header from "./Header/Header";
import HomePage from './Home/HomePage';
import SearchPage from './Search/SearchPage';
import LoginPage from './Login/LoginPage';
import SignupPage from './Login/SignupPage';
import ConcertPage from './Ticket/ConcertPage';
import MusicalPage from './Ticket/MusicalPage';
import PlayPage from './Ticket/PlayPage';
import ExhibitionPage from './Ticket/ExhibitionPage';
import TicketDetailPage from './Ticket/TicketDetailPage';
import Bookmark from './My/Bookmark';  

import './App.css';

function App() {
  return (
    <>
      {/* ✅ 공통 헤더 */}
      <Header />

      {/* ✅ 페이지별 라우팅 */}
      <Routes>
        {/* ✅ 로그인 페이지 */}
        <Route
          path="/login"
          element={
            <div className="login-page-wrapper">
              <LoginPage />
            </div>
          }
        />

        {/* ✅ 회원가입 페이지 */}
        <Route
          path="/signup"
          element={
            <div className="signup-page-wrapper">
              <SignupPage />
            </div>
          }
        />

        {/* ✅ 홈 페이지 */}
        <Route
          path="/"
          element={
            <main className="content">
              <HomePage />
            </main>
          }
        />

        {/* ✅ 공연 카테고리별 페이지 */}
        <Route
          path="/concerts"
          element={
            <main className="content">
              <ConcertPage />
            </main>
          }
        />
        <Route
          path="/musicals"
          element={
            <main className="content">
              <MusicalPage />
            </main>
          }
        />
        <Route
          path="/plays"
          element={
            <main className="content">
              <PlayPage />
            </main>
          }
        />
        <Route
          path="/exhibitions"
          element={
            <main className="content">
              <ExhibitionPage />
            </main>
          }
        />

        {/* ✅ 검색 페이지 */}
        <Route
          path="/search"
          element={
            <main className="content">
              <SearchPage />
            </main>
          }
        />

        {/* ✅ 티켓 상세 페이지 (동적 경로) */}
        <Route
          path="/ticket/:id"
          element={
            <main className="content">
              <TicketDetailPage />
            </main>
          }
        />

        {/* ✅ 즐겨찾기 페이지 */}
        <Route
          path="/Bookmark"
          element={
            <main className="content">
              <Bookmark />
            </main>
          }
        />
      </Routes>
    </>
  );
}

export default App;
