import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';  // ✅ 링크 추가
import axios from 'axios';
import './Ticket.css';

const ConcertPage = () => {
  const [tickets, setTickets] = useState([]);

  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const res = await axios.get('/api/tickets/category/1');
        setTickets(res.data);
      } catch (err) {
        console.error('❌ 콘서트 티켓 불러오기 오류:', err);
      }
    };
    fetchTickets();
  }, []);

  return (
    <div className="concert-page">
      <h1>콘서트</h1>
      {tickets.length === 0 ? (
        <p>콘서트 데이터가 없습니다.</p>
      ) : (
        <div className="concert-grid">
          {tickets.map((ticket) => (
            // ✅ 카드 전체를 Link로 감싸기
            <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="concert-card-link">
              <div className="concert-card">
                <img src={ticket.imageUrl} alt={ticket.title} />
                <div className="concert-info">
                  <h2>{ticket.title}</h2>
                  <p>{ticket.eventDatetime}</p> {/* 필드명 확인! */}
                  <p>{ticket.venue}</p>          {/* venue */}
                  <p>{ticket.price ? `${ticket.price}원` : '무료'}</p>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default ConcertPage;

