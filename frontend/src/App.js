import React, { useEffect, useState } from "react";
import { Routes, Route } from "react-router-dom";
import { jwtDecode } from "jwt-decode"; // ✅ 토큰 디코딩용

// ✅ 공통 컴포넌트
import Header from "./Header/Header";

// ✅ 페이지 컴포넌트들
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
import MyProfile from "./My/Myprofile";
import BoardListPage from './Board/BoardListPage';
import BoardDetailPage from './Board/BoardDetailPage';
import BoardCreatePage from './Board/BoardCreatePage';

import './App.css';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  // ✅ 새로고침 또는 진입 시 토큰 만료 여부 확인 → 자동 로그아웃
  useEffect(() => {
    const token = localStorage.getItem("accessToken");

    if (token) {
      try {
        const { exp } = jwtDecode(token); // 만료 시간 추출
        const now = Date.now() / 1000;

        if (exp < now) {
          // 만료된 토큰 → 자동 로그아웃 처리
          localStorage.clear();
          alert("세션이 만료되었습니다. 다시 로그인해주세요.");
          window.location.href = "/login";
        } else {
          setIsLoggedIn(true); // 유효하면 로그인 상태 유지
        }
      } catch (e) {
        console.error("❌ 토큰 디코딩 실패", e);
        localStorage.clear();
      }
    }
  }, []);

  return (
    <>
      {/* ✅ 공통 헤더: 로그인 여부 전달 */}
      <Header isLoggedIn={isLoggedIn} />

      {/* ✅ 페이지 라우팅 */}
      <Routes>
        <Route path="/login" element={<div className="login-page-wrapper"><LoginPage setIsLoggedIn={setIsLoggedIn} /></div>} />
        <Route path="/signup" element={<div className="signup-page-wrapper"><SignupPage /></div>} />
        <Route path="/" element={<main className="content"><HomePage /></main>} />
        <Route path="/concerts" element={<main className="content"><ConcertPage /></main>} />
        <Route path="/musicals" element={<main className="content"><MusicalPage /></main>} />
        <Route path="/plays" element={<main className="content"><PlayPage /></main>} />
        <Route path="/exhibitions" element={<main className="content"><ExhibitionPage /></main>} />
        <Route path="/search" element={<main className="content"><SearchPage /></main>} />
        <Route path="/ticket/:id" element={<main className="content"><TicketDetailPage /></main>} />
        <Route path="/bookmark" element={<main className="content"><Bookmark /></main>} />
        <Route path="/myprofile" element={<main className="content"><MyProfile /></main>} />
        <Route path="/board" element={<main className="content"><BoardListPage type="general" /></main>} />
        <Route path="/notice" element={<main className="content"><BoardListPage type="notice" /></main>} />
        <Route path="/boards/:id" element={<main className="content"><BoardDetailPage /></main>} />
        <Route path="/boards/new" element={<BoardCreatePage />} />
      </Routes>
    </>
  );
}

export default App;
