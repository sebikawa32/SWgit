import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Header.css";

function Header() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [nickname, setNickname] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [showNotifications, setShowNotifications] = useState(false);
  const bellRef = useRef(null);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    setIsLoggedIn(!!token);
    const storedNickname = localStorage.getItem("nickname") || "";
    setNickname(storedNickname);
  }, []);

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (bellRef.current && !bellRef.current.contains(e.target)) {
        setShowNotifications(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleSearch = () => {
    if (searchQuery.trim() !== "") {
      navigate(`/search?query=${encodeURIComponent(searchQuery)}`);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("nickname");
    setIsLoggedIn(false);
    navigate("/");
  };

  const toggleNotifications = () => {
    setShowNotifications((prev) => !prev);
  };

  return (
    <header className="App-header">
      <nav className="navbar">
        {/* ✅ 로고: 아이콘 + 텍스트 */}
       <div className="nav-left">
  <Link to="/" className="logo-link">
    <img
      src="/logo.jpeg"
      alt="TicketPlanet Logo"
      className="logo-image"
    />
  </Link>
</div>


        <div className="nav-center">
          <ul className="nav-links">
            <li><Link to="/concerts">콘서트</Link></li>
            <li><Link to="/musicals">뮤지컬</Link></li>
            <li><Link to="/plays">연극</Link></li>
            <li><Link to="/exhibitions">전시</Link></li>
            <li className="separator">|</li>
            <Link to="/notice">공지사항</Link>
            <li><Link to="/board">게시판</Link></li>
          </ul>
        </div>

        <div className="nav-right">
          <div className="search-container">
            <input
              type="text"
              className="search-input"
              placeholder="검색..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <button className="search-btn" onClick={handleSearch} type="button">
              🔍
            </button>
          </div>

          {isLoggedIn ? (
            <>
              {/* 알림 버튼 + 팝업 */}
              <div className="notification-wrapper" ref={bellRef}>
                <button onClick={toggleNotifications} className="notification-btn" type="button">
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    width="20"
                    height="20"
                    fill="white"
                    className="bi bi-bell-fill"
                    viewBox="0 0 16 16"
                  >
                    <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2m.995-14.901a1 1 0 1 0-1.99 0A5 5 0 0 0 3 6c0 1.098-.5 6-2 7h14c-1.5-1-2-5.902-2-7 0-2.42-1.72-4.44-4.005-4.901"/>
                  </svg>
                </button>
                {showNotifications && (
                  <div className="notification-popup">
                    <p>📢 새로운 공연이 등록되었습니다!</p>
                    <p>🎫 마감 임박 티켓이 있어요!</p>
                  </div>
                )}
              </div>

              {/* 닉네임 + ▼ 버튼 딱 붙게 */}
              <div className="nickname-wrapper">
                <span className="user-greeting">{nickname}님</span>
                <div className="my-menu">
                  <button className="my-btn" type="button">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="16"
                      height="16"
                      fill="white"
                      className="bi bi-caret-down-fill"
                      viewBox="0 0 16 16"
                    >
                      <path d="M7.247 11.14 2.451 5.658C1.885 5.013 2.345 4 3.204 4h9.592a1 1 0 0 1 .753 1.659l-4.796 5.48a1 1 0 0 1-1.506 0z" />
                    </svg>
                  </button>
                  <div className="my-dropdown">
                    <Link to="/MyProfile">내 프로필</Link>
                    <Link to="/Bookmark">즐겨찾기 목록</Link>
                    <Link to="/alarm-settings">알람 설정</Link>
                    <button onClick={handleLogout} type="button">로그아웃</button>
                  </div>
                </div>
              </div>
            </>
          ) : (
            <ul className="nav-links">
              <li><Link to="/login">로그인</Link></li>
              <li><Link to="/signup">회원가입</Link></li>
            </ul>
          )}
        </div>
      </nav>
    </header>
  );
}

export default Header;

