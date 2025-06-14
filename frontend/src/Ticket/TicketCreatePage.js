import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const TicketCreatePage = () => {
  const navigate = useNavigate();
  const [ticket, setTicket] = useState({
    title: '',
    price: '',
    venue: '',
    bookingLink: '',
    bookingProvider: '',
    bookingDatetime: '',
    eventStart: '',
    eventEnd: '',
    imageUrl: '',
    description: '',
    categoryId: '1', // 기본: 콘서트
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setTicket({ ...ticket, [name]: value });
  };

  const handleSubmit = async () => {
    const token = localStorage.getItem('accessToken');
    try {
      await axios.post('http://localhost:8080/api/tickets', ticket, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });
      alert("티켓이 등록되었습니다.");
      navigate('/');
    } catch (err) {
      console.error("❌ 티켓 등록 실패:", err);
      alert("등록 실패");
    }
  };

  return (
    <div className="ticket-create-container">
      <style>{`
        .ticket-create-container {
          max-width: 700px;
          margin: 40px auto;
          padding: 24px;
          background-color: #fff;
          border: 1px solid #ccc;
          border-radius: 12px;
          box-shadow: 0 4px 12px rgba(0,0,0,0.05);
          font-family: 'Segoe UI', sans-serif;
        }
        .ticket-create-container h2 {
          text-align: center;
          font-size: 1.6rem;
          margin-bottom: 24px;
        }
        .ticket-create-container input,
        .ticket-create-container select,
        .ticket-create-container textarea {
          width: 100%;
          padding: 10px;
          margin-bottom: 16px;
          border-radius: 6px;
          border: 1px solid #ccc;
        }
        .ticket-create-container button {
          padding: 12px;
          background-color: #111;
          color: white;
          font-weight: bold;
          border: none;
          border-radius: 6px;
          cursor: pointer;
          width: 100%;
        }
        .ticket-create-container button:hover {
          background-color: #333;
        }
      `}</style>

      <h2>티켓 등록</h2>
      <input name="title" value={ticket.title} onChange={handleChange} placeholder="공연 제목" />
      <input name="price" value={ticket.price} onChange={handleChange} placeholder="티켓 가격" />
      <input name="venue" value={ticket.venue} onChange={handleChange} placeholder="공연 장소" />
      <input name="bookingLink" value={ticket.bookingLink} onChange={handleChange} placeholder="예매 링크" />
      <input name="bookingProvider" value={ticket.bookingProvider} onChange={handleChange} placeholder="예매처" />
      <input name="bookingDatetime" value={ticket.bookingDatetime} onChange={handleChange} placeholder="예매 시작일 (예: 2025-06-20 10:00)" />
      <input name="eventStart" value={ticket.eventStart} onChange={handleChange} placeholder="공연 시작일 (예: 2025-07-01 19:00)" />
      <input name="eventEnd" value={ticket.eventEnd} onChange={handleChange} placeholder="공연 종료일 (예: 2025-08-30 22:00)" />
      <input name="imageUrl" value={ticket.imageUrl} onChange={handleChange} placeholder="대표 이미지 URL" />
      <textarea name="description" value={ticket.description} onChange={handleChange} placeholder="공연 상세 설명" />
      <select name="categoryId" value={ticket.categoryId} onChange={handleChange}>
        <option value="1">콘서트</option>
        <option value="2">전시</option>
        <option value="3">연극</option>
        <option value="4">뮤지컬</option>
      </select>

      <button onClick={handleSubmit}>등록</button>
    </div>
  );
};

export default TicketCreatePage;
