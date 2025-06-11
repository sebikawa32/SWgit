import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Ticket.css';

const ConcertPage = () => {
  const [tickets, setTickets] = useState([]);
  const [popularConcerts, setPopularConcerts] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 30;

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}.${month}.${day}`;
  };

  // ✅ 오늘 이후 + 정렬된 티켓 조회
  const fetchTickets = async (page) => {
    try {
      const res = await axios.get(`/api/tickets/sorted/page?categoryId=1&page=${page}&size=${pageSize}`);
      setTickets(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      console.error('❌ 콘서트 티켓 불러오기 오류:', err);
    }
  };

  const fetchPopularConcerts = async () => {
    try {
      const res = await axios.get('/api/tickets/popular');
      setPopularConcerts(res.data);
    } catch (err) {
      console.error('🔥 인기 콘서트 불러오기 오류:', err);
    }
  };

  useEffect(() => {
    fetchTickets(currentPage);
    fetchPopularConcerts();
  }, [currentPage]);

  const handlePageChange = (page) => {
    if (page >= 0 && page < totalPages) {
      setCurrentPage(page);
    }
  };

  const renderPagination = () => {
    const pages = [];
    const maxVisible = 10;
    const startPage = Math.floor(currentPage / maxVisible) * maxVisible;
    const endPage = Math.min(startPage + maxVisible, totalPages);

    if (startPage > 0) {
      pages.push(
        <button key="prev" onClick={() => handlePageChange(startPage - 1)}>
          ← 이전
        </button>
      );
    }

    for (let i = startPage; i < endPage; i++) {
      pages.push(
        <button
          key={i}
          onClick={() => handlePageChange(i)}
          className={i === currentPage ? 'active' : ''}
        >
          {i + 1}
        </button>
      );
    }

    if (endPage < totalPages) {
      pages.push(
        <button key="next" onClick={() => handlePageChange(endPage)}>
          다음 →
        </button>
      );
    }

    return pages;
  };

  return (
    <div className="concert-page">
      {/* 🔥 인기 콘서트 섹션 */}
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

      <hr className="section-divider" />

      <h1>Concert</h1>

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
                  <p>{formatDate(ticket.eventStartDatetime)} ~ {formatDate(ticket.eventEndDatetime)}</p>
                  <p>{ticket.venue}</p>
                  <p>{ticket.price}원</p>
                </div>
              </div>
            </Link>
          ))}
        </div>
      )}

      <div className="pagination">{renderPagination()}</div>
    </div>
  );
};

export default ConcertPage;
