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

  const [results, setResults] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!query) return;

    setLoading(true);
    setError(null);

    let url = `http://localhost:8080/api/search?query=${query}`;
    if (categoryId !== 0) {
      url += `&categoryId=${categoryId}`;
    }

    axios.get(url)
      .then(res => {
        setResults(res.data);
        setLoading(false);
      })
      .catch(() => {
        setError('검색 실패');
        setLoading(false);
      });
  }, [query, categoryId]);

  // ✅ 카테고리 버튼 클릭 시 검색어는 유지, categoryId만 바뀜
  const handleCategoryClick = (id) => {
    const newParams = new URLSearchParams();
    newParams.set('query', query);
    if (id !== 0) {
      newParams.set('categoryId', id);
    }
    navigate(`/search?${newParams.toString()}`);
  };

  // ✅ 검색 결과 클릭 → 상세 페이지로 이동
  const handleCardClick = (ticketId) => {
    navigate(`/ticket/${ticketId}`);
  };

  return (
    <main className="content">
      <section>
        <h2>🔎 검색 결과: "{query}"</h2>

        {/* 카테고리 버튼 */}
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
        {results.length === 0 && !loading && !error && <p>검색 결과가 없습니다.</p>}

        {/* ✅ 검색 결과 카드 리스트 */}
        <div className="event-list-wrapper">
          <div className="event-list">
            {results.map(ticket => (
              <div
                key={ticket.id}
                className="event-card"
                onClick={() => handleCardClick(ticket.id)}
                style={{ cursor: "pointer" }}
              >
                <img src={ticket.imageUrl} alt={ticket.title} />
                <h3>{ticket.title}</h3>
                <p>{ticket.eventDatetime}</p>
                <p>{ticket.venue}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <Footer />
    </main>
  );
}

export default SearchPage;
