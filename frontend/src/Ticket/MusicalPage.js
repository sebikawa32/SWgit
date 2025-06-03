import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom'; // ✅ 상세페이지 이동을 위한 Link
import './Ticket.css'; // ✅ 같은 스타일 파일 재사용!

const MusicalPage = () => {
  const [tickets, setTickets] = useState([]);

  // ✅ 뮤지컬 티켓 불러오기
  useEffect(() => {
    const fetchTickets = async () => {
      try {
        const res = await axios.get('/api/tickets/category/2'); // 카테고리 ID=2 (뮤지컬)
        setTickets(res.data);
      } catch (err) {
        console.error('❌ 뮤지컬 티켓 불러오기 오류:', err);
      }
    };
    fetchTickets();
  }, []);

  return (
    <div className="concert-page">
      <h1>뮤지컬</h1>

      {/* 뮤지컬 티켓이 없을 때 */}
      {tickets.length === 0 ? (
        <p>뮤지컬 데이터가 없습니다.</p>
      ) : (
        <div className="concert-grid">
          {/* ✅ 뮤지컬 티켓 목록 */}
          {tickets.map((ticket) => (
            <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="concert-card-link">
              <div className="concert-card">
                <img src={ticket.imageUrl} alt={ticket.title} />
                <div className="concert-info">
                  <h2>{ticket.title}</h2>
                  <p>{ticket.eventDatetime}</p> {/* 공연 날짜 */}
                  <p>{ticket.venue}</p> {/* 공연 장소 */}
                  <p>{ticket.price ? `${ticket.price}원` : '무료'}</p> {/* 가격 표시 */}
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}
    </div>
  );
};

export default MusicalPage;
