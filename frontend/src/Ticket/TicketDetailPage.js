import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./TicketDetailPage.css";

function TicketDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [ticket, setTicket] = useState(null);
  const [error, setError] = useState(null);
  const [bookmarkMessage, setBookmarkMessage] = useState("");

  useEffect(() => {
    axios.get(`http://localhost:8080/api/tickets/${id}`)
      .then(res => setTicket(res.data))
      .catch(err => {
        console.error("❌ 상세페이지 오류:", err);
        setError("티켓 상세 정보를 불러오는 데 실패했습니다.");
      });
  }, [id]);

  const handleAddBookmark = async () => {
    const userId = localStorage.getItem("userId");
    if (!userId) {
      alert("로그인이 필요합니다!");
      navigate("/login");
      return;
    }

    try {
      const requestData = {
        userId: parseInt(userId),
        ticketId: parseInt(id),
      };
      const res = await axios.post("http://localhost:8080/api/bookmarks", requestData);
      console.log("✅ 즐겨찾기 성공:", res.data);
      setBookmarkMessage("즐겨찾기에 추가되었습니다!");
    } catch (err) {
      console.error("❌ 즐겨찾기 실패:", err);
      if (err.response && err.response.status === 409) {
        setBookmarkMessage("이미 즐겨찾기에 추가된 티켓입니다!");
      } else {
        setBookmarkMessage("즐겨찾기 추가에 실패했습니다.");
      }
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return date.toLocaleString("ko-KR", {
      year: "numeric",
      month: "2-digit",
      day: "2-digit"
    });
  };

  if (error) return <p style={{ color: "red" }}>{error}</p>;
  if (!ticket) return <p>로딩 중...</p>;

  return (
    <main className="ticket-detail-container">

      <div className="ticket-detail-content">
        <img src={ticket.imageUrl} alt={ticket.title} className="ticket-detail-image" />

        <div className="ticket-info">
          <h1>{ticket.title}</h1>
          <hr className="divider" />

          <p><strong>카테고리:</strong> {ticket.categoryName || "없음"}</p>
          <p><strong>공연 기간:</strong> {formatDate(ticket.eventStartDatetime)} ~ {formatDate(ticket.eventEndDatetime)}</p>
          <p><strong>장소:</strong> {ticket.venue}</p>
          <p><strong>가격:</strong> {ticket.price ? `${ticket.price}원` : "무료"}</p>
          <p><strong>예매처:</strong> {ticket.bookingProvider}</p>

          {ticket.bookingLink && (
            <button
              onClick={() => window.open(ticket.bookingLink, "_blank", "noopener noreferrer")}
              className="btn btn-booking"
            >
              예매 링크 바로가기
            </button>
          )}

          <button
            onClick={handleAddBookmark}
            className="btn btn-bookmark"
          >
            즐겨찾기 추가
          </button>

          {bookmarkMessage && (
            <p className="bookmark-message">{bookmarkMessage}</p>
          )}
        </div>
      </div>
    </main>
  );
}

export default TicketDetailPage;
