import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Ticket.css';

const ConcertPage = () => {
  const [tickets, setTickets] = useState([]);
  const [popularConcerts, setPopularConcerts] = useState([]);

  // 날짜 직접 포맷팅 함수 (공백 없이 "YYYY.MM.DD" 형태)
  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 1~12 월
    const day = String(date.getDate()).padStart(2, '0');

    return `${year}.${month}.${day}`;
  };

  useEffect(() => {
    const fetchTickets = async () => {
      try {
        setPopularConcerts([]); // 인기콘서트 임시
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

      {/* 인기 콘서트 섹션 */}
      <section className="popular-concerts">
        <h2>Hot</h2>
        <div className="popular-concerts-grid">
          {popularConcerts.length === 0 ? (
            <p>인기 콘서트 정보 준비 중입니다.</p>
          ) : (
            popularConcerts.map((concert) => (
              <div key={concert.id} className="popular-concert-card">
                <p>{concert.title}</p>
              </div>
            ))
          )}
        </div>
      </section>

      {/* 구분선 */}
      <hr className="section-divider" />

      {/* 전체 콘서트 제목 */}
      <h1>Concert</h1>

      {/* 전체 콘서트 리스트 */}
      {tickets.length === 0 ? (
        <p>콘서트 데이터가 없습니다.</p>
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

export default ConcertPage;
