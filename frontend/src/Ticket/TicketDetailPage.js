import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "./TicketDetailPage.css"; // 스타일 파일도 같이 관리!

function TicketDetailPage() {
  const { id } = useParams(); // URL에서 티켓 ID 가져오기
  const navigate = useNavigate();

  const [ticket, setTicket] = useState(null);
  const [error, setError] = useState(null);
  const [bookmarkMessage, setBookmarkMessage] = useState(""); // ✅ 즐겨찾기 추가 결과 메시지

  // ✅ 상세정보 요청
  useEffect(() => {
    axios.get(`http://localhost:8080/api/tickets/${id}`)
      .then(res => setTicket(res.data))
      .catch(err => {
        console.error("❌ 상세페이지 오류:", err);
        setError("티켓 상세 정보를 불러오는 데 실패했습니다.");
      });
  }, [id]);

  // ✅ 즐겨찾기 추가 요청
  const handleAddBookmark = async () => {
    const userId = localStorage.getItem("userId"); // 로그인 시 저장했다고 가정
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

  // ✅ 날짜 포맷팅 함수
  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    return date.toLocaleString();
  };

  if (error) return <p style={{ color: "red" }}>{error}</p>;
  if (!ticket) return <p>로딩 중...</p>;

  return (
    <main className="ticket-detail-container">
      <button onClick={() => navigate(-1)} className="back-btn">◀ 뒤로가기</button>

      <h1>{ticket.title}</h1>

      <div className="ticket-detail-content">
        <img src={ticket.imageUrl} alt={ticket.title} className="ticket-detail-image" />

        <div className="ticket-info">
          <p><strong>카테고리:</strong> {ticket.category?.name || "없음"}</p>
          <p><strong>일시:</strong> {formatDate(ticket.eventDatetime)}</p>
          <p><strong>장소:</strong> {ticket.venue}</p>
          <p><strong>가격:</strong> {ticket.price ? `${ticket.price}원` : "무료"}</p>
          <p><strong>설명:</strong> {ticket.description}</p>
          <p><strong>예매처:</strong> {ticket.bookingProvider}</p>
          <p><strong>예매 링크:</strong> {ticket.bookingLink ? (<a href={ticket.bookingLink} target="_blank" rel="noopener noreferrer">예매하기</a>) : "없음"}</p>
          <p><strong>예매 시작일:</strong> {formatDate(ticket.bookingDatetime)}</p>
          <p><strong>등록일:</strong> {formatDate(ticket.createdAt)}</p>

          {/* ✅ 즐겨찾기 버튼 */}
          <button
            onClick={handleAddBookmark}
            style={{
              marginTop: "10px",
              background: "#ff6b6b",
              color: "white",
              border: "none",
              borderRadius: "4px",
              padding: "8px 12px",
              cursor: "pointer",
              fontWeight: "600"
            }}
          >
            즐겨찾기 추가
          </button>

          {/* ✅ 즐겨찾기 결과 메시지 */}
          {bookmarkMessage && (
            <p style={{ color: "green", marginTop: "8px" }}>
              {bookmarkMessage}
            </p>
          )}
        </div>
      </div>
    </main>
  );
}

export default TicketDetailPage;


