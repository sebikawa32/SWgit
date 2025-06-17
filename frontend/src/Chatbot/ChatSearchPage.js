import React, { useEffect, useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import './ChatSearchPage.css';

const ChatSearchPage = () => {
  const location = useLocation();
  const navigate = useNavigate();

  const queryParams = new URLSearchParams(location.search);
  const queryFromURL = queryParams.get('query');
  const queryFromState = location.state?.query;
  const results = location.state?.results ?? null;

  const query = queryFromState || queryFromURL || '';

  const [typedText, setTypedText] = useState('');

  useEffect(() => {
    if (!query) {
      setTypedText('검색어가 없습니다냥.');
      return;
    }

    const fullText = `'${query}' 검색 결과입니다냥!`;
    let index = 0;

    const interval = setInterval(() => {
      setTypedText(fullText.slice(0, index + 1));
      index++;
      if (index >= fullText.length) clearInterval(interval);
    }, 70);

    return () => clearInterval(interval);
  }, [query]);

  return (
    <div className="chat-search-result-page">
      {/* 🐱 챗봇 캐릭터 + 말풍선 */}
      <div className="chatbot-message-wrapper">
        <img
          src="/images/chat.jpeg"
          alt="챗봇"
          className="chat-cat-image"
        />
        <div className="chat-balloon">{typedText}</div>
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
