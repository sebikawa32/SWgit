import React, { useEffect, useState, useRef } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import "./Header.css";
import NotificationBell from "../SseNotification/NotificationBell";

function Header({ isLoggedIn: externalIsLoggedIn }) {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [nickname, setNickname] = useState("");
  const [searchQuery, setSearchQuery] = useState("");
  const [showNotifications, setShowNotifications] = useState(false);
  const bellRef = useRef(null);

  const [showDropdown, setShowDropdown] = useState(false);
  const [popularKeywords, setPopularKeywords] = useState([]);
  const [isInputFocused, setIsInputFocused] = useState(false);
  const [isInputHovered, setIsInputHovered] = useState(false);
  const [isDropdownHovered, setIsDropdownHovered] = useState(false);
  const dropdownRef = useRef();

  const [isMyMenuActive, setIsMyMenuActive] = useState(false);
  const myMenuRef = useRef(null);

  const navigate = useNavigate();
  const storedUserId = Number(localStorage.getItem("userId"));

  const apiUrl = process.env.REACT_APP_API_URL; // ✅ 환경변수 선언

  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    setIsLoggedIn(!!token);

    if (token) {
      axios
        .get("/users/me", {
          headers: { Authorization: `Bearer ${token}` },
        })
        .then((res) => {
          const newNickname = res.data?.data?.nickname;
          setNickname(newNickname);
          localStorage.setItem("nickname", newNickname);
        })
        .catch((err) => {
          console.error("닉네임 조회 실패:", err);
          setNickname("");
        });
    }
  }, [externalIsLoggedIn]);

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
      if (myMenuRef.current && !myMenuRef.current.contains(e.target)) {
        setIsMyMenuActive(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleSearch = () => {
    if (searchQuery.trim() !== "") {
      setShowDropdown(false);
      navigate(`/search?query=${encodeURIComponent(searchQuery)}`);
    }
  };

  const fetchPopularKeywords = async () => {
    try {
      const res = await axios.get('/keywords/popular');
      setPopularKeywords(res.data || []);
    } catch (e) {
      setPopularKeywords([]);
    }
  };

  const handleInputFocus = () => {
    setIsInputFocused(true);
    fetchPopularKeywords();
    setShowDropdown(true);
  };
  const handleInputBlur = () => setTimeout(() => setIsInputFocused(false), 120);
  const handleInputChange = (e) => {
    setSearchQuery(e.target.value);
    fetchPopularKeywords();
    setShowDropdown(true);
  };
  const handleInputMouseEnter = () => {
    setIsInputHovered(true);
    fetchPopularKeywords();
    setShowDropdown(true);
  };
  const handleInputMouseLeave = () => setIsInputHovered(false);
  const handleDropdownMouseEnter = () => setIsDropdownHovered(true);
  const handleDropdownMouseLeave = () => setIsDropdownHovered(false);

  const handleKeywordClick = (keyword) => {
    setShowDropdown(false);
    setSearchQuery(keyword);
    navigate(`/search?query=${encodeURIComponent(keyword)}`);
  };

  const handleInputKeyDown = (e) => {
    if (e.key === "Enter") {
      e.preventDefault();
      handleSearch();
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("nickname");
    localStorage.removeItem("userId");
    setIsLoggedIn(false);
    navigate("/");
    window.location.reload();
  };

  const shouldShowDropdown =
    showDropdown &&
    (isInputFocused || isInputHovered || isDropdownHovered) &&
    popularKeywords.length > 0;

  // 내 메뉴 관련 이벤트
  const handleMyBtnClick = () => {
    setIsMyMenuActive((prev) => !prev);
  };
  const handleMyMenuMouseEnter = () => {
    setIsMyMenuActive(true);
  };
  const handleMyMenuMouseLeave = () => {
    setTimeout(() => {
      setIsMyMenuActive(false);
    }, 200);
  };

  return (
    <header className="App-header">
      <nav className="navbar">
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
            <li>
              <Link to="/concerts">콘서트</Link>
            </li>
            <li>
              <Link to="/musicals">뮤지컬</Link>
            </li>
            <li>
              <Link to="/plays">연극</Link>
            </li>
            <li>
              <Link to="/exhibitions">전시</Link>
            </li>
            <li className="separator">|</li>
            <li>
              <Link to="/notice">공지사항</Link>
            </li>
            <li>
              <Link to="/board">게시판</Link>
            </li>
          </ul>
        </div>

        <div className="nav-right">
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
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="currentColor"
                className="bi bi-search"
                viewBox="0 0 16 16"
              >
                <path d="M11.742 10.344a6.5 6.5 0 1 0-1.397 1.398h-.001q.044.06.098.115l3.85 3.85a1 1 0 0 0 1.415-1.414l-3.85-3.85a1 1 0 0 0-.115-.1zM12 6.5a5.5 5.5 0 1 1-11 0 5.5 5.5 0 0 1 11 0" />
              </svg>
            </button>
            {shouldShowDropdown && (
              <ul
                className="search-dropdown"
                ref={dropdownRef}
                onMouseEnter={handleDropdownMouseEnter}
                onMouseLeave={handleDropdownMouseLeave}
              >
                {popularKeywords.map((item, idx) => (
                  <li key={idx} onClick={() => handleKeywordClick(item.keyword)}>
                    <span style={{ fontWeight: 600, color: "#c0c0c0" }}>
                      {idx + 1}위
                    </span>
                    &nbsp;{item.keyword}
                  </li>
                ))}
              </ul>
            )}
          </div>

          {isLoggedIn ? (
            <>
              <div className="notification-wrapper" ref={bellRef}>
                <NotificationBell userId={storedUserId} />
              </div>
              <div
                className="nickname-wrapper"
                ref={myMenuRef}
                onMouseEnter={handleMyMenuMouseEnter}
                onMouseLeave={handleMyMenuMouseLeave}
              >
                <span className="user-greeting">{nickname}님</span>
                <div className={`my-menu ${isMyMenuActive ? "active" : ""}`}>
                  <button className="my-btn" type="button" onClick={handleMyBtnClick}>
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
                    <Link to="/profile">내 프로필</Link>
                    <Link to="/Bookmark">즐겨찾기 목록</Link>
                    <Link to={`/alarm-settings?userId=${storedUserId}`}>
                      알림 설정
                    </Link>
                    <button onClick={handleLogout} type="button">
                      로그아웃
                    </button>
                  </div>
                </div>
              </div>
            </>
          ) : (
            <ul className="nav-links">
              <li>
                <Link to="/login">로그인</Link>
              </li>
              <li>
                <Link to="/signup">회원가입</Link>
              </li>
            </ul>
          )}
        </div>
      </nav>
    </header>
  );
}

export default Header;
