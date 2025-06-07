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
import MyProfile from "./My/Myprofile";
import BoardListPage from './Board/BoardListPage';
import BoardDetailPage from './Board/BoardDetailPage';
import BoardCreatePage  from './Board/BoardCreatePage';

import './App.css';

function App() {
  return (
    <>
      {/* 공통 헤더: 모든 페이지에 표시 */}
      <Header />

      {/* 라우터: 경로별 컴포넌트 매핑 */}
      <Routes>
        {/* 로그인 페이지 */}
        <Route path="/login" element={<div className="login-page-wrapper"><LoginPage /></div>} />
       
      
        {/* 회원가입 페이지 */}
        <Route path="/signup" element={<div className="signup-page-wrapper"><SignupPage /></div>} />
       
      
        {/* 홈 페이지 */}
        <Route path="/" element={<main className="content"><HomePage /></main>} />
       
      
        {/* 공연 카테고리별 페이지 */}
        <Route path="/concerts" element={<main className="content"><ConcertPage /></main>} />
        <Route path="/musicals" element={<main className="content"><MusicalPage /></main>} />
        <Route path="/plays" element={<main className="content"><PlayPage /></main>} />
        <Route path="/exhibitions" element={<main className="content"><ExhibitionPage /></main>} />
       
      
        {/* 검색 페이지 */}
        <Route path="/search" element={<main className="content"><SearchPage /></main>} />
       
      
        {/* 티켓 상세 페이지 (동적 경로) */}
       <Route path="/ticket/:id" element={<main className="content"><TicketDetailPage /></main>} />

      
        {/* 즐겨찾기 페이지 */}
        <Route path="/bookmark" element={<main className="content"><Bookmark /></main>} />
       
      
        {/* 내 프로필 페이지 */}
        <Route path="/myprofile" element={<main className="content"><MyProfile /></main>} />
    
       {/* 게시판 리스트 */}
<Route path="/board" element={<main className="content"><BoardListPage type="general" /></main>} />

{/* 공지사항 리스트 */}
<Route path="/notice" element={<main className="content"><BoardListPage type="notice" /></main>} />


        {/* 게시판 상세 (동적 경로) */}
        <Route path="/boards/:id" element={<main className="content"><BoardDetailPage /></main>} />
       
       <Route path="/boards/new" element={<BoardCreatePage />} />

      </Routes>
    </>
  );
}

export default App;
