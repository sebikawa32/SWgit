import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Footer from '../Footer/Footer';
import '../Header/Header.css';
import './SearchPage.css';

const categories = [
  { id: 0, name: '전체' },
  { id: 1, name: '콘서트' },
  { id: 2, name: '뮤지컬' },
  { id: 3, name: '연극' },
  { id: 4, name: '전시' },
];

function SearchPage() {
  const location = useLocation();
  const navigate = useNavigate();

  const queryParams = new URLSearchParams(location.search);
  const query = queryParams.get('query') || '';
  const categoryId = parseInt(queryParams.get('categoryId') || '0', 10);

  const [tickets, setTickets] = useState([]);
  const [boards, setBoards] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const trimmedQuery = query.trim();
    if (!trimmedQuery) {
      setError("검색어를 입력하세요.");
      return;
    }

    setLoading(true);
    setError(null);

    const params = new URLSearchParams();
    params.set("query", trimmedQuery);
    if (categoryId !== 0) {
      params.set("categoryId", categoryId.toString());
    }

    const url = `http://localhost:8080/api/search?${params.toString()}`;
    console.log("📡 요청 URL:", url);

    axios.get(url)
      .then(res => {
        console.log("✅ 응답:", res.data);
        setTickets(res.data.tickets || []);
        setBoards(res.data.boards || []);
        setLoading(false);
      })
      .catch(err => {
        console.error("❌ 검색 실패:", err);
        setError("검색 실패");
        setLoading(false);
      });
  }, [query, categoryId]);

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

  // 여기 boardId를 받아서 이동
 const handleBoardClick = (boardId) => {
  navigate(`/boards/${boardId}`);
};

  return (
    <main className="content">
      <section>
        <h2>🔎 검색 결과: "{query}"</h2>

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

        {loading && <p>로딩 중...</p>}
        {error && <p style={{ color: 'red' }}>{error}</p>}
        {!loading && !error && tickets.length === 0 && boards.length === 0 && (
          <p>검색 결과가 없습니다.</p>
        )}

        {tickets.length > 0 && (
          <>
            <h3>🎫 공연 검색 결과</h3>
            <div className="event-list-wrapper">
              <div className="event-list">
                {tickets.map(ticket => (
                  <div
                    key={ticket.ticketId}
                    className="event-card"
                    onClick={() => handleTicketClick(ticket.ticketId)}
                    style={{ cursor: "pointer" }}
                  >
                    <img src={ticket.imageUrl} alt={ticket.title} />
                    <h3>{ticket.title}</h3>
                    <p>{ticket.eventStartDatetime} ~ {ticket.eventEndDatetime}</p>
                    <p>{ticket.venue}</p>
                  </div>
                ))}
              </div>
            </div>
          </>
        )}

        {boards.length > 0 && (
          <>
            <h3>📝 게시글 검색 결과</h3>
            <div className="board-list">
              {boards.map(board => (
                <div
                  key={board.id}
                  className="board-card"
                  onClick={() => handleBoardClick(board.id)}
                  style={{ borderBottom: '1px solid #ccc', padding: '10px', cursor: 'pointer' }}
                >
                  <h4>{board.title}</h4>
                  <p style={{ color: '#666' }}>{board.nickname} · {board.createdAt}</p>
                </div>
              ))}
            </div>
          </>
        )}

        {!loading && !error && tickets.length > 0 && boards.length === 0 && (
          <>
            <h3>📝 게시글 검색 결과</h3>
            <p>현재 게시글이 없습니다.</p>
          </>
        )}
      </section>

      <Footer />
    </main>
  );
}

export default SearchPage;
