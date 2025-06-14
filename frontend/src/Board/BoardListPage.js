import React, { useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './BoardListPage.css';

const BoardList = ({ type = "general", ticketId }) => {
  const [boards, setBoards] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [sort, setSort] = useState("latest");
  const postsPerPage = 10;
  const navigate = useNavigate();

  const userRole = localStorage.getItem('role'); // ✅ 사용자 역할 확인용

  useEffect(() => {
    const fetchBoards = async () => {
      try {
        const sortParam = type !== 'notice' ? `&sort=${sort}` : "";
        const url = ticketId
          ? `/api/boards/tickets/${ticketId}/boards?type=${type}`
          : `/api/boards?type=${type}${sortParam}`;

        const res = await axios.get(url);
        setBoards(res.data);
        setCurrentPage(1);
      } catch (err) {
        console.error("게시글 불러오기 실패", err);
      }
    };

    fetchBoards();
  }, [type, ticketId, sort]);

  const handleWriteClick = () => {
    const query = `?type=${type}` + (ticketId ? `&ticketId=${ticketId}` : '');
    navigate(`/boards/new${query}`);
  };

  const indexOfLast = currentPage * postsPerPage;
  const indexOfFirst = indexOfLast - postsPerPage;
  const currentBoards = boards.slice(indexOfFirst, indexOfLast);
  const totalPages = Math.ceil(boards.length / postsPerPage);

  return (
    <div className="board-list-container">
      {/* 제목 */}
      <div className="board-title-wrapper">
        <h1 className="board-title">{type === 'notice' ? '공지사항' : '게시글'}</h1>
      </div>

      {/* 정렬 및 글쓰기 */}
      <div className="board-list-controls">
        {type !== 'notice' && !ticketId ? (
          <select
            value={sort}
            onChange={(e) => setSort(e.target.value)}
            className="sort-select"
          >
            <option value="latest">최신순</option>
            <option value="views">조회수순</option>
          </select>
        ) : (
          <div style={{ width: '120px' }}></div>
        )}

        {(type !== 'notice' || userRole === 'ADMIN') && (
          <button className="write-button" onClick={handleWriteClick}>
            글쓰기
          </button>
        )}
      </div>

      {/* 게시글 테이블 */}
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
            <tr key={board.id}>
              <td>{board.type === 'notice' ? "공지사항" : boards.length - (indexOfFirst + index)}</td>
              <td>{board.ticketTitle || ""}</td>
              <td>
                <Link to={`/boards/${board.id}`}>
                  {board.type === 'notice' && <span className="notice-label">[공지사항] </span>}
                  {board.title}
                </Link>
              </td>
              <td>{board.nickname}</td>
              <td>{board.createdAt?.slice(0, 10)}</td>
            </tr>
          ))}
        </tbody>
      </table>

      {/* 페이지네이션 */}
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
 