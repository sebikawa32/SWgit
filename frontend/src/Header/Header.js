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

  const goToNotifications = () => {
    navigate("/notifications");
  };

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
        <div className="nav-right" style={{ display: "flex", alignItems: "center", gap: "12px" }}>
          {/* 게시판 텍스트 링크 (검색창 왼쪽) */}
          <div>
            <Link
              to="/board"
              style={{
                color: "#fff",
                fontWeight: "500",
                fontSize: "16px",
                textDecoration: "none",
                cursor: "pointer",
                marginRight: "10px",
              }}
            >
              게시판
            </Link>
          </div>

          {/* 검색창 */}
          <div className="search-container" style={{ marginRight: "10px" }}>
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

          {/* 로그인/회원가입 or MY/로그아웃, 알림 버튼 별도 */}
          {isLoggedIn ? (
            <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
              {/* 알림 버튼 (MY 왼쪽에 배치) */}
              <button
                onClick={goToNotifications}
                style={{
                  background: "none",
                  border: "none",
                  color: "#fff",
                  cursor: "pointer",
                  fontSize: "20px",
                  padding: 0,
                }}
                aria-label="알림"
                type="button"
              >
                🔔
              </button>

              {/* MY 메뉴 (드롭다운) */}
              <div
                className="my-menu"
                style={{ position: "relative" }}
                onMouseEnter={() => setShowDropdown(true)}
                onMouseLeave={() => setShowDropdown(false)}
              >
                <button
                  style={{
                    background: "none",
                    border: "none",
                    color: "#fff",
                    cursor: "pointer",
                    fontSize: "16px",
                    fontWeight: "500",
                    padding: 0,
                  }}
                  type="button"
                >
                  MY
                </button>

                {showDropdown && (
                  <div
                    className="my-dropdown"
                    style={{
                      position: "absolute",
                      top: "100%",
                      right: 0,
                      backgroundColor: "#222",
                      borderRadius: "4px",
                      padding: "8px",
                      minWidth: "150px",
                      zIndex: 1000,
                      display: "flex",
                      flexDirection: "column",
                      gap: "8px",
                    }}
                  >
                    <Link to="/MyProfile" style={{ color: "#fff", textDecoration: "none" }}>
                      내 프로필
                    </Link>
                    <Link to="/Bookmark" style={{ color: "#fff", textDecoration: "none" }}>
                      즐겨찾기 목록
                    </Link>
                    <Link to="/alarm-settings" style={{ color: "#fff", textDecoration: "none" }}>
                      알람 설정
                    </Link>
                  </div>
                )}
              </div>

              {/* 로그아웃 버튼 */}
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
                type="button"
              >
                로그아웃
              </button>
            </div>
          ) : (
            <ul
              className="nav-links"
              style={{ display: "flex", alignItems: "center", gap: "12px", margin: 0, padding: 0, listStyle: "none" }}
            >
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
