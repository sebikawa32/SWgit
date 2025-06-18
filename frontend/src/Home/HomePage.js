import React, { useState, useEffect, useRef } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import '../Header/Header.css';
import ChatSearchBoxForHome from '../Chatbot/ChatSearchBoxForHome';
import './HomePage.css';

const categories = [
  { id: 1, name: '콘서트' },
  { id: 4, name: '뮤지컬' },
  { id: 3, name: '연극' },
  { id: 2, name: '전시' },
];

const rankingCategories = [
  { id: 1, name: '콘서트' },
  { id: 4, name: '뮤지컬' },
  { id: 2, name: '전시' },
  { id: 3, name: '연극' },
];

const bannerImages = [
  '/images/banner1.jpeg',
  '/images/banner2.jpeg',
  '/images/banner3.jpeg',
  '/images/banner4.jpeg',
  '/images/banner5.png',
  '/images/banner6.png',
  '/images/banner7.png',
];

const bannerLinks = [
  '/ticket/32603', '/ticket/39020', '/ticket/33136',
  '/ticket/32901', '/ticket/61986', '/ticket/61987', '/ticket/61985',
];

const RADIUS = 43;
const CIRCLE_LENGTH = 2 * Math.PI * RADIUS;

// 오프닝 슬라이더 카드 크기와 gap
const cardWidth = 220; // px
const cardHeight = 380; // px (기존 340에서 380으로 변경)
const cardsPerView = 5;
const cardGap = 24; // px

function OpeningCard({ ticket, percent, getDDay, formatDateRange }) {
  const [offset, setOffset] = useState(CIRCLE_LENGTH);
  const ref = useRef();

  useEffect(() => {
    const el = ref.current;
    let timeout;
    let observer;
    function handleEntry([entry]) {
      if (entry.isIntersecting) {
        setOffset(CIRCLE_LENGTH);
        timeout = setTimeout(() => {
          setOffset(CIRCLE_LENGTH - (CIRCLE_LENGTH * percent / 100));
        }, 80);
      } else {
        setOffset(CIRCLE_LENGTH);
      }
    }
    observer = new window.IntersectionObserver(handleEntry, { threshold: 0.5 });
    if (el) observer.observe(el);
    return () => {
      if (observer) observer.disconnect();
      if (timeout) clearTimeout(timeout);
    };
  }, [percent]);

  return (
    <div
      className="event-card"
      ref={ref}
      style={{
        width: cardWidth,
        height: cardHeight, // 변경된 높이 적용
        position: 'relative',
        borderRadius: 12,
        boxShadow: '0 4px 18px rgba(0,0,0,0.08)',
        background: '#fff',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center'
      }}
      data-id={ticket.id}
    >
      <div
        className="dday-circle-wrapper"
        style={{ width: 76, height: 76, top: 65 }}
      >
        <svg className="dday-svg-circle" width="76" height="76" viewBox="0 0 100 100">
          <circle
            cx="50"
            cy="50"
            r={RADIUS}
            fill="rgba(0,0,0,0.65)"
            stroke="none"
          />
          <circle
            cx="50"
            cy="50"
            r={RADIUS}
            className="progress-animate"
            style={{
              stroke: 'orange',
              strokeWidth: 8,
              strokeDasharray: CIRCLE_LENGTH,
              strokeDashoffset: offset,
              transition: 'stroke-dashoffset 1.1s cubic-bezier(.72,0,.31,1)'
            }}
            fill="none"
          />
        </svg>
        <div className="dday-text-overlay" style={{ fontSize: 19 }}>{getDDay(ticket.eventStartDatetime)}</div>
      </div>
      <img
        src={ticket.imageUrl}
        alt={ticket.title}
        style={{
          width: '100%',
          height: 300, // 기존 260에서 300으로 변경
          objectFit: 'cover',
          borderRadius: '0 0 12px 12px'
        }}
      />
      <div className="card-title" style={{ textAlign: 'center', padding: '6px 8px 0' }}>
        <h3 style={{
          fontSize: '1.04rem',
          fontWeight: 600,
          margin: 0,
          overflow: 'hidden',
          textOverflow: 'ellipsis',
          whiteSpace: 'nowrap'
        }}>{ticket.title}</h3>
      </div>
      <div className="card-info" style={{ textAlign: 'center', padding: '0 8px' }}>
        <p style={{ fontSize: '0.95rem', color: '#555', margin: '2px 0', whiteSpace: 'nowrap' }}>{formatDateRange(ticket.eventStartDatetime, ticket.eventEndDatetime)}</p>
        <p style={{ fontSize: '0.95rem', color: '#555', margin: '2px 0', whiteSpace: 'nowrap' }}>{ticket.venue}</p>
      </div>
    </div>
  );
}

