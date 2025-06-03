import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import Footer from '../Footer/Footer';
import '../Header/Header.css';
import './HomePage.css';

const categories = [
  { id: 0, name: '전체' },
  { id: 1, name: '콘서트' },
  { id: 2, name: '뮤지컬' },
  { id: 3, name: '연극' },
  { id: 4, name: '전시' },
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
  const [deadlinePageIndex, setDeadlinePageIndex] = useState(0);

  const [rankingTickets, setRankingTickets] = useState([]);
  const [loadingRanking, setLoadingRanking] = useState(false);
  const [errorRanking, setErrorRanking] = useState(null);
  const [rankingPageIndex, setRankingPageIndex] = useState(0);

  const visibleCount = 5;

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
        setAllTickets(res.data);
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
        setDeadlineTickets(res.data);
        setLoadingDeadline(false);
      })
      .catch(() => {
        setErrorDeadline('마감일 순 티켓 목록 불러오기 실패');
        setLoadingDeadline(false);
      });
  }, []);

  const calculateDDay = (deadline) => {
    if (!deadline) return '';
    const today = new Date();
    const isoString = deadline.replace(' ', 'T');
    const deadlineDate = new Date(isoString);
    if (isNaN(deadlineDate)) return '';
    const diffTime = deadlineDate.getTime() - today.getTime();
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    if (diffDays > 0) return `D-${diffDays}`;
    else if (diffDays === 0) return 'D-Day';
    else return '마감';
  };

  // ✅ 슬라이더 렌더링 (페이지네이션 기반!)
  const renderSlider = (tickets, pageIndex, setPageIndex, showDDay) => {
    const totalPages = Math.ceil(tickets.slice(0, 20).length / visibleCount);
    const moveX = pageIndex * (100 / totalPages);

    return (
      <div className="slider-wrapper">
        <button
          onClick={() => setPageIndex(Math.max(pageIndex - 1, 0))}
          disabled={pageIndex === 0}
          className="slide-btn"
        >
          ◀
        </button>

        <div className="event-list-wrapper">
          <div
            className="event-list"
            style={{
              transform: `translateX(-${moveX}%)`,
              width: `${(tickets.slice(0, 20).length / visibleCount) * 100}%`,
              transition: 'transform 0.5s ease-in-out'
            }}
          >
            {tickets.slice(0, 20).map(ticket => (
              <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="event-card-link">
                <div className="event-card">
                  {showDDay && ticket.bookingDatetime && (
                    <p className="dday">{calculateDDay(ticket.bookingDatetime)}</p>
                  )}
                  <img src={ticket.imageUrl} alt={ticket.title} />
                  <h3>{ticket.title}</h3>
                  <p>{ticket.eventDatetime}</p>
                  <p>{ticket.venue}</p>
                </div>
              </Link>
            ))}
          </div>
        </div>

        <button
          onClick={() => setPageIndex(Math.min(pageIndex + 1, totalPages - 1))}
          disabled={pageIndex === totalPages - 1}
          className="slide-btn"
        >
          ▶
        </button>
      </div>
    );
  };

  return (
    <main className="content">
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
        {renderSlider(allTickets, allPageIndex, setAllPageIndex, false)}
      </section>

      <hr style={{ margin: '50px 0' }} />

      <section>
        <h2>Coming soon</h2>
        {loadingDeadline && <p>로딩 중...</p>}
        {errorDeadline && <p style={{ color: 'red' }}>{errorDeadline}</p>}
        {renderSlider(deadlineTickets, deadlinePageIndex, setDeadlinePageIndex, true)}
      </section>

      <hr style={{ margin: '50px 0' }} />

      <section>
        <h2>Top Ranking</h2>
        {loadingRanking && <p>로딩 중...</p>}
        {errorRanking && <p style={{ color: 'red' }}>{errorRanking}</p>}
        {renderSlider(rankingTickets, rankingPageIndex, setRankingPageIndex, false)}
      </section>

      <Footer />
    </main>
  );
};

export default HomePage;
