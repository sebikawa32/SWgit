import React, { useState, useEffect } from 'react';
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
];

const bannerLinks = [
  '/ticket/32603', 
  '',              
  '/ticket/33136', 
  '/ticket/32901', 
];

const HomePage = () => {
  const navigate = useNavigate();
  const [allTickets, setAllTickets] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState(1);
  const [loadingAll, setLoadingAll] = useState(false);
  const [errorAll, setErrorAll] = useState(null);

  const [selectedRankingCategory, setSelectedRankingCategory] = useState(1);
  const [rankingTickets, setRankingTickets] = useState([]);
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

    const url = `http://localhost:8080/api/tickets/category/${selectedCategoryId}`;

    axios.get(url)
      .then(res => {
        const shuffled = [...res.data].sort(() => Math.random() - 0.5).slice(0, 10);
        setAllTickets(shuffled);
        setLoadingAll(false);
      })
      .catch(() => {
        setErrorAll('티켓 목록 불러오기 실패');
        setLoadingAll(false);
      });
  }, [selectedCategoryId]);

  useEffect(() => {
    axios.get(`http://localhost:8080/api/tickets/popular?categoryId=${selectedRankingCategory}&size=5`)
      .then(res => setRankingTickets(res.data))
      .catch(err => console.error("🔥 인기 티켓 불러오기 실패:", err));
  }, [selectedRankingCategory]);

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
          {bannerImages.map((src, index) => {
            const link = bannerLinks[index];
            const image = (
              <img
                key={index}
                src={src}
                alt={`배너${index + 1}`}
                className={`banner-image ${index === currentBannerIndex ? 'active' : ''}`}
              />
            );
            return link ? (
              <Link key={index} to={link}>
                {image}
              </Link>
            ) : (
              <React.Fragment key={index}>{image}</React.Fragment>
            );
          })}
        </div>
      </section>

      <main className="content">

        <h2>AI SEARCH</h2>
        <ChatSearchBoxForHome
          onResults={(data, query) => {
            if (!data) {
              console.warn("GPT 응답 없음");
              return;
            }
            const encoded = encodeURIComponent(query);
            navigate(`/chat/search?query=${encoded}`, {
              state: { results: data },
            });
          }}
        />

        <hr style={{ margin: '50px 0' }} />

        <section>
          <h2>RANKING</h2>
          <div className="category-buttons">
            {rankingCategories.map(cat => (
              <button
                key={cat.id}
                className={`category-button ${selectedRankingCategory === cat.id ? 'active' : ''}`}
                onClick={() => setSelectedRankingCategory(cat.id)}
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

        <section>
          <h2>TICKETS</h2>
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

          <div className="ticket-grid">
            {[0, 1].map(row => (
              <div key={row} className="ticket-row">
                {allTickets.slice(row * 5, row * 5 + 5).map(ticket => (
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
            ))}
          </div>
        </section>

      </main>
    </>
  );
};

export default HomePage;