const HomePage = () => {
  const navigate = useNavigate();
  const [allTickets, setAllTickets] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState(1);
  const [loadingAll, setLoadingAll] = useState(false);
  const [errorAll, setErrorAll] = useState(null);
  const [selectedRankingCategory, setSelectedRankingCategory] = useState(1);
  const [rankingTickets, setRankingTickets] = useState([]);
  const [comingSoonTickets, setComingSoonTickets] = useState([]);
  const [currentBannerIndex, setCurrentBannerIndex] = useState(0);

  // OPENING 슬라이더
  const [openingIndex, setOpeningIndex] = useState(0);
  const [openingAnimating, setOpeningAnimating] = useState(false);
  const slideRef = useRef(null);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentBannerIndex((prev) => (prev + 1) % bannerImages.length);
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    setLoadingAll(true);
    setErrorAll(null);
    axios.get(`http://localhost:8080/api/tickets/category/${selectedCategoryId}`)
      .then(res => {
        const filtered = res.data.filter(t => t.imageUrl && t.imageUrl.trim() !== '').slice(0, 10);
        setAllTickets(filtered);
        setLoadingAll(false);
      })
      .catch(() => {
        setErrorAll('티켓 목록 불러오기 실패');
        setLoadingAll(false);
      });
  }, [selectedCategoryId]);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/tickets/popular?categoryId=${selectedRankingCategory}&size=15`)
      .then(res => {
        setRankingTickets(res.data.filter(t => t.imageUrl && t.imageUrl.trim() !== '').slice(0, 5));
      })
      .catch(err => console.error("🔥 인기 티켓 불러오기 실패:", err));
  }, [selectedRankingCategory]);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/tickets/deadline`)
      .then(res => setComingSoonTickets(res.data.filter(t => t.imageUrl && t.imageUrl.trim() !== '').slice(0, 15)))
      .catch(err => console.error("⏳ Coming Soon 티켓 불러오기 실패:", err));
  }, []);

  // 날짜 포맷 함수
  const formatDateRange = (start, end) => {
    const format = (dateStr) => {
      if (!dateStr) return '';
      const date = new Date(dateStr);
      const year = String(date.getFullYear()).slice(2);
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}.${month}.${day}`;
    };
    return `${format(start)}~${format(end)}`;
  };

  const getDDay = (startDate) => {
    if (!startDate) return null;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const start = new Date(startDate);
    start.setHours(0, 0, 0, 0);
    const diffTime = start - today;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    return diffDays === 0 ? 'Today' : `D-${diffDays}`;
  };

  const getDDayPercent = (startDate) => {
    if (!startDate) return 0;
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const start = new Date(startDate);
    start.setHours(0, 0, 0, 0);
    const diffDays = Math.ceil((start - today) / (1000 * 60 * 60 * 24));
    if (diffDays <= 0) return 100;
    if (diffDays >= 10) return 10;
    return 100 - diffDays * 10;
  };

  // 슬라이드 최대 인덱스
  const maxIndex = Math.max(0, comingSoonTickets.length - cardsPerView);

  // 슬라이드 이동
  const handlePrev = () => {
    if (openingAnimating || openingIndex === 0) return;
    setOpeningAnimating(true);
    setOpeningIndex(idx => Math.max(0, idx - 1));
  };
  const handleNext = () => {
    if (openingAnimating || openingIndex === maxIndex) return;
    setOpeningAnimating(true);
    setOpeningIndex(idx => Math.min(maxIndex, idx + 1));
  };

  // 트랜지션 처리 (카드 1장만큼 이동)
  useEffect(() => {
    if (slideRef.current) {
      slideRef.current.style.transition = 'transform 0.5s cubic-bezier(0.55, 0, 0.1, 1)';
      slideRef.current.style.transform = `translateX(-${openingIndex * (cardWidth + cardGap)}px)`;
    }
  }, [openingIndex]);

  // transitionend 이벤트로 애니 락 해제
  useEffect(() => {
    const ref = slideRef.current;
    if (!ref) return;
    const handleTransitionEnd = () => setOpeningAnimating(false);
    ref.addEventListener('transitionend', handleTransitionEnd);
    return () => {
      ref.removeEventListener('transitionend', handleTransitionEnd);
    };
  }, []);

  useEffect(() => {
    setOpeningIndex(0);
  }, [comingSoonTickets.length]);

  return (
    <>
      <section className="banner-slider">
        <div className="banner-container">
          {bannerImages.map((src, index) => {
            const link = bannerLinks[index];
            const image = (
              <img key={index} src={src} alt={`배너${index + 1}`} className={`banner-image ${index === currentBannerIndex ? 'active' : ''}`} />
            );
            return link ? <Link key={index} to={link}>{image}</Link> : <React.Fragment key={index}>{image}</React.Fragment>;
          })}
        </div>
      </section>

      <main className="content">
        <h2>AI SEARCH</h2>
        <ChatSearchBoxForHome onResults={(data, query) => {
          if (!data) return;
          const encoded = encodeURIComponent(query);
          navigate(`/chat/search?query=${encoded}`, { state: { results: data } });
        }} />

        <hr style={{ margin: '50px 0' }} />

        <section>
          <h2>RANKING</h2>
          <section style={{ marginBottom: '50px' }}></section>
          <div className="category-buttons">
            {rankingCategories.map(cat => (
              <button key={cat.id} className={`category-button ${selectedRankingCategory === cat.id ? 'active' : ''}`} onClick={() => setSelectedRankingCategory(cat.id)}>
                {cat.name}
              </button>
            ))}
          </div>
          <div className="ranking-list">
            {rankingTickets.map((ticket, index) => (
              <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="event-card-link">
                <div className="event-card">
                  <div className="ranking-badge">{`${index + 1}위`}</div>
                  <img src={ticket.imageUrl} alt={ticket.title} />
                  <div className="card-title"><h3>{ticket.title}</h3></div>
                  <div className="card-info">
                    <p>{formatDateRange(ticket.eventStartDatetime, ticket.eventEndDatetime)}</p>
                    <p>{ticket.venue}</p>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </section>

        <hr style={{ margin: '50px 0' }} />

        {/* OPENING 슬라이드 섹션 */}
        <section>
          <h2>OPENING</h2>
          <section style={{ marginBottom: '50px' }}></section>
          <div className="opening-multi-slider-wrapper" style={{ justifyContent: 'center' }}>
            <button
              onClick={handlePrev}
              disabled={openingIndex === 0 || openingAnimating}
              className="slider-arrow prev"
            >&#60;</button>
            <div
              className="opening-multi-slider-view"
              style={{
                width: cardWidth * cardsPerView + cardGap * (cardsPerView - 1),
                overflow: 'hidden',
                position: 'relative',
                borderRadius: 16,
                background: '#fff',
              }}
            >
              <div
                className="opening-multi-slider-track"
                ref={slideRef}
                style={{
                  width: comingSoonTickets.length * cardWidth + (comingSoonTickets.length - 1) * cardGap,
                  display: 'flex',
                  gap: `${cardGap}px`
                }}
              >
                {comingSoonTickets.map((ticket, idx) => (
                  <Link
                    to={`/ticket/${ticket.id}`}
                    key={ticket.id}
                    className="event-card-link"
                    style={{
                      minWidth: cardWidth,
                      maxWidth: cardWidth,
                      flex: `0 0 ${cardWidth}px`,
                      margin: 0,
                    }}
                  >
                    <OpeningCard
                      ticket={ticket}
                      percent={getDDayPercent(ticket.eventStartDatetime)}
                      getDDay={getDDay}
                      formatDateRange={formatDateRange}
                    />
                  </Link>
                ))}
              </div>
            </div>
            <button
              onClick={handleNext}
              disabled={openingIndex === maxIndex || openingAnimating}
              className="slider-arrow next"
            >&#62;</button>
          </div>
        </section>

        <hr style={{ margin: '50px 0' }} />

        <section>
          <h2>TICKETS</h2>
          <section style={{ marginBottom: '50px' }}></section>
          <div className="category-buttons">
            {categories.map(cat => (
              <button key={cat.id} className={`category-button ${selectedCategoryId === cat.id ? 'active' : ''}`} onClick={() => setSelectedCategoryId(cat.id)}>
                {cat.name}
              </button>
            ))}
          </div>
          {loadingAll && <p>로딩 중...</p>}
          {errorAll && <p style={{ color: 'red' }}>{errorAll}</p>}
          <div className="ticket-grid">
            {[0, 1].map(row => (
              <div key={row} className="ticket-row">
                {allTickets.slice(row * 5, row * 5 + 5).map(ticket => (
                  <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="event-card-link">
                    <div className="event-card">
                      <img src={ticket.imageUrl} alt={ticket.title} />
                      <div className="card-title"><h3>{ticket.title}</h3></div>
                      <div className="card-info">
                        <p>{formatDateRange(ticket.eventStartDatetime, ticket.eventEndDatetime)}</p>
                        <p>{ticket.venue}</p>
                      </div>
                    </div>
                  </Link>
                ))}
              </div>
            ))}
          </div>
        </section>
      </main>
    </>
  );
};

export default HomePage;
