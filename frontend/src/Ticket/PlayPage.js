import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Ticket.css';

const PlayPage = () => {
  const [tickets, setTickets] = useState([]);
  const [popularPlays, setPopularPlays] = useState([]);

  // 직접 포맷팅 함수 (YYYY.MM.DD)
  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 1~12월
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  };

  useEffect(() => {
    const fetchTickets = async () => {
      try {
        setPopularPlays([]); // 인기 연극 임시
        const res = await axios.get('/api/tickets/category/3'); // 연극 카테고리 ID = 3
        setTickets(res.data);
      } catch (err) {
        console.error('❌ 연극 티켓 불러오기 오류:', err);
      }
    };
    fetchTickets();
  }, []);

  return (
    <div className="concert-page">

      {/* 인기 연극 섹션 */}
      <section className="popular-concerts">
        <h2>Hot</h2>
        <div className="popular-concerts-grid">
          {popularPlays.length === 0 ? (
            <p>인기 연극 정보 준비 중입니다.</p>
          ) : (
            popularPlays.map((play) => (
              <div key={play.id} className="popular-concert-card">
                <p>{play.title}</p>
              </div>
            ))
          )}
        </div>
      </section>

      {/* 구분선 */}
      <hr className="section-divider" />

      {/* 전체 연극 제목 */}
      <h1>Play</h1>

      {/* 전체 연극 리스트 */}
      {tickets.length === 0 ? (
        <p>연극 데이터가 없습니다.</p>
      ) : (
        <div className="concert-grid">
          {tickets.map((ticket) => (
            <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="concert-card-link">
              <div className="concert-card">
                <div className="concert-card-image-wrapper">
                  <img src={ticket.imageUrl} alt={ticket.title} />
                </div>

                <div className="concert-info">
                  <h2>{ticket.title}</h2>
                  <p style={{ whiteSpace: 'nowrap' }}>
                    {formatDate(ticket.eventStartDatetime)} ~ {formatDate(ticket.eventEndDatetime)}
                  </p>
                  <p>{ticket.venue}</p>
                  {ticket.price && <p>{ticket.price}원</p>}
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
