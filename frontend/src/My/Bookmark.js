import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../Ticket/Ticket.css";

const Bookmark = () => {
  const [bookmarks, setBookmarks] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const token = localStorage.getItem("accessToken");

  // ✅ 날짜 포맷 함수
  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}.${month}.${day}`;
  };

  // ✅ 즐겨찾기 목록 조회
  useEffect(() => {
    const fetchBookmarks = async () => {
      try {
        if (!token) {
          console.warn("⚠️ token이 없습니다.");
          setError("로그인이 필요합니다.");
          return;
        }

        const res = await axios.get("/api/bookmarks", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        setBookmarks(res.data);
      } catch (err) {
        console.error("❌ 북마크 불러오기 실패:", err);
        setError("즐겨찾기 목록을 불러오지 못했습니다.");
      }
    };

    if (token) {
      fetchBookmarks();
    } else {
      setError("로그인이 필요합니다.");
    }
  }, [token]);

  // ✅ 즐겨찾기 삭제
  const handleRemove = async (ticketId) => {
    try {
      await axios.delete("/api/bookmarks", {
        params: { ticketId },
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setBookmarks((prev) => prev.filter((item) => item.ticketId !== ticketId));
    } catch (err) {
      console.error("삭제 실패:", err);
      alert("삭제에 실패했습니다.");
    }
  };

  const handleCardClick = (ticketId) => {
    navigate(`/ticket/${ticketId}`);
  };

  return (
    <div className="concert-page">
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
              onClick={() => handleCardClick(item.ticketId)}
              style={{ cursor: "pointer" }}
            >
              <img
                src={item.imageUrl || "/images/placeholder.jpg"}
                alt={item.ticketTitle}
              />

              <div className="concert-info">
                <h2>{item.ticketTitle}</h2>

                <p style={{ whiteSpace: "nowrap" }}>
                  {formatDate(item.eventStartDatetime)} ~ {formatDate(item.eventEndDatetime)}
                </p>

                <p>{item.venue}</p>

                {item.price && <p>{item.price}원</p>}

                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRemove(item.ticketId);
                  }}
                  style={{
                    marginTop: "10px",
                    background: "#4a4a4a",
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
