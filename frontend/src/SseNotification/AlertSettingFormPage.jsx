
// src/pages/AlertSettingFormPage.jsx
import React, { useEffect, useState, useRef } from "react";
import { useLocation } from "react-router-dom";
import axios from "axios";
import "./AlertSettingFormPage.css";

function AlertSettingFormPage() {
  const location = useLocation();
  const queryParams = new URLSearchParams(location.search);
  const userId = Number(queryParams.get("userId"));

  const [tickets, setTickets] = useState([]);
  const [ticketSearch, setTicketSearch] = useState("");
  const [selectedTicketId, setSelectedTicketId] = useState(null);
  const [alertMinutes, setAlertMinutes] = useState(1440);
  const [emailEnabled, setEmailEnabled] = useState(false);
  const [submitted, setSubmitted] = useState(false);
  const [showSuggestions, setShowSuggestions] = useState(false);
  const suggestionsRef = useRef();

  useEffect(() => {
    axios
      .get("http://localhost:8080/api/tickets")
      .then(res => {
        const list = Array.isArray(res.data) ? res.data : res.data.tickets || [];
        setTickets(list);
      })
      .catch(err => console.error("티켓 불러오기 실패:", err));
  }, []);

  useEffect(() => {
    const handleClickOutside = e => {
      if (suggestionsRef.current && !suggestionsRef.current.contains(e.target)) {
        setShowSuggestions(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const filtered = tickets.filter(t =>
    t.title.toLowerCase().includes(ticketSearch.toLowerCase())
  );

  const handleSelect = ticket => {
    setSelectedTicketId(ticket.id);
    setTicketSearch(ticket.title);
    setShowSuggestions(false);
  };

  const handleSubmit = async e => {
    e.preventDefault();
    if (!selectedTicketId) {
      alert("공연을 선택해주세요");
      return;
    }
    try {
      await axios.post(
        "http://localhost:8080/api/alerts",
        { userId, ticketId: selectedTicketId, alertMinutes, emailEnabled }
      );
      setSubmitted(true);
    } catch (err) {
      console.error("알림 설정 실패:", err);
      alert("이미 설정했거나 오류가 발생했습니다");
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
              {filtered.map(ticket => (
                <li
                  key={ticket.id}
                  className="suggestion-item"
                  onClick={() => handleSelect(ticket)}
                >
                  {ticket.title}
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
    </div>
  );
}

export default AlertSettingFormPage;

