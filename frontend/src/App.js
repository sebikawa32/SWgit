import React, { useEffect, useState } from "react";
import { Routes, Route } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import Footer from "./Footer/Footer";
import Header from "./Header/Header";
import ScrollToTop from "./ScrollToTop"; // ✅ 추가

// ✅ 페이지 컴포넌트들
import HomePage from './Home/HomePage';
import SearchPage from './Search/SearchPage';
import LoginPage from './Login/LoginPage';
import SignupPage from './Login/SignupPage';
import AgreementPage from './Login/AgreementPage';
import ConcertPage from './Ticket/ConcertPage';
import MusicalPage from './Ticket/MusicalPage';
import PlayPage from './Ticket/PlayPage';
import ExhibitionPage from './Ticket/ExhibitionPage';
import TicketDetailPage from './Ticket/TicketDetailPage';
import Bookmark from './My/Bookmark';
import BoardListPage from './Board/BoardListPage';
import BoardDetailPage from './Board/BoardDetailPage';
import BoardCreatePage from './Board/BoardCreatePage';
import BoardEditPage from './Board/BoardEditPage';
import TicketCreatePage from './Ticket/TicketCreatePage';
import ChatSearchPage from './Chatbot/ChatSearchPage';
import ResetPasswordPage from "./Login/ResetPasswordPage";
import UserProfilePage from './My/UserProfilePage';
import AlertSettingFormPage from "./SseNotification/AlertSettingFormPage";
import './App.css';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    if (token) {
      try {
        const { exp } = jwtDecode(token);
        const now = Date.now() / 1000;
        if (exp < now) {
          localStorage.clear();
          alert("세션이 만료되었습니다. 다시 로그인해주세요.");
          window.location.href = "/login";
        } else {
          setIsLoggedIn(true);
        }
      } catch (e) {
        console.error("❌ 토큰 디코딩 실패", e);
        localStorage.clear();
      }
    }
  }, []);

  return (
    <>
      <Header isLoggedIn={isLoggedIn} />
      <ScrollToTop /> {/* ✅ 라우팅 위에 위치시켜야 정상 동작합니다 */}

      <Routes>
        <Route path="/login" element={<div className="login-page-wrapper"><LoginPage setIsLoggedIn={setIsLoggedIn} /></div>} />
        <Route path="/signup" element={<AgreementPage />} />
        <Route path="/signup/form" element={<div className="signup-page-wrapper"><SignupPage /></div>} />
        <Route path="/" element={<main className="content"><HomePage /></main>} />
        <Route path="/concerts" element={<main className="content"><ConcertPage /></main>} />
        <Route path="/musicals" element={<main className="content"><MusicalPage /></main>} />
        <Route path="/plays" element={<main className="content"><PlayPage /></main>} />
        <Route path="/exhibitions" element={<main className="content"><ExhibitionPage /></main>} />
        <Route path="/search" element={<main className="content"><SearchPage /></main>} />
        <Route path="/ticket/:id" element={<main className="content"><TicketDetailPage /></main>} />
        <Route path="/bookmark" element={<main className="content"><Bookmark /></main>} /> 
        <Route path="/board" element={<main className="content"><BoardListPage type="general" /></main>} />
        <Route path="/notice" element={<main className="content"><BoardListPage type="notice" /></main>} />
        <Route path="/boards/:id" element={<main className="content"><BoardDetailPage /></main>} />
        <Route path="/boards/new" element={<BoardCreatePage />} />
        <Route path="/board/edit/:id" element={<BoardEditPage />} />
        <Route path="/admin/tickets/new" element={<main className="content"><TicketCreatePage /></main>} />
        <Route path="/chat/search" element={<ChatSearchPage />} />
        <Route path="/alarm-settings" element={<AlertSettingFormPage />} />
        <Route path="/reset-password" element={<div className="login-page-wrapper"><ResetPasswordPage /></div>} />
        <Route path="/profile" element={<UserProfilePage />} />
      </Routes>

      <Footer />
    </>
  );
}

export default App;
