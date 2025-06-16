import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "./Bookmark.css";

const Bookmark = () => {
  const [bookmarks, setBookmarks] = useState([]);
  const [error, setError] = useState(null);
  const navigate = useNavigate();
  const token = localStorage.getItem("accessToken");

  const formatDate = (dateStr) => {
    if (!dateStr) return "";
    const date = new Date(dateStr);
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    return `${year}.${month}.${day}`;
  };

  useEffect(() => {
    const fetchBookmarks = async () => {
      try {
        if (!token) {
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
        setError("즐겨찾기 목록을 불러오지 못했습니다.");
      }
    };

    if (token) {
      fetchBookmarks();
    } else {
      setError("로그인이 필요합니다.");
    }
  }, [token]);

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
      alert("삭제에 실패했습니다.");
    }
  };

  const handleCardClick = (ticketId) => {
    navigate(`/ticket/${ticketId}`);
  };

  return (
    <div className="bookmark-page">
      <h1 className="bookmark-title">내 즐겨찾기 목록</h1>

      {error && <p className="bookmark-error">{error}</p>}

      {bookmarks.length === 0 ? (
        <p className="bookmark-empty">아직 즐겨찾기한 티켓이 없어요.</p>
      ) : (
        <div className="bookmark-grid">
          {bookmarks.map((item) => (
            <div
              className="bookmark-card"
              key={item.ticketId}
              onClick={() => handleCardClick(item.ticketId)}
            >
              <img
                src={item.imageUrl || "/images/placeholder.jpg"}
                alt={item.ticketTitle}
              />

              <div className="bookmark-info">
                <h2>{item.ticketTitle}</h2>
                <p>{formatDate(item.eventStartDatetime)} ~ {formatDate(item.eventEndDatetime)}</p>
                <p>{item.venue}</p>
                {item.price && <p>{item.price}원</p>}

                <button
                  className="bookmark-remove-btn"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRemove(item.ticketId);
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
