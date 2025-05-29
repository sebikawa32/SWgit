// src/pages/TicketList.js
import React, { useEffect, useState } from "react";

const TicketList = ({ selectedCategory }) => {
  const [tickets, setTickets] = useState([]);

  useEffect(() => {
    // JSON 파일을 fetch해서 tickets 상태 업데이트
    fetch("/mock/tickets.json")
      .then((res) => res.json())
      .then((data) => setTickets(data));
  }, []);

  // 선택된 카테고리에 따라 필터링
  const filteredTickets =
    selectedCategory === "전체"
      ? tickets
      : tickets.filter((ticket) => ticket.category === selectedCategory);

  return (
    <div className="event-list">
      {filteredTickets.map((ticket) => (
        <div key={ticket.id} className="event-card">
          <img src={ticket.image} alt={ticket.title} />
          <h3>{ticket.title}</h3>
          <p>{ticket.date}</p>
          <p>{ticket.location}</p>
        </div>
      ))}
    </div>
  );
};

export default TicketList;