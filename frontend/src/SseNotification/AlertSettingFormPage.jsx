// src/pages/AlertSettingFormPage.jsx
import React, { useEffect, useState, useRef } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import "./AlertSettingFormPage.css";

export default function AlertSettingFormPage() {
  const location = useLocation();
  const query = new URLSearchParams(location.search);
  const userId = Number(query.get("userId"));

  const [tickets, setTickets] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [ticketSearch, setTicketSearch] = useState("");
  const [selectedTicketId, setSelectedTicketId] = useState(null);
  const [alertMinutes, setAlertMinutes] = useState(1440);
  const [emailEnabled, setEmailEnabled] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const suggestionsRef = useRef();

  // 전체 티켓 목록 (제목 매핑용)
  useEffect(() => {
    axios
      .get(`/tickets`)
      .then(res => setTickets(Array.isArray(res.data) ? res.data : res.data.tickets || []))
      .catch(console.error);
  }, []);

  // 내 알림 목록
  const fetchAlerts = () => {
    axios
      .get(`/alerts?userId=${userId}`)
      .then(res => setAlerts(res.data || []))
      .catch(console.error);
  };
  useEffect(() => {
    if (userId) fetchAlerts();
  }, [userId]);

  // 검색창 외부 클릭 시 제안 목록 닫기
  useEffect(() => {
    const handler = e => {
      if (suggestionsRef.current && !suggestionsRef.current.contains(e.target)) {
        setShowSuggestions(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  const filtered = tickets.filter(t =>
    t.title.toLowerCase().includes(ticketSearch.toLowerCase())
  );

  const handleSelect = ticket => {
    setSelectedTicketId(ticket.id);
    setTicketSearch(ticket.title);
    setShowSuggestions(false);
  };

  const formatAlertTime = mins => {
    switch (mins) {
      case 1440: return "1일 전";
      case 720:  return "12시간 전";
      case 120:  return "2시간 전";
      case 30:   return "30분 전";
      default:   return `${mins}분 전`;
    }
  };

  const handleSubmit = async e => {
    e.preventDefault();
    if (!selectedTicketId) {
      alert("공연을 선택해주세요");
      return;
    }
    try {
      await axios.post(
        `/alerts?userId=${userId}`,
        { ticketId: selectedTicketId, alertMinutes, emailEnabled }
      );
      setSubmitted(true);
      fetchAlerts();
    } catch (err) {
      console.error("알림 설정 실패:", err);
      alert("이미 설정했거나 오류가 발생했습니다");
    }
  };

  const handleDelete = async alertId => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      await axios.delete(
        `/alerts/${alertId}?userId=${userId}`
      );
      fetchAlerts();
    } catch (err) {
      console.error("삭제 실패:", err);
      alert("삭제 중 오류가 발생했습니다");
    }
  };

  return (
    <div className="alert-container">
      <h2 className="alert-header">알림 설정</h2>
      <form className="alert-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label className="form-label">공연 검색</label>
          <input
            type="text"
            className="form-input"
            value={ticketSearch}
            onChange={e => { setTicketSearch(e.target.value); setShowSuggestions(true); }}
            onFocus={() => setShowSuggestions(true)}
            placeholder="공연명으로 검색"
          />
          {showSuggestions && ticketSearch && (
            <ul className="suggestions" ref={suggestionsRef}>
              {filtered.map(t => (
                <li
                  key={t.id}
                  className="suggestion-item"
                  onClick={() => handleSelect(t)}
                >
                  {t.title}
                </li>
              ))}
              {filtered.length === 0 && (
                <li className="suggestion-disabled">검색 결과가 없습니다</li>
              )}
            </ul>
          )}
        </div>

        <div className="form-group">
          <label className="form-label">언제 알림 받을까요?</label>
          <select
            className="form-select"
            value={alertMinutes}
            onChange={e => setAlertMinutes(Number(e.target.value))}
          >
            <option value={1440}>1일 전</option>
            <option value={720}>12시간 전</option>
            <option value={120}>2시간 전</option>
            <option value={30}>30분 전</option>
          </select>
        </div>

        <div className="form-group checkbox-group">
          <input
            id="emailEnabled"
            type="checkbox"
            checked={emailEnabled}
            onChange={e => setEmailEnabled(e.target.checked)}
          />
          <label htmlFor="emailEnabled" className="form-label">
            이메일로도 알림 받기
          </label>
        </div>

        <button type="submit" className="form-button">저장</button>
        {submitted && <div className="submit-msg">알림 설정 완료!</div>}
      </form>

      <div className="alert-list-section">
        <h2 className="alert-header">내 알림 목록</h2>
        <div className="alert-table">
          <div className="alert-table-header">
            <div className="col-title">공연 제목</div>
            <div className="col-time">알림 시간</div>
            <div className="col-action"></div>
          </div>
          {alerts.length > 0 ? (
            alerts.map(a => {
              const t = tickets.find(x => x.id === a.ticketId) || {};
              return (
                <div className="alert-table-row" key={a.alertId}>
                  <div className="col-title">{t.title || "알 수 없음"}</div>
                  <div className="col-time">{formatAlertTime(a.alertMinutes)}</div>
                  <button
                    className="delete-btn"
                    onClick={() => handleDelete(a.alertId)}
                  >
                    삭제
                  </button>
                </div>
              );
            })
          ) : (
            <div className="no-alert">설정된 알림이 없습니다.</div>
          )}
        </div>
      </div>
    </div>
  );
}
