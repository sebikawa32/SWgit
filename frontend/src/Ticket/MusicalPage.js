import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Ticket.css';
import HotTicketSlider from './HotTicketSlider';

const MusicalPage = () => {
  const [tickets, setTickets] = useState([]);
  const [popularMusicals, setPopularMusicals] = useState([]);
  const [failedPopularIds, setFailedPopularIds] = useState([]);
  const [failedMainIds, setFailedMainIds] = useState([]);
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

  const getValidPopularMusicals = () =>
    popularMusicals
      .filter(musical =>
        musical.imageUrl &&
        musical.imageUrl.trim() !== "" &&
        !failedPopularIds.includes(musical.id)
      )
      .slice(0, 5);

  const getValidTickets = () =>
    tickets
      .filter(ticket =>
        ticket.imageUrl &&
        ticket.imageUrl.trim() !== "" &&
        !failedMainIds.includes(ticket.id)
      );

  const handleImageError = (ticketId, type) => {
    if (type === "popular") setFailedPopularIds(prev => [...prev, ticketId]);
    if (type === "main") setFailedMainIds(prev => [...prev, ticketId]);
  };

  // 전체 뮤지컬 리스트
  const fetchTickets = async (page) => {
    try {
      const res = await axios.get(`/tickets/sorted/page?categoryId=4&page=${page}&size=${pageSize}`);
      setTickets(res.data.content);
      setTotalPages(res.data.totalPages);
      setFailedMainIds([]);
    } catch (err) {
      console.error('❌ 뮤지컬 티켓 불러오기 오류:', err);
    }
  };

  // 인기 뮤지컬 리스트
  const fetchPopularMusicals = async () => {
    try {
      const res = await axios.get('/tickets/popular', {
        params: { categoryId: 4, size: 10 },
      });
      setPopularMusicals(res.data);
      setFailedPopularIds([]);
    } catch (err) {
      console.error('🔥 인기 뮤지컬 불러오기 오류:', err);
    }
  };

  useEffect(() => {
    fetchTickets(currentPage);
    fetchPopularMusicals();
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
      <h2 className="concert-title">WHAT'S Hot</h2>
      <section className="popular-concerts" style={{ marginBottom: '48px' }}>
        <HotTicketSlider tickets={getValidPopularMusicals()} />
      </section>

      <hr className="section-divider" />
      <section style={{ marginBottom: '100px' }}></section>
      <h1 className="concert-title">MUSICAL</h1>
      <section style={{ marginBottom: '70px' }}></section>
      {getValidTickets().length === 0 ? (
        <p style={{ textAlign: "center" }}>뮤지컬 데이터가 없습니다.</p>
      ) : (
        <div className="concert-grid">
          {getValidTickets().map((ticket) => (
            <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="concert-card-link">
              <div className="concert-card">
                <div className="concert-card-image-wrapper">
                  <img
                    src={ticket.imageUrl}
                    alt={ticket.title}
                    onError={() => handleImageError(ticket.id, "main")}
                  />
                </div>
                <div className="concert-info">
                  <h2>{ticket.title}</h2>
                  <p>{formatDate(ticket.eventStartDatetime)} ~ {formatDate(ticket.eventEndDatetime)}</p>
                  <p>{ticket.venue}</p>
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

export default MusicalPage;
