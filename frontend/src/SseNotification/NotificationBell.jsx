import React, { useState, useRef, useEffect } from "react";
import useSseNotification from "./useSseNotification";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function NotificationBell({ userId }) {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);
  const navigate = useNavigate();


  // UTC → KST 변환 후 YYYY-MM-DD HH:mm:ss 포맷
const formatDate = (isoString) => {
  if (!isoString) return "";
  const utc = new Date(isoString);
  const kst = new Date(utc.getTime() + 9 * 60 * 60 * 1000);
  const Y = kst.getFullYear();
  const M = String(kst.getMonth() + 1).padStart(2, "0");
  const D = String(kst.getDate()).padStart(2, "0");
  const h = String(kst.getHours()).padStart(2, "0");
  const m = String(kst.getMinutes()).padStart(2, "0");
  const s = String(kst.getSeconds()).padStart(2, "0");
  return `${Y}-${M}-${D} ${h}:${m}:${s}`;
};

  // 실시간 알림 받기
  useSseNotification(userId, (noti) => {
    setNotifications((prev) => {
      if (prev.some((n) => n.notificationId === noti.notificationId)) return prev;
      return [noti, ...prev];
    });
    setUnreadCount((c) => c + 1);
  });

  // 마운트 시 기존 알림/안읽음 개수 불러오기
  useEffect(() => {
    if (!userId) return;
    axios
      .get(`/notifications/${userId}`)
      .then((res) => setNotifications(res.data || []))
      .catch((err) => console.error("알림 목록 로드 실패", err));
    axios
      .get(`/notifications/${userId}/unread-count`)
      .then((res) => setUnreadCount(res.data || 0))
      .catch((err) => console.error("알림 수 로드 실패", err));
  }, [userId]);

  // 드롭다운 외부 클릭 시 닫기
  useEffect(() => {
    const handler = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    if (open) window.addEventListener("mousedown", handler);
    return () => window.removeEventListener("mousedown", handler);
  }, [open]);

  // 개별 알림 읽음 처리
  const handleRead = (notificationId) => {
    axios
      .post(`/notifications/${notificationId}/read`)
      .then(() => {
        setNotifications((prev) =>
          prev.map((n) =>
            n.notificationId === notificationId ? { ...n, isRead: true } : n
          )
        );
        setUnreadCount((c) => Math.max(0, c - 1));
      })
      .catch((err) => console.error("알림 읽음 처리 실패", err));
  };

  // 모두 읽음 처리
  const handleMarkAllRead = () => {
    axios
      .post(`/notifications/${userId}/read-all`)
      .then(() => {
        setNotifications((prev) => prev.map((n) => ({ ...n, isRead: true })));
        setUnreadCount(0);
      })
      .catch((err) => console.error("모두 읽음 처리 실패", err));
  };

  // 알림 클릭 시 분기 처리
  const handleNotificationClick = (n) => {
    console.log("알림 클릭!", n);
    if (!n.isRead) handleRead(n.notificationId);

    // 타입별 이동
    if (n.type === "comment") {
      // 게시글 상세로 이동
      navigate(`/boards/${n.targetId}`);
    } else if (n.type === "d-day") {
      // 티켓 상세로 이동
      navigate(`/ticket/${n.targetId}`);
    } else if (n.url) {
      // 혹시 url이 있으면 그대로 이동
      window.location.href = n.url;
    } else {
      // 기타 알림 분기 (없으면 그냥 닫기)
      setOpen(false);
    }
  };

  return (
    <div style={{ position: "relative", display: "inline-block" }}>
      <button
        onClick={() => setOpen((v) => !v)}
        style={{
          position: "relative",
          background: "none",
          border: "none",
          padding: 0,
          cursor: "pointer",
          lineHeight: 0,
        }}
        aria-label="알림"
      >
        {/* 사이즈를 20x20으로 약간 키운 종 모양 아이콘 */}
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          fill="currentColor"
          className="bi bi-bell-fill"
          viewBox="0 0 16 16"
        >
          <path d="M8 16a2 2 0 0 0 2-2H6a2 2 0 0 0 2 2m.995-14.901a1 
            1 0 1 0-1.99 0A5 5 0 0 0 3 6c0 1.098-.5 
            6-2 7h14c-1.5-1-2-5.902-2-7 0-2.42-1.72-4.44-4.005-4.901" />
        </svg>
        {unreadCount > 0 && (
          <span
            style={{
              position: "absolute",
              top: 2,
              right: 2,
              width: 10,
              height: 10,
              background: "#e53e3e",
              borderRadius: "50%",
            }}
          />
        )}
      </button>

      {open && (
        <div
          ref={dropdownRef}
          style={{
            position: "absolute",
            right: 0,
            marginTop: 8,
            width: 340,
            background: "#fff",
            border: "1px solid #ddd",
            borderRadius: 10,
            boxShadow: "0 4px 24px rgba(0,0,0,0.08)",
            zIndex: 10,
            maxHeight: 400,
            overflowY: "auto",
          }}
        >
          <div
            style={{
              padding: 16,
              borderBottom: "1px solid #eee",
              fontWeight: 700,
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
            }}
          >
            <span>알림함 {unreadCount > 0 && `(${unreadCount})`}</span>
            {unreadCount > 0 && (
              <button
                onClick={handleMarkAllRead}
                style={{
                  background: "none",
                  border: "none",
                  fontSize: 12,
                  color: "#2b5be3",
                  cursor: "pointer",
                }}
              >
                모두 읽음 처리
              </button>
            )}
          </div>

          {notifications.length === 0 ? (
            <div style={{ padding: 24, textAlign: "center", color: "#aaa" }}>
              알림이 없습니다
            </div>
          ) : (
            <ul style={{ listStyle: "none", margin: 0, padding: 0 }}>
              {notifications.map((n) => (
                <li
                  key={n.notificationId}
                  style={{
                    padding: "14px 16px",
                    borderBottom: "1px solid #f2f2f2",
                    background: n.isRead ? "#fafafa" : "#f1f9ff",
                    cursor: "pointer",
                  }}
                  onClick={() => handleNotificationClick(n)}
                >
                  <div
                    style={{
                      fontWeight: n.isRead ? 400 : 700,
                      color: n.isRead ? "#999" : "#2b5be3",
                    }}
                  >
                    [{n.type}] {n.content}
                  </div>
                  <div style={{ fontSize: 12, color: "#888", marginTop: 4 }}>
                    {formatDate(n.createdAt)}
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}

export default NotificationBell;
