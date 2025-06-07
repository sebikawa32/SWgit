import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './BoardListPage.css';

const BoardList = ({ type = "general", ticketId }) => {
  const [boards, setBoards] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const postsPerPage = 10;
  const navigate = useNavigate();

  useEffect(() => {
    const fetchBoards = async () => {
      try {
        let res;
        if (ticketId) {
          res = await axios.get(`/api/boards/tickets/${ticketId}/boards?type=${type}`);
        } else {
          res = await axios.get(`/api/boards?type=${type}`);
        }
        const sorted = [...res.data].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        setBoards(sorted);
        setCurrentPage(1); // 티켓 변경 시 첫 페이지로 초기화
      } catch (err) {
        console.error("게시글 불러오기 실패", err);
      }
    };
    fetchBoards();
  }, [type, ticketId]);

  const handleWriteClick = () => {
    const query = `?type=${type}` + (ticketId ? `&ticketId=${ticketId}` : '');
    navigate(`/boards/new${query}`);
  };

  // ✅ 페이징 계산
  const indexOfLast = currentPage * postsPerPage;
  const indexOfFirst = indexOfLast - postsPerPage;
  const currentBoards = boards.slice(indexOfFirst, indexOfLast);
  const totalPages = Math.ceil(boards.length / postsPerPage);

  return (
    <div className="board-list-container">
      <div className="board-list-header">
        <h1>{type === 'notice' ? '공지사항' : '게시판'} 목록</h1>
        <button className="write-button" onClick={handleWriteClick}>글쓰기</button>
      </div>

      <table className="board-table">
        <thead>
          <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          {currentBoards.map((board, index) => (
            <tr key={board.id}>
              <td>{boards.length - (indexOfFirst + index)}</td>
              <td>
                <Link to={`/boards/${board.id}`}>{board.title}</Link>
              </td>
              <td>{board.nickname}</td>
              <td>{board.createdAt?.slice(0, 10)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* ✅ 페이지네이션 */}
      <div className="pagination">
        <button onClick={() => setCurrentPage(prev => Math.max(prev - 1, 1))} disabled={currentPage === 1}>
          이전
        </button>
        {Array.from({ length: totalPages }, (_, i) => (
          <button
            key={i + 1}
            onClick={() => setCurrentPage(i + 1)}
            className={currentPage === i + 1 ? 'active' : ''}
          >
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

export default BoardList;
