import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import BoardListMerged from "../Board/BoardListMerged";
import "./TicketDetailPage.css";

const MapEmbed = ({ venue }) => {
  if (!venue) return null;
  const encodedVenue = encodeURIComponent(venue);
  return (
    <div style={{ marginTop: "16px", width: "100%", height: "300px" }}>
      <iframe
        title="map"
        width="100%"
        height="100%"
        frameBorder="0"
        style={{ border: 0, borderRadius: "8px" }}
        src={`https://www.google.com/maps?q=${encodedVenue}&output=embed`}
        allowFullScreen
      ></iframe>
    </div>
  );
};

function TicketDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [ticket, setTicket] = useState(null);
  const [error, setError] = useState(null);
  const [isBookmarked, setIsBookmarked] = useState(false);
  const [bookmarkCount, setBookmarkCount] = useState(0);
  const [bookmarkMessage, setBookmarkMessage] = useState("");
  const [activeTab, setActiveTab] = useState("info");

  useEffect(() => {
    axios.get(`/api/tickets/${id}`)
      .then(res => setTicket(res.data))
      .catch(err => {
        console.error("❌ 상세페이지 오류:", err);
        setError("티켓 상세 정보를 불러오는 데 실패했습니다.");
      });

    const token = localStorage.getItem("accessToken");
    if (token) {
      axios.get(`/api/bookmarks/check?ticketId=${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      }).then(res => {
        if (res.data.exists) setIsBookmarked(true);
      });
    }

    axios.get(`/api/bookmarks/count?ticketId=${id}`)
      .then(res => setBookmarkCount(res.data.count || 0))
      .catch(() => setBookmarkCount(0));
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
        await axios.post("/api/bookmarks", null, {
          params: { ticketId: Number(id) },
          headers: { Authorization: `Bearer ${token}` },
        });
        setIsBookmarked(true);
        setBookmarkMessage("즐겨찾기에 추가되었습니다!");
      } else {
        await axios.delete("/api/bookmarks", {
          params: { ticketId: Number(id) },
          headers: { Authorization: `Bearer ${token}` },
        });
        setIsBookmarked(false);
        setBookmarkMessage("즐겨찾기에서 삭제되었습니다!");
      }

      const res = await axios.get(`/api/bookmarks/count?ticketId=${id}`);
      setBookmarkCount(res.data.count || 0);
    } catch (err) {
      console.error("즐겨찾기 처리 중 오류:", err);
      setBookmarkMessage("즐겨찾기 처리 중 오류가 발생했습니다.");
    }
  };

  const formatDate = (dateStr) => {
    if (!dateStr || typeof dateStr !== "string") return "예매링크 참조";
    const date = new Date(dateStr);
    return isNaN(date)
      ? "예매링크 참조"
      : date.toLocaleDateString("ko-KR", {
          year: "numeric",
          month: "2-digit",
          day: "2-digit"
        });
  };

  const handleBookingClick = () => {
    axios.post(`/api/tickets/${id}/click`).catch(err => {
      console.error("예매 클릭 로그 저장 실패:", err);
    });
    window.open(ticket.bookingLink, "_blank", "noopener noreferrer");
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
            <span className="bookmark-count">({bookmarkCount})</span>
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
              {ticket.price
                ? ticket.price.split(";").map((item, idx) => (
                    <li key={idx}>{item.trim()}</li>
                  ))
                : <li>무료</li>}
            </ul>
          </div>

          <p><strong>관람 연령</strong> {ticket.ageLimit || '전체관람가'}</p>
          <p><strong>공연 시간</strong> {ticket.eventTime || '예매 링크 참조'}</p>
          <p><strong>예매처</strong> {ticket.bookingProvider}</p>

          {ticket.bookingLink && (
            <button onClick={handleBookingClick} className="btn btn-booking">
              예매 링크 바로가기
            </button>
          )}
        </div>
      </div>

      <div className="ticket-detail-tabs">
        <button onClick={() => setActiveTab("info")} className={activeTab === "info" ? "active" : ""}>상세정보</button>
        <button onClick={() => setActiveTab("map")} className={activeTab === "map" ? "active" : ""}>길찾기</button>
        <button onClick={() => setActiveTab("board")} className={activeTab === "board" ? "active" : ""}>게시글</button>
      </div>

      <div className="ticket-detail-tab-content">
        {activeTab === "info" && ticket.descriptionUrl && (
          <div className="ticket-description-image">
            {Array.isArray(ticket.descriptionUrl)
              ? ticket.descriptionUrl.map((url, idx) => (
                  <img key={idx} src={url} alt={`상세 설명 ${idx + 1}`} style={{ width: "100%", marginTop: "16px" }} />
                ))
              : <img src={ticket.descriptionUrl} alt="상세 설명" style={{ width: "100%", marginTop: "16px" }} />}
          </div>
        )}

        {activeTab === "map" && <MapEmbed venue={ticket.venue} />}

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
