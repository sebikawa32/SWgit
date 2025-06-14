import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';

const ChatSearchPage = () => {
  const location = useLocation();
  const navigate = useNavigate();

  // 🔍 쿼리 파라미터에서 검색어 추출
  const queryParams = new URLSearchParams(location.search);
  const query = queryParams.get('query') || '';

  // 📦 상태 기반 검색 결과
  const results = location.state?.results;

  // 💡 상태 없이 접근 시 홈으로 리디렉션
  if (!results) {
    return (
      <div style={{ padding: '2rem' }}>
        <h2>⚠️ 검색 결과를 불러올 수 없습니다.</h2>
        <p>페이지를 새로고침했거나, 검색 결과가 만료되었을 수 있습니다.</p>
        <button onClick={() => navigate('/')} style={{ marginTop: '1rem' }}>
          홈으로 이동
        </button>
      </div>
    );
  }

  return (
    <div className="chat-search-result-page" style={{ padding: '2rem' }}>
      <h2 style={{ marginBottom: '1.5rem' }}>🔍 '{query}' 검색 결과</h2>

      {results.length > 0 ? (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {results.map((item, index) => (
            <li
              key={index}
              style={{
                marginBottom: '1.5rem',
                padding: '1rem',
                border: '1px solid #ccc',
                borderRadius: '8px',
              }}
            >
              <h3 style={{ marginBottom: '0.5rem' }}>{item.ticket_title}</h3>
              <p>장소: {item.ticket_venue}</p>
              <p>가격: {item.ticket_price}</p>
              <p>일정: {item.ticket_event_start_datetime} ~ {item.ticket_event_end_datetime}</p>
              <a href={item.ticket_booking_link} target="_blank" rel="noopener noreferrer">
                예매하기
              </a>
            </li>
          ))}
        </ul>
      ) : (
        <p>검색 결과가 없습니다.</p>
      )}
    </div>
  );
};

export default ChatSearchPage;
