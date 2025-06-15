import React, { useState, useRef, useEffect } from "react";
import useSseNotification from "./useSseNotification"; // 아까 만든 훅
import axios from "axios";



// 뱃지/알림 리스트/드롭다운 UI 스타일은 자유롭게 커스텀!
function NotificationBell({ userId }) {
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  // 실시간 알림 받기
  useSseNotification(userId, (noti) => {
    setNotifications((prev) => [noti, ...prev]);
    setUnreadCount((count) => count + 1);
  });

  // 마운트 시 기존 알림/안읽음 개수 불러오기
  useEffect(() => {
    if (!userId) return;
    axios.get(`/api/notifications/${userId}`).then(res => setNotifications(res.data || []));
    axios.get(`/api/notifications/${userId}/unread-count`).then(res => setUnreadCount(res.data || 0));
  }, [userId]);

  // 드롭다운 외부 클릭시 닫기
  useEffect(() => {
    function handleClick(e) {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) setOpen(false);
    }
    if (open) window.addEventListener("mousedown", handleClick);
    return () => window.removeEventListener("mousedown", handleClick);
  }, [open]);

  // 알림 읽음 처리
  const handleRead = (notificationId) => {
    axios.post(`/api/notifications/${notificationId}/read`).then(() => {
      setNotifications((prev) =>
        prev.map((n) => n.notificationId === notificationId ? { ...n, isRead: true } : n)
      );
      setUnreadCount((count) => Math.max(0, count - 1));
    });
  };

  return (
    <div style={{ position: "relative", display: "inline-block" }}>
      {/* 종 버튼 + 뱃지 */}
      <button
        onClick={() => setOpen((v) => !v)}
        style={{
          position: "relative",
          background: "none",
          border: "none",
          fontSize: 28,
          cursor: "pointer"
        }}
        aria-label="알림"
      >
        🔔
        {unreadCount > 0 && (
          <span style={{
            position: "absolute",
            top: -3, right: -3,
            background: "#e53e3e",
            color: "white",
            borderRadius: "50%",
            padding: "2px 7px",
            fontSize: 12,
            fontWeight: 700,
            border: "1px solid #fff"
          }}>{unreadCount}</span>
        )}
      </button>

      {/* 알림함 드롭다운 */}
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
            overflowY: "auto"
          }}
        >
          <div style={{ padding: 16, borderBottom: "1px solid #eee", fontWeight: 700 }}>
            알림함
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
                    cursor: "pointer"
                  }}
                  onClick={() => {
                    if (!n.isRead) handleRead(n.notificationId);
                    window.location.href = n.url; // 클릭 시 해당 url로 이동
                  }}
                >
                  <div style={{ fontWeight: n.isRead ? 400 : 700, color: n.isRead ? "#999" : "#2b5be3" }}>
                    [{n.type}] {n.content}
                  </div>
                  <div style={{ fontSize: 12, color: "#888", marginTop: 4 }}>
                    {n.timeAgo || n.createdAt}
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
