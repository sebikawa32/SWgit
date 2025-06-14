import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom'; // ✅ 추가
import './ChatSearchBoxForHome.css';

function ChatSearchBoxForHome({ onResults }) {
  const [query, setQuery] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate(); // ✅ 추가

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!query.trim()) return;

    setLoading(true);

    try {
      const response = await axios.post('/api/chat/search', {
        message: query,
      });

      console.log('✅ GPT 검색 응답 결과:', response.data);

      if (typeof onResults === 'function') {
        onResults(response.data, query);
      } else {
        // ✅ navigate로 /chat-search 경로로 이동하며, 상태 전달
        navigate('/chat-search', {
          state: {
            results: response.data,
            query: query,
          },
        });
      }
    } catch (error) {
      console.error('❌ GPT 검색 오류 (AxiosError):', error);
      if (error.response) {
        console.error('응답 상태 코드:', error.response.status);
        console.error('응답 데이터:', error.response.data);
      }
      alert('검색 중 오류가 발생했습니다. 콘솔을 확인해주세요.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <form onSubmit={handleSubmit} className="chat-search-form">
        <img
          src="/images/chat.jpeg"
          alt="챗봇 아이콘"
          className="chatbot-icon"
        />
        <input
          type="text"
          value={query}
          placeholder="챗봇 우주 냥이에게 물어봐라냥! "
          onChange={(e) => setQuery(e.target.value)}
          className="chat-search-input"
        />
        <button type="submit" className="chat-search-button" disabled={loading}>
          {loading ? '검색 중...' : '검색'}
        </button>
      </form>

      <p className="chat-search-examples">
        AI에게 원하는 공연 조건을 검색해보세요!<br />
        (예: "7월에 하는 뮤지컬 중 5만원 이하", "이번주에 하는 전시회")
      </p>
    </>
  );
}

export default ChatSearchBoxForHome;
