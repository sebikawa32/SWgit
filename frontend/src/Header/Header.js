import React, { useState, useEffect, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./Header.css";

function Header({ isLoggedIn: externalIsLoggedIn }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [nickname, setNickname] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [showNotifications, setShowNotifications] = useState(false);
  const bellRef = useRef(null);

  // ✅ 드롭다운 상태 분리
  const [showDropdown, setShowDropdown] = useState(false);
  const [popularKeywords, setPopularKeywords] = useState([]);
  const [isInputFocused, setIsInputFocused] = useState(false);
  const [isInputHovered, setIsInputHovered] = useState(false);
  const [isDropdownHovered, setIsDropdownHovered] = useState(false);
  const dropdownRef = useRef();

  const navigate = useNavigate();

  // 로그인 상태 및 닉네임 초기화
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    setIsLoggedIn(!!token);
    const storedNickname = localStorage.getItem("nickname") || "";
    setNickname(storedNickname);
  }, [externalIsLoggedIn]);

  // 외부 클릭 시 알림/드롭다운 닫힘
  useEffect(() => {
    const handleClickOutside = (e) => {
      if (bellRef.current && !bellRef.current.contains(e.target)) {
        setShowNotifications(false);
      }
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target) &&
        e.target.className !== "search-input"
      ) {
        setShowDropdown(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  // 검색 처리
  const handleSearch = () => {
    if (searchQuery.trim() !== "") {
      setShowDropdown(false);
      navigate(`/search?query=${encodeURIComponent(searchQuery)}`);
    }
  };

  // 인기검색어 불러오기
  const fetchPopularKeywords = async () => {
    try {
      const res = await axios.get("http://localhost:8080/api/keywords/popular");
      setPopularKeywords(res.data || []);
    } catch (e) {
      setPopularKeywords([]);
    }
  };

  // 검색창 포커스/입력시 인기검색어 드롭다운
  const handleInputFocus = () => {
    setIsInputFocused(true);
    fetchPopularKeywords();
    setShowDropdown(true);
  };
  const handleInputBlur = () => {
    setTimeout(() => setIsInputFocused(false), 120);
  };
  const handleInputChange = (e) => {
    setSearchQuery(e.target.value);
    fetchPopularKeywords();
    setShowDropdown(true);
  };

  // 드롭다운 hover/focus 제어
  const handleInputMouseEnter = () => {
    setIsInputHovered(true);
    fetchPopularKeywords();
    setShowDropdown(true);
  };
  const handleInputMouseLeave = () => {
    setIsInputHovered(false);
  };

  const handleDropdownMouseEnter = () => setIsDropdownHovered(true);
  const handleDropdownMouseLeave = () => setIsDropdownHovered(false);

  // 인기검색어 클릭시 바로 검색
  const handleKeywordClick = (keyword) => {
    setShowDropdown(false);
    setSearchQuery(keyword);
    navigate(`/search?query=${encodeURIComponent(keyword)}`);
  };

  // 엔터로 검색
  const handleInputKeyDown = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      handleSearch();
    }
  };

  // 로그아웃 처리
  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("nickname");
    localStorage.removeItem("userId");
    setIsLoggedIn(false);
    navigate("/");
    window.location.reload();
  };

  // 알림 토글
  const toggleNotifications = () => {
    setShowNotifications((prev) => !prev);
  };

  // 드롭다운 표시 조건
  const shouldShowDropdown =
    showDropdown &&
    (isInputFocused || isInputHovered || isDropdownHovered) &&
    popularKeywords.length > 0;

  return (
    <header className="App-header">
      <nav className="navbar">
        {/* 로고 */}
        <div className="nav-left">
          <Link to="/" className="logo-link">
            <img
              src="/logo.jpeg"
              alt="TicketPlanet Logo"
              className="logo-image"
            />
          </Link>
        </div>

        {/* 중앙 메뉴 */}
        <div className="nav-center">
          <ul className="nav-links">
            <li><Link to="/concerts">콘서트</Link></li>
            <li><Link to="/musicals">뮤지컬</Link></li>
            <li><Link to="/plays">연극</Link></li>
            <li><Link to="/exhibitions">전시</Link></li>
            <li className="separator">|</li>
            <li><Link to="/notice">공지사항</Link></li>
            <li><Link to="/board">게시판</Link></li>
          </ul>
        </div>

        {/* 우측 영역 */}
        <div className="nav-right">
          {/* 검색창 */}
          <div className="search-container" style={{ position: "relative" }}>
            <input
              type="text"
              className="search-input"
              placeholder="검색..."
              value={searchQuery}
              onFocus={handleInputFocus}
              onBlur={handleInputBlur}
              onChange={handleInputChange}
              onKeyDown={handleInputKeyDown}
              onMouseEnter={handleInputMouseEnter}
              onMouseLeave={handleInputMouseLeave}
              autoComplete="off"
            />
            <button className="search-btn" onClick={handleSearch} type="button">
              🔍
            </button>
            {/* 인기검색어 드롭다운 */}
            {shouldShowDropdown && (
              <ul
                className="search-dropdown"
                ref={dropdownRef}
                onMouseEnter={handleDropdownMouseEnter}
                onMouseLeave={handleDropdownMouseLeave}
              >
                {popularKeywords.map((item, idx) => (
                  <li key={idx} onClick={() => handleKeywordClick(item.keyword)}>
                    <span style={{ fontWeight: 600, color: "#c0c0c0" }}>{idx + 1}위</span>&nbsp;
                    {item.keyword}
                  </li>
                ))}
              </ul>
            )}
          </div>

          {/* 로그인된 경우 */}
          {isLoggedIn ? (
            <>
              {/* 알림 아이콘 */}
              <div className="notification-wrapper" ref={bellRef}>
                <button onClick={toggleNotifications} className="notification-btn" type="button">
                  <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="white" className="bi bi-bell-fill" viewBox="0 0 16 16">
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
              {/* 사용자 닉네임 및 드롭다운 */}
              <div className="nickname-wrapper">
                <span className="user-greeting">{nickname}님</span>
                <div className="my-menu">
                  <button className="my-btn" type="button">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="white" className="bi bi-caret-down-fill" viewBox="0 0 16 16">
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
            // 로그인 안 된 경우
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
