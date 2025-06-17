import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import './ChatSearchPage.css'; // 스타일 파일

const ChatSearchPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const queryParams = new URLSearchParams(location.search);
  const query = queryParams.get('query');
  const results = location.state?.results;

  return (
    <div className="chat-search-result-page">
      {/* 🔶 상단 챗봇 이미지 + 제목 */}
      <div className="chat-search-header">
        <img
          src="/images/chat.jpeg"
          alt="챗봇"
        />
        <span className="chat-search-title">
          '{query}' 검색 결과입니다냥
        </span>
      </div>

      {/* 🔶 검색 결과 목록 */}
      {results ? (
        results.length > 0 ? (
          <div className="chat-search-grid">
            {results.map((item, index) => (
              <div
                key={index}
                className="chat-search-card"
                onClick={() => navigate(`/ticket/${item.id}`)}
              >
                <div className="concert-card-image-wrapper">
                  <img
                    src={item.imageUrl || '/images/no-image.jpg'}
                    alt={item.title}
                  />
                </div>
                <div className="concert-info">
                  <h2>{item.title}</h2>
                  <p>{item.venue}</p>
                  <p>
                    {item.eventStartDatetime?.split('T')[0]} ~ {item.eventEndDatetime?.split('T')[0]}
                  </p>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <p style={{ textAlign: 'center' }}>검색 결과가 없습니다냥.</p>
        )
      ) : (
        <p style={{ textAlign: 'center' }}>검색 결과를 불러올 수 없습니다냥.</p>
      )}
    </div>
  );
};

export default ChatSearchPage;
