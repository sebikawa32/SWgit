import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Ticket.css';
import HotTicketSlider from './HotTicketSlider';

const PlayPage = () => {
  const [tickets, setTickets] = useState([]);
  const [popularPlays, setPopularPlays] = useState([]);
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

  const getValidPopularPlays = () =>
    popularPlays
      .filter(play =>
        play.imageUrl &&
        play.imageUrl.trim() !== "" &&
        !failedPopularIds.includes(play.id)
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

  const fetchTickets = async (page) => {
    try {
      const res = await axios.get(`/api/tickets/sorted/page?categoryId=3&page=${page}&size=${pageSize}`);
      setTickets(res.data.content);
      setTotalPages(res.data.totalPages);
      setFailedMainIds([]);
    } catch (err) {
      console.error('❌ 연극 티켓 불러오기 오류:', err);
    }
  };

  const fetchPopularPlays = async () => {
    try {
      const res = await axios.get('/api/tickets/popular', {
        params: { categoryId: 3, size: 10 }
      });
      setPopularPlays(res.data);
      setFailedPopularIds([]);
    } catch (err) {
      console.error('🔥 인기 연극 불러오기 오류:', err);
    }
  };

  useEffect(() => {
    fetchTickets(currentPage);
    fetchPopularPlays();
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
        <HotTicketSlider tickets={getValidPopularPlays()} />
      </section>

      <hr className="section-divider" />
      <section style={{ marginBottom: '100px' }}></section>

      <h1 className="concert-title">PLAY</h1>
      <section style={{ marginBottom: '70px' }}></section>

      {getValidTickets().length === 0 ? (
        <p style={{ textAlign: "center" }}>연극 데이터가 없습니다.</p>
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

export default PlayPage;
