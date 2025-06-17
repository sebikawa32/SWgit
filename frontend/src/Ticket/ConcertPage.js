import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Ticket.css';

const ConcertPage = () => {
  const [tickets, setTickets] = useState([]);
  const [popularConcerts, setPopularConcerts] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 30;

  // 🔥 이미지 깨진 인기콘서트 id 저장용
  const [failedPopularIds, setFailedPopularIds] = useState([]);
  // 인기콘서트 렌더링 필터
  const getValidPopularConcerts = () =>
    popularConcerts
      .filter(concert =>
        concert.imageUrl &&
        concert.imageUrl.trim() !== "" &&
        !failedPopularIds.includes(concert.id)
      )
      .slice(0, 5);

  // 🔥 이미지 깨진 티켓 id 저장용 (메인)
  const [failedMainIds, setFailedMainIds] = useState([]);
  // 메인 티켓 렌더링 필터
  const getValidTickets = () =>
    tickets
      .filter(ticket =>
        ticket.imageUrl &&
        ticket.imageUrl.trim() !== "" &&
        !failedMainIds.includes(ticket.id)
      );

  // 이미지 에러 핸들러
  const handleImageError = (ticketId, type) => {
    if (type === "popular") {
      setFailedPopularIds(prev => [...prev, ticketId]);
    }
    if (type === "main") {
      setFailedMainIds(prev => [...prev, ticketId]);
    }
  };

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
      // 5개만 말고 8~10개 받아오면 더 안전!
      const res = await axios.get('/api/tickets/popular', {
        params: { categoryId: 1, size: 10 },
      });
      setPopularConcerts(res.data);
    } catch (err) {
      console.error('🔥 인기 콘서트 불러오기 오류:', err);
    }
  };

  useEffect(() => {
    fetchTickets(currentPage);
    fetchPopularConcerts();
    setFailedPopularIds([]); // 페이지 바뀌면 실패 리스트 리셋
    setFailedMainIds([]);
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
          {getValidPopularConcerts().length === 0 ? (
            <p>인기 콘서트 정보 준비 중입니다.</p>
          ) : (
            getValidPopularConcerts().map((concert, index) => (
              <Link to={`/ticket/${concert.id}`} key={concert.id} className="concert-card-link">
                <div className="concert-card">
                  <div className="ranking-badge">{`${index + 1}위`}</div>
                  <div className="concert-card-image-wrapper">
                    <img
                      src={concert.imageUrl}
                      alt={concert.title}
                      onError={() => handleImageError(concert.id, "popular")}
                    />
                  </div>
                  <div className="concert-info">
                    <h2>{concert.title}</h2>
                    <p>{formatDate(concert.eventStartDatetime)} ~ {formatDate(concert.eventEndDatetime)}</p>
                    <p>{concert.venue}</p>
                  </div>
                </div>
              </Link>
            ))
          )}
        </div>
      </section>

      <hr className="section-divider" />

      <h1>Concert</h1>

      {getValidTickets().length === 0 ? (
        <p>콘서트 데이터가 없습니다.</p>
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

export default ConcertPage;
