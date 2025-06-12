import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import Footer from '../Footer/Footer';
import '../Header/Header.css';
import './HomePage.css';

const categories = [
  { id: 0, name: '전체' },
  { id: 1, name: '콘서트' },
  { id: 4, name: '뮤지컬' },
  { id: 3, name: '연극' },
  { id: 2, name: '전시' },
];

const rankingCategories = [
  { id: 1, name: '콘서트' },
  { id: 2, name: '전시' },
  { id: 3, name: '연극' },
  { id: 4, name: '뮤지컬' },
];

const HomePage = () => {
  const [allTickets, setAllTickets] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState(0);
  const [loadingAll, setLoadingAll] = useState(false);
  const [errorAll, setErrorAll] = useState(null);
  const [allPageIndex, setAllPageIndex] = useState(0);

  const [deadlineTickets, setDeadlineTickets] = useState([]);
  const [loadingDeadline, setLoadingDeadline] = useState(false);
  const [errorDeadline, setErrorDeadline] = useState(null);

  const [selectedRankingCategory, setSelectedRankingCategory] = useState(1);
  const [rankingTickets, setRankingTickets] = useState([]);

  const bannerImages = [
    '/images/banner1.jpeg',
    '/images/banner2.jpeg',
    '/images/banner3.jpeg',
  ];
  const [currentBannerIndex, setCurrentBannerIndex] = useState(0);

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentBannerIndex((prev) => (prev + 1) % bannerImages.length);
    }, 5000);
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    setLoadingAll(true);
    setErrorAll(null);
    setAllPageIndex(0);

    let url = 'http://localhost:8080/api/tickets';
    if (selectedCategoryId !== 0) {
      url = `http://localhost:8080/api/tickets/category/${selectedCategoryId}`;
    }

    axios.get(url)
      .then(res => {
        const sorted = res.data.sort(
          (a, b) => new Date(b.ticket_created_at) - new Date(a.ticket_created_at)
        );
        setAllTickets(sorted);
        setLoadingAll(false);
      })
      .catch(() => {
        setErrorAll('전체 티켓 목록 불러오기 실패');
        setLoadingAll(false);
      });
  }, [selectedCategoryId]);

  useEffect(() => {
    setLoadingDeadline(true);
    setErrorDeadline(null);

    axios.get('http://localhost:8080/api/tickets/deadline')
      .then(res => {
        const now = new Date();
        now.setHours(0, 0, 0, 0);
        const filtered = res.data
          .filter(ticket => {
            if (!ticket.bookingDatetime) return false;
            const bookingDate = new Date(ticket.bookingDatetime);
            bookingDate.setHours(0, 0, 0, 0);
            return bookingDate >= now;
          })
          .sort((a, b) => new Date(b.ticket_created_at) - new Date(a.ticket_created_at));

        setDeadlineTickets(filtered);
        setLoadingDeadline(false);
      })
      .catch(() => {
        setErrorDeadline('예매일 순 티켓 목록 불러오기 실패');
        setLoadingDeadline(false);
      });
  }, []);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/tickets/popular?categoryId=${selectedRankingCategory}&size=5`)
      .then(res => setRankingTickets(res.data))
      .catch(err => console.error("🔥 인기 티켓 불러오기 실패:", err));
  }, [selectedRankingCategory]);

  const calculateDDay = (datetime) => {
    if (!datetime) return '';
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    const targetDate = new Date(datetime);
    targetDate.setHours(0, 0, 0, 0);

    const diffTime = targetDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

    if (diffDays > 0) return `D-${diffDays}`;
    else if (diffDays === 0) return 'D-Day';
    else return '마감';
  };

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

  return (
    <>
      <section className="banner-slider">
        <div className="banner-container">
          {bannerImages.map((src, index) => (
            <img
              key={index}
              src={src}
              alt={`배너${index + 1}`}
              className={`banner-image ${index === currentBannerIndex ? 'active' : ''}`}
            />
          ))}
        </div>
      </section>

      <main className="content">

        {/* 🔁 Ranking Section (이제 먼저 나옴) */}
        <section>
          <h2>Ranking</h2>
          <div style={{ display: 'flex', gap: '10px', marginBottom: '10px' }}>
            {rankingCategories.map(cat => (
              <button
                key={cat.id}
                onClick={() => setSelectedRankingCategory(cat.id)}
                style={{
                  padding: '6px 12px',
                  borderRadius: '8px',
                  border: selectedRankingCategory === cat.id ? '2px solid #333' : '1px solid #ccc',
                  backgroundColor: selectedRankingCategory === cat.id ? '#eee' : '#fff',
                  cursor: 'pointer'
                }}
              >
                {cat.name}
              </button>
            ))}
          </div>
          {rankingTickets.length === 0 ? (
            <p>인기 티켓 정보 준비 중입니다.</p>
          ) : (
            <div className="ranking-list">
              {rankingTickets.map((ticket, index) => (
                <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="event-card-link">
                  <div className="event-card">
                    <div className="ranking-badge">{`${index + 1}위`}</div>
                    <img src={ticket.imageUrl} alt={ticket.title} />
                    <div className="card-title">
                      <h3>{ticket.title}</h3>
                    </div>
                    <div className="card-info">
                      <p>{formatDateRange(ticket.eventStartDatetime, ticket.eventEndDatetime)}</p>
                      <p>{ticket.venue}</p>
                    </div>
                  </div>
                </Link>
              ))}
            </div>
          )}
        </section>

        <hr style={{ margin: '50px 0' }} />

        {/* 🔁 Tickets Section (이제 뒤에 나옴) */}
        <section>
          <h2>Tickets</h2>
          <div className="category-buttons">
            {categories.map(cat => (
              <button
                key={cat.id}
                className={`category-button ${selectedCategoryId === cat.id ? 'active' : ''}`}
                onClick={() => setSelectedCategoryId(cat.id)}
              >
                {cat.name}
              </button>
            ))}
          </div>
          {loadingAll && <p>로딩 중...</p>}
          {errorAll && <p style={{ color: 'red' }}>{errorAll}</p>}
          <div className="event-list-wrapper no-slider">
            {allTickets.slice(0, 10).map(ticket => (
              <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="event-card-link">
                <div className="event-card">
                  <img src={ticket.imageUrl} alt={ticket.title} />
                  <div className="card-title">
                    <h3>{ticket.title}</h3>
                  </div>
                  <div className="card-info">
                    <p>{formatDateRange(ticket.eventStartDatetime, ticket.eventEndDatetime)}</p>
                    <p>{ticket.venue}</p>
                  </div>
                </div>
              </Link>
            ))}
          </div>
        </section>

        <Footer />
      </main>
    </>
  );
};

export default HomePage;
