import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom'; // ✅ 상세페이지 이동을 위한 Link 컴포넌트
import './Ticket.css'; // ✅ 같은 스타일 재사용

const PlayPage = () => {
  const [tickets, setTickets] = useState([]);

  // ✅ 연극 카테고리의 티켓을 불러오는 useEffect
  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const res = await axios.get('/api/tickets/category/3'); // 카테고리 ID=3 (연극)
        setTickets(res.data);
      } catch (err) {
        console.error('❌ 연극 티켓 불러오기 오류:', err);
      }
    };
    fetchTickets();
  }, []);

  return (
    <div className="concert-page">
      <h1>연극</h1>

      {/* 연극 데이터가 없을 때 */}
      {tickets.length === 0 ? (
        <p>연극 데이터가 없습니다.</p>
      ) : (
        <div className="concert-grid">
          {/* ✅ 연극 티켓 목록 */}
          {tickets.map((ticket) => (
            <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="concert-card-link">
              <div className="concert-card">
                <img src={ticket.imageUrl} alt={ticket.title} />
                <div className="concert-info">
                  <h2>{ticket.title}</h2>
                  <p>{ticket.eventDatetime}</p> {/* 공연 일시 */}
                  <p>{ticket.venue}</p> {/* 공연 장소 */}
                  <p>{ticket.price ? `${ticket.price}원` : '무료'}</p> {/* 가격 */}
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default PlayPage;
