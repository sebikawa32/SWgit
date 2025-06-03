import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Header.css";

function Header() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [showDropdown, setShowDropdown] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    setIsLoggedIn(!!token);
  }, []);

  const handleSearch = () => {
    if (searchQuery.trim() !== "") {
      navigate(`/search?query=${encodeURIComponent(searchQuery)}`);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    navigate("/");
  };

  const toggleDropdown = () => setShowDropdown(!showDropdown);

  return (
    <header className="App-header">
      <nav className="navbar">
        {/* 왼쪽 로고 */}
        <div className="nav-left">
          <Link to="/" className="logo-link">
            <h1>🪐 TicketPlanet</h1>
          </Link>
        </div>

        {/* 가운데 카테고리 */}
        <div className="nav-center">
          <ul className="nav-links">
            <li><Link to="/concerts">콘서트</Link></li>
            <li><Link to="/musicals">뮤지컬</Link></li>
            <li><Link to="/plays">연극</Link></li>
            <li><Link to="/exhibitions">전시</Link></li>
          </ul>
        </div>

        {/* 오른쪽 */}
        <div className="nav-right">
          {/* 검색창 */}
          <div className="search-container">
            <input
              type="text"
              className="search-input"
              placeholder="검색..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
            />
            <button className="search-btn" onClick={handleSearch}>
              🔍
            </button>
          </div>

          {/* 로그인/회원가입 or MY/로그아웃 */}
          <ul className="nav-links">
            {isLoggedIn ? (
              <>
                <li className="my-menu">
                  <button
                    onClick={toggleDropdown}
                    style={{
                      background: "none",
                      border: "none",
                      color: "#fff",
                      cursor: "pointer",
                      fontSize: "16px",
                      fontWeight: "500",
                    }}
                  >
                    MY
                  </button>
                  {/* ✅ 조건부 렌더링으로 드롭다운 메뉴 표시 */}
                  {showDropdown && (
                    <div className="my-dropdown">
                      <Link to="/Bookmark">즐겨찾기 목록</Link>
                      <Link to="/MyProfile">내 프로필</Link>
                    </div>
                  )}
                </li>
                <li>
                  <button
                    onClick={handleLogout}
                    style={{
                      background: "none",
                      border: "none",
                      color: "#fff",
                      cursor: "pointer",
                      fontSize: "16px",
                      fontWeight: "500",
                    }}
                  >
                    로그아웃
                  </button>
                </li>
              </>
            ) : (
              <>
                <li><Link to="/login">로그인</Link></li>
                <li><Link to="/signup">회원가입</Link></li>
              </>
            )}
          </ul>
        </div>
      </nav>
    </header>
  );
}

export default Header;
