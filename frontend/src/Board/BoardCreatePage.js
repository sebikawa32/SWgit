import React, { useState, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import './BoardCreatePage.css';

const BoardCreatePage = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);

  const [type] = useState(searchParams.get("type") || "general");
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [tickets, setTickets] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [filteredTickets, setFilteredTickets] = useState([]);
  const [selectedTicket, setSelectedTicket] = useState(null);

  useEffect(() => {
    axios.get('/api/tickets/summaries')
      .then(res => {
        setTickets(res.data);
        setFilteredTickets(res.data);
      })
      .catch(err => console.error('티켓 목록 불러오기 실패', err));
  }, []);

  useEffect(() => {
    const filtered = tickets.filter(ticket =>
      ticket.title.toLowerCase().includes(searchKeyword.toLowerCase())
    );
    setFilteredTickets(filtered);
  }, [searchKeyword, tickets]);

  const handleTicketSelect = (ticket) => {
    setSelectedTicket(ticket);
    setSearchKeyword(ticket.title);
    setFilteredTickets([]);
  };

  const handleTicketRemove = () => {
    setSelectedTicket(null);
    setSearchKeyword('');
  };

  const handleSubmit = async () => {
    const token = localStorage.getItem("accessToken");

    if (!token) {
      alert("로그인이 필요합니다.");
      return;
    }

    if (!title.trim() || !content.trim()) {
      alert("제목과 내용을 모두 입력해주세요.");
      return;
    }

    try {
      await axios.post("/api/boards", {
        title,
        content,
        ticketId: selectedTicket ? Number(selectedTicket.id) : null,
        type
      }, {
        headers: {
          Authorization: `Bearer ${token}`
        }
      });

      alert("게시글이 등록되었습니다.");
      navigate(-1);
    } catch (err) {
      console.error("게시글 등록 실패", err);
      alert("게시글 등록에 실패했습니다.");
    }
  };

  return (
    <div className="board-create-container">
      <h1>게시글 작성</h1>

      <div className="form-row">
        <label>티켓 검색</label>
        <div className="ticket-search-wrapper">
          <input
            type="text"
            placeholder="티켓 제목을 입력하세요"
            value={searchKeyword}
            onChange={e => setSearchKeyword(e.target.value)}
          />
          {filteredTickets.length > 0 && searchKeyword && !selectedTicket && (
            <ul className="search-dropdown">
              {filteredTickets.map(ticket => (
                <li key={ticket.id} onClick={() => handleTicketSelect(ticket)}>
                  {ticket.title}
                </li>
              ))}
            </ul>
          )}
        </div>
        {selectedTicket && (
          <div className="selected-ticket" style={{ display: "flex", alignItems: "center", marginTop: "10px" }}>
            <span style={{ fontWeight: "500", flex: 1 }}>{selectedTicket.title}</span>
            <i
              className="bi bi-x-circle-fill"
              style={{ color: "#dc3545", cursor: "pointer", marginLeft: "8px", fontSize: "20px" }}
              onClick={handleTicketRemove}
            ></i>
          </div>
        )}
      </div>

      <div className="form-row">
        <label>제목</label>
        <input
          type="text"
          placeholder="제목을 입력하세요"
          value={title}
          onChange={e => setTitle(e.target.value)}
        />
      </div>

      <div className="form-row">
        <label>내용</label>
        <textarea
          placeholder="내용을 입력하세요"
          value={content}
          onChange={e => setContent(e.target.value)}
        />
      </div>

      <div className="button-row">
        <button onClick={handleSubmit}>등록</button>
      </div>
    </div>
  );
};

export default BoardCreatePage;
