import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './BoardListPage.css';

const BoardListMerged = ({ ticketId }) => {
  const [boards, setBoards] = useState([]);
  const postsPerPage = 10;
  const [currentPage, setCurrentPage] = useState(1);

  useEffect(() => {
    const fetchBoards = async () => {
      try {
        const [noticeRes, generalRes] = await Promise.all([
          axios.get(`/api/boards/tickets/${ticketId}/boards?type=notice`),
          axios.get(`/api/boards/tickets/${ticketId}/boards?type=general`),
        ]);

        const notices = noticeRes.data.map(b => ({ ...b, isNotice: true }));
        const generals = generalRes.data.map(b => ({ ...b, isNotice: false }));

        const combined = [...notices, ...generals].sort((a, b) => {
          if (a.isNotice !== b.isNotice) return a.isNotice ? -1 : 1;
          return new Date(b.createdAt) - new Date(a.createdAt);
        });

        setBoards(combined);
      } catch (err) {
        console.error("게시글 불러오기 실패", err);
      }
    };
    fetchBoards();
  }, [ticketId]);

  const indexOfLast = currentPage * postsPerPage;
  const indexOfFirst = indexOfLast - postsPerPage;
  const currentBoards = boards.slice(indexOfFirst, indexOfLast);
  const totalPages = Math.ceil(boards.length / postsPerPage);

  return (
    <div className="board-list-container">
      <div className="board-list-header">
        <h1>게시글</h1>
      </div>
      <table className="board-table">
        <thead>
          <tr>
            <th>번호</th>
            <th>티켓</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          {currentBoards.map((board, index) => (
            <tr key={board.id} className={board.isNotice ? "notice-row" : ""}>
              <td>{board.id}</td>
              <td>{board.ticketTitle || ""}</td>
              <td>
                <Link to={`/boards/${board.id}`}>
                  {board.isNotice && <span className="notice-label">[공지사항]</span>} {board.title}
                </Link>
              </td>
              <td>{board.nickname}</td>
              <td>{board.createdAt?.slice(0, 10)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className="pagination">
        <button onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))} disabled={currentPage === 1}>
          이전
        </button>
        {Array.from({ length: totalPages }, (_, i) => (
          <button key={i} onClick={() => setCurrentPage(i + 1)} className={currentPage === i + 1 ? "active" : ""}>
            {i + 1}
          </button>
        ))}
        <button onClick={() => setCurrentPage(prev => Math.min(prev + 1, totalPages))} disabled={currentPage === totalPages}>
          다음
        </button>
      </div>
    </div>
  );
};

export default BoardListMerged;
