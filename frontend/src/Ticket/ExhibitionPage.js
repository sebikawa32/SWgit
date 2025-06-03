import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom'; // ✅ 상세페이지로 이동할 Link
import './Ticket.css';

const ExhibitionPage = () => {
  const [tickets, setTickets] = useState([]);

  // ✅ 전시 티켓 목록 불러오기
  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const res = await axios.get('/api/tickets/category/4'); // 전시 카테고리 ID=4
        setTickets(res.data);
      } catch (err) {
        console.error('❌ 전시 티켓 불러오기 오류:', err);
      }
    };
    fetchTickets();
  }, []);

  return (
    <div className="concert-page">
      <h1>전시</h1>

      {/* 전시 티켓이 없을 때 */}
      {tickets.length === 0 ? (
        <p>전시 데이터가 없습니다.</p>
      ) : (
        <div className="concert-grid">
          {/* ✅ 전시 티켓 목록 */}
          {tickets.map((ticket) => (
            <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="concert-card-link">
              <div className="concert-card">
                <img src={ticket.imageUrl} alt={ticket.title} />
                <div className="concert-info">
                  <h2>{ticket.title}</h2>
                  <p>{ticket.eventDatetime}</p> {/* 날짜 정보 */}
                  <p>{ticket.venue}</p> {/* 장소 정보 */}
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

export default ExhibitionPage;
