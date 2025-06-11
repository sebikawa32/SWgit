import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import BoardListMerged from "../Board/BoardListMerged";
import "./TicketDetailPage.css";

function TicketDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [ticket, setTicket] = useState(null);
  const [error, setError] = useState(null);
  const [isBookmarked, setIsBookmarked] = useState(false);
  const [bookmarkMessage, setBookmarkMessage] = useState("");
  const [activeTab, setActiveTab] = useState("info");

  useEffect(() => {
    axios.get(`http://localhost:8080/api/tickets/${id}`)
      .then(res => setTicket(res.data))
      .catch(err => {
        console.error("❌ 상세페이지 오류:", err);
        setError("티켓 상세 정보를 불러오는 데 실패했습니다.");
      });

    const userId = localStorage.getItem("userId");
    const token = localStorage.getItem("accessToken"); // ✅ 수정됨

    if (userId && token) {
      axios.get(`http://localhost:8080/api/bookmarks/check?userId=${userId}&ticketId=${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      }).then(res => {
        if (res.data.exists) setIsBookmarked(true);
      }).catch(() => {});
    }
  }, [id]);

const handleToggleBookmark = async () => {
  const token = localStorage.getItem("accessToken");

  if (!token) {
    alert("로그인이 필요합니다!");
    navigate("/login");
    return;
  }

  try {
    if (!isBookmarked) {
      // ✅ 북마크 추가 (쿼리 파라미터로 전송)
      await axios.post("http://localhost:8080/api/bookmarks", null, {
        params: { ticketId: Number(id) },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setIsBookmarked(true);
      setBookmarkMessage("즐겨찾기에 추가되었습니다!");
    } else {
      // ✅ 북마크 삭제 (쿼리 파라미터 그대로 사용)
      await axios.delete("http://localhost:8080/api/bookmarks", {
        params: { ticketId: Number(id) },
        headers: { Authorization: `Bearer ${token}` },
      });
      setIsBookmarked(false);
      setBookmarkMessage("즐겨찾기에서 삭제되었습니다!");
    }
  } catch (err) {
    setBookmarkMessage(
      isBookmarked
        ? "즐겨찾기 삭제에 실패했습니다."
        : (err.response?.status === 409
          ? "이미 즐겨찾기에 추가된 티켓입니다!"
          : "즐겨찾기 추가에 실패했습니다.")
    );
  }
};



  const formatDate = (dateStr) => {
    if (!dateStr || typeof dateStr !== "string") return "예매링크 참조";
    const date = new Date(dateStr);
    return isNaN(date)
      ? "예매링크 참조"
      : date.toLocaleDateString("ko-KR", { year: "numeric", month: "2-digit", day: "2-digit" });
  };

  if (error) return <p style={{ color: "red" }}>{error}</p>;
  if (!ticket) return <p>로딩 중...</p>;

  return (
    <main className="ticket-detail-container">
      <div className="ticket-detail-content">
        <div className="image-and-heart">
          <img src={ticket.imageUrl} alt={ticket.title} className="ticket-detail-image" />
          <div className="heart-row" onClick={handleToggleBookmark} style={{ cursor: "pointer" }}>
            {isBookmarked
              ? <i className="bi bi-heart-fill heart-icon"></i>
              : <i className="bi bi-heart heart-icon"></i>}
            <span className="heart-text">Bookmark</span>
          </div>
          {bookmarkMessage && <p className="bookmark-message">{bookmarkMessage}</p>}
        </div>

        <div className="ticket-info">
          <h1>{ticket.title}</h1>
          <hr className="divider" />
          <p><strong>공연기간</strong> {formatDate(ticket.eventStartDatetime)} ~ {formatDate(ticket.eventEndDatetime)}</p>
          <p><strong>장소</strong> {ticket.venue}</p>

          <div className="price-row">
            <strong>가격</strong>
            <ul className="price-list-inline">
              {ticket.price ? ticket.price.split(";").map((item, idx) => (
                <li key={idx}>{item.trim()}</li>
              )) : <li>무료</li>}
            </ul>
          </div>

          <p><strong>관람 연령</strong> {ticket.ageLimit || '예매 링크 참조'}</p>
          <p><strong>공연 시간</strong> {ticket.eventTime || '예매 링크 참조'}</p>
          <p><strong>예매일</strong> {formatDate(ticket.bookingDatetime)}</p>
          <p><strong>예매처</strong> {ticket.bookingProvider}</p>

          {ticket.bookingLink && (
            <button
              onClick={() => window.open(ticket.bookingLink, "_blank", "noopener noreferrer")}
              className="btn btn-booking"
            >
              예매 링크 바로가기
            </button>
          )}
        </div>
      </div>

      {/* ✅ 탭 선택 */}
      <div className="ticket-detail-tabs">
        <button onClick={() => setActiveTab("info")} className={activeTab === "info" ? "active" : ""}>상세정보</button>
        <button onClick={() => setActiveTab("board")} className={activeTab === "board" ? "active" : ""}>게시글</button>
      </div>

      {/* ✅ 탭 내용 */}
      <div className="ticket-detail-tab-content">
        {activeTab === "info" && ticket.descriptionUrl && (
          <div className="ticket-description-image">
            {Array.isArray(ticket.descriptionUrl) ? (
              ticket.descriptionUrl.map((url, idx) => (
                <img key={idx} src={url} alt={`상세 설명 ${idx + 1}`} style={{ width: "100%", marginTop: "16px" }} />
              ))
            ) : (
              <img src={ticket.descriptionUrl} alt="상세 설명" style={{ width: "100%", marginTop: "16px" }} />
            )}
          </div>
        )}
        {activeTab === "board" && (
          <div className="ticket-board-section">
            <BoardListMerged ticketId={id} />
          </div>
        )}
      </div>
    </main>
  );
}

export default TicketDetailPage;
