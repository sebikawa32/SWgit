import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './SearchPage.css';

const categories = [
  { id: 0, name: '전체' },
  { id: 1, name: '콘서트' },
  { id: 4, name: '뮤지컬' },
  { id: 3, name: '연극' },
  { id: 2, name: '전시' },
];

function SearchPage() {
  const location = useLocation();
  const navigate = useNavigate();

  const apiUrl = process.env.REACT_APP_API_URL; // ✅ 환경변수로 API 주소 선언

  const queryParams = new URLSearchParams(location.search);
  const query = queryParams.get('query') || '';
  const categoryId = parseInt(queryParams.get('categoryId') || '0', 10);

  const [tickets, setTickets] = useState([]);
  const [boards, setBoards] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [popularKeywords, setPopularKeywords] = useState([]);

  useEffect(() => {
    axios.get(`${apiUrl}/keywords/popular`)
      .then(res => setPopularKeywords((res.data || []).slice(0, 6)))
      .catch(() => setPopularKeywords([]));
  }, [apiUrl]);

  useEffect(() => {
    const trimmedQuery = query.trim();
    if (!trimmedQuery) {
      setError("검색어를 입력하세요.");
      return;
    }

    setLoading(true);
    setError(null);

    const userId = localStorage.getItem("userId");
    if (userId) {
      axios.post(`${apiUrl}/search/log`, {
        userId: Number(userId),
        keyword: trimmedQuery
      }).catch(() => {});
    }

    const params = new URLSearchParams();
    params.set("query", trimmedQuery);
    if (categoryId !== 0) {
      params.set("categoryId", categoryId.toString());
    }

    axios.get(`${apiUrl}/search?${params.toString()}`)
      .then(res => {
        setTickets(res.data.tickets || []);
        setBoards(res.data.boards || []);
        setLoading(false);
      })
      .catch(() => {
        setError("검색 실패");
        setLoading(false);
      });
  }, [query, categoryId, apiUrl]);

  const handleCategoryClick = (id) => {
    const params = new URLSearchParams();
    params.set('query', query.trim());
    if (id !== 0) {
      params.set('categoryId', id.toString());
    }
    navigate(`/search?${params.toString()}`);
  };

  const handleTicketClick = (ticketId) => {
    navigate(`/ticket/${ticketId}`);
  };

  const handleBoardClick = (boardId) => {
    navigate(`/boards/${boardId}`);
  };

  return (
    <main className="content">
      {popularKeywords.length > 0 && (
        <div className="popular-keywords-wrapper">
          <div className="popular-keywords-title-row">
            <span className="popular-keywords-title">인기 검색어</span>
          </div>
          <div className="popular-keywords-grid">
            {popularKeywords.map((item, idx) => (
              <div
                className="popular-keyword"
                key={idx}
                onClick={() => navigate(`/search?query=${encodeURIComponent(item.keyword)}`)}
              >
                <span className={`popular-keyword-rank ${idx < 3 ? 'highlight-rank' : ''}`}>
                  {idx + 1}.
                </span>
                <span className="popular-keyword-text">{item.keyword}</span>
              </div>
            ))}
          </div>
        </div>
      )}

      <div className="search-summary-bar">
        <strong>"{query}"</strong> 검색 결과 ({tickets.length + boards.length}건)
      </div>

      <hr className="search-divider" />

      <div className="category-buttons">
        {categories.map(cat => (
          <button
            key={cat.id}
            className={`category-button ${categoryId === cat.id ? 'active' : ''}`}
            onClick={() => handleCategoryClick(cat.id)}
          >
            {cat.name}
          </button>
        ))}
      </div>

      {loading && (
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>검색 중입니다. 잠시만 기다려주세요...</p>
        </div>
      )}

      {error && <p style={{ color: 'red' }}>{error}</p>}

      {!loading && !error && tickets.length === 0 && boards.length === 0 && (
        <div className="no-result-box">
          <img src="/images/no-result.png" alt="결과 없음" />
          <p>조건에 맞는 검색 결과가 없어요. 다른 키워드로 검색해보세요!</p>
        </div>
      )}

      {tickets.length > 0 && (
        <>
          <h3>공연 검색 결과 <span style={{ fontWeight: 'normal' }}>({tickets.length})</span></h3>
          <div className="event-list-wrapper">
            <div className="event-list">
              {tickets
                .filter(ticket => ticket.imageUrl && ticket.imageUrl.trim() !== "")
                .map(ticket => (
                  <div
                    key={ticket.id}
                    className="event-card"
                    onClick={() => handleTicketClick(ticket.id)}
                  >
                    <img src={ticket.imageUrl} alt={ticket.title} />
                    <h3>{ticket.title}</h3>
                    <p>{ticket.eventStartDatetime?.split('T')[0]} ~ {ticket.eventEndDatetime?.split('T')[0]}</p>
                    <p>{ticket.venue}</p>
                  </div>
                ))}
            </div>
          </div>
        </>
      )}

      {boards.length > 0 && (
        <>
          <h3>게시글 검색 결과 <span style={{ fontWeight: 'normal' }}>({boards.length})</span></h3>
          <div className="board-list">
            {boards.map(board => (
              <div
                key={board.id}
                className="board-card"
                onClick={() => handleBoardClick(board.id)}
              >
                <h4>{board.title}</h4>
                <p style={{ color: '#666' }}>{board.nickname} · {board.createdAt?.split('T')[0]}</p>
              </div>
            ))}
          </div>
        </>
      )}

      {!loading && !error && tickets.length > 0 && boards.length === 0 && (
        <>
          <h3>게시글 검색 결과 <span style={{ fontWeight: 'normal' }}>(0)</span></h3>
          <p className="no-board-message">현재 게시글이 없습니다.</p>
        </>
      )}
    </main>
  );
}

export default SearchPage;
