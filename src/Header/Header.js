import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Header.css";

function Header() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    setIsLoggedIn(!!token);
  }, []);

  const handleSearch = () => {
    alert("검색 기능은 아직 구현되지 않았습니다.");
  };

  const handleLogout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    navigate("/");
  };

  return (
    <header className="App-header">
      <nav className="navbar">
        <div className="nav-left">
          <Link to="/" className="logo-link">
            <h1>🪐 TicketPlanet</h1>
          </Link>
        </div>

        <div className="nav-center">
          <ul className="nav-links">
            <li><Link to="/Concert">콘서트</Link></li>
            <li><Link to="/Musical">뮤지컬</Link></li>
            <li><Link to="/Theater">연극</Link></li>
            <li><Link to="/Exhibition">전시</Link></li>
          </ul>
        </div>

        <div className="nav-right">
          <div className="search-container">
            <input
              type="text"
              className="search-input"
              placeholder="검색..."
            />
            <button className="search-btn" onClick={handleSearch}>
              🔍
            </button>
          </div>
          <ul className="nav-links">
            {isLoggedIn ? (
              <>
                <li><Link to="/MyProfile">MY</Link></li>
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

// 배너 컴포넌트도 함께 내보내기
export function HomeBanner() {
  return (
    <section className="home-banner">
      <img src="/images/banner.jpg" alt="홈 배너" />
      <div className="home-banner-text">
        <h1>최신 이벤트 모음</h1>
        <p>놓치지 마세요! 특별 할인 & 인기 공연 티켓</p>
      </div>
    </section>
  );
}
