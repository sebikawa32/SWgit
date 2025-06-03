// Bookmark.js
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom"; // ✅ 상세 페이지 이동을 위해 추가
import axios from "axios";
import "../Ticket/Ticket.css"; // ✅ Ticket 카드 스타일 가져오기!

const Bookmark = () => {
  const [bookmarks, setBookmarks] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const userId = localStorage.getItem("userId");

  useEffect(() => {
    const fetchBookmarks = async () => {
      try {
        // ✅ 템플릿 리터럴로 URL 작성
        const res = await axios.get(`/api/bookmarks/${userId}`);
        setBookmarks(res.data);
      } catch (err) {
        console.error(err);
        setError("즐겨찾기 목록을 불러오지 못했습니다.");
      }
    };

    if (userId) {
      fetchBookmarks();
    }
  }, [userId]);

  // ✅ 삭제 처리
  const handleRemove = async (ticketId) => {
    try {
      await axios.delete("/api/bookmarks", {
        data: { userId: parseInt(userId), ticketId },
      });
      setBookmarks((prev) => prev.filter((item) => item.ticketId !== ticketId));
    } catch (err) {
      console.error("삭제 실패:", err);
      alert("삭제에 실패했습니다.");
    }
  };

  // ✅ 상세 페이지로 이동
  const handleCardClick = (ticketId) => {
    navigate(`/ticket/${ticketId}`);
  };

  return (
    <div className="concert-page"> {/* ✅ TicketPage 스타일 적용 */}
      <h1>내 즐겨찾기 목록</h1>

      {error && <p style={{ color: "red", textAlign: "center" }}>{error}</p>}

      {bookmarks.length === 0 ? (
        <p style={{ textAlign: "center" }}>아직 즐겨찾기한 티켓이 없어요.</p>
      ) : (
        <div className="concert-grid">
          {bookmarks.map((item) => (
            <div
              className="concert-card"
              key={item.ticketId}
              onClick={() => handleCardClick(item.ticketId)} // ✅ 카드 클릭 시 상세 페이지 이동
              style={{ cursor: "pointer" }}
            >
              {/* ✅ 썸네일 이미지 (없으면 기본 이미지) */}
              <img
                src={item.imageUrl || "/images/placeholder.jpg"}
                alt={item.ticketTitle}
              />

              <div className="concert-info">
                <h2>{item.ticketTitle}</h2>
                <p>{item.venue}</p>
                {/* ✅ 삭제 버튼 클릭 시 부모 클릭 이벤트 막기 */}
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRemove(item.ticketId);
                  }}
                  style={{
                    marginTop: "10px",
                    background: "#ff6b6b",
                    color: "white",
                    border: "none",
                    borderRadius: "4px",
                    padding: "6px 10px",
                    cursor: "pointer",
                    fontSize: "0.85rem",
                  }}
                >
                  삭제
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Bookmark;
