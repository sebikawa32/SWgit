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

  const [popularTickets, setPopularTickets] = useState([]);
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

  const formatDate = (dateStr) => {
    if (!dateStr) return '';
    const date = new Date(dateStr);
    return date.toLocaleDateString('ko-KR', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
    });
  };

  const renderSlider = (tickets, pageIndex, setPageIndex, showDDay) => {
    const displayTickets = tickets.slice(0, 20);
    const totalPages = Math.ceil(displayTickets.length / visibleCount);
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
              width: `${(displayTickets.length / visibleCount) * 100}%`,
              transition: 'transform 0.5s ease-in-out'
            }}
          >
            {displayTickets.map(ticket => (
              <Link to={`/ticket/${ticket.id}`} key={ticket.id} className="event-card-link">
                <div className="event-card">
                  {showDDay && ticket.bookingDatetime && (
                    <div className="dday-badge">{calculateDDay(ticket.bookingDatetime)}</div>
                  )}
                  <img src={ticket.imageUrl} alt={ticket.title} />
                  <h3>{ticket.title}</h3>
                  <p>{formatDate(ticket.eventStartDatetime)} ~ {formatDate(ticket.eventEndDatetime)}</p>
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
        <h2>Ranking</h2>
        {popularTickets.length === 0 ? (
          <p>인기 티켓 정보 준비 중입니다.</p>
        ) : (
          renderSlider(popularTickets, 0, () => {}, false)
        )}
      </section>

      <Footer />
    </main>
  );
};

export default HomePage;
