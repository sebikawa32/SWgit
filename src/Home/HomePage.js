import React, { useState, useEffect } from 'react';
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
  // 전체 티켓 (카테고리별 필터링 포함)
  const [allTickets, setAllTickets] = useState([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState(0);
  const [loadingAll, setLoadingAll] = useState(false);
  const [errorAll, setErrorAll] = useState(null);

  // 슬라이더 인덱스 상태 추가
  const [slideIndex, setSlideIndex] = useState(0);
  const visibleCount = 4; // 한 화면에 보여줄 카드 개수

  // 마감일 순 티켓
  const [deadlineTickets, setDeadlineTickets] = useState([]);
  const [loadingDeadline, setLoadingDeadline] = useState(false);
  const [errorDeadline, setErrorDeadline] = useState(null);

  useEffect(() => {
    setLoadingAll(true);
    setErrorAll(null);
    setSlideIndex(0); // 카테고리 바뀌면 슬라이더 인덱스 초기화

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
  }, []); // 컴포넌트 마운트 시 한 번만 호출

  // D-day 계산 함수 (마감일 기준)
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

  // 슬라이더 최대 인덱스 계산
  const maxSlideIndex = Math.max(0, allTickets.length - visibleCount);

  const handlePrev = () => {
    setSlideIndex((prev) => Math.max(prev - 1, 0));
  };

  const handleNext = () => {
    setSlideIndex((prev) => Math.min(prev + 1, maxSlideIndex));
  };

  return (
    <main className="content" style={{ paddingTop: '140px' }}>
      <img src="/images/banner.jpg" alt="홈 배너" className="home-banner" />

      {/* 전체 티켓 목록 */}
      <section>
        <h2>Tickets </h2>
        {/* 카테고리 선택 버튼 */}
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

        {loadingAll && <p>전체 티켓 로딩 중...</p>}
        {errorAll && <p style={{ color: 'red' }}>{errorAll}</p>}

        {/* 슬라이더 컨테이너 */}
        <div className="slider-wrapper">
          <button onClick={handlePrev} disabled={slideIndex === 0} className="slide-btn">◀</button>

          <div
            className="event-list"
            style={{
              transform: `translateX(-${slideIndex * (100 / visibleCount)}%)`,
              transition: 'transform 0.5s ease',
              width: `${(allTickets.length / visibleCount) * 100}%`,
            }}
          >
            {allTickets.map(event => (
              <div key={event.id} className="event-card">
                <img src={event.image} alt={event.title} />
                <h3>{event.title}</h3>
                <p>{event.date}</p>
                <p>{event.location}</p>
              </div>
            ))}
          </div>

          <button onClick={handleNext} disabled={slideIndex === maxSlideIndex} className="slide-btn">▶</button>
        </div>
      </section>

      <hr style={{ margin: '50px 0' }} />

      {/* 마감일 순 티켓 목록 */}
      <section>
        <h2>Comming soon</h2>
        {loadingDeadline && <p>마감일 순 티켓 로딩 중...</p>}
        {errorDeadline && <p style={{ color: 'red' }}>{errorDeadline}</p>}
        <div className="event-list">
          {deadlineTickets.map(event => (
            <div key={event.id} className="event-card">
              {event.bookingDatetime && (
                <p className="dday">{calculateDDay(event.bookingDatetime)}</p>
              )}
              <img src={event.imageUrl} alt={event.title} />
              <h3>{event.title}</h3>
              <p>{event.eventDatetime}</p>
              <p>{event.venue}</p>
            </div>
          ))}
        </div>
      </section>

      <Footer />
    </main>
  );
};

export default HomePage;

