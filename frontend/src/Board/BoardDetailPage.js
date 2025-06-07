import React, { useEffect, useState } from 'react';
import { useParams, Link } from 'react-router-dom';
import axios from 'axios';
import "./BoardDetailPage.css";

const BoardDetail = () => {
  const { id } = useParams();
  const [board, setBoard] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [error, setError] = useState(null);

  const token = localStorage.getItem('token');

  useEffect(() => {
    axios.get(`/api/boards/${id}`)
      .then(res => setBoard(res.data))
      .catch(() => setError('게시글을 불러오지 못했습니다.'));

    axios.get(`/api/comments?boardId=${id}`, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(res => setComments(res.data))
      .catch(() => setError('댓글을 불러오지 못했습니다.'));
  }, [id, token]);

  const handleCommentSubmit = () => {
    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }

    axios.post('/api/comments', {
      content: newComment,
      boardId: id
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(res => {
        setComments([...comments, res.data]);
        setNewComment('');
      })
      .catch(() => alert('댓글 작성 실패'));
  };

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!board) return <p>로딩 중...</p>;

  return (
    <div className="board-detail-container">
      {/* 제목 */}
      <h1>{board.title}</h1>

      {/* 작성자, 날짜, 공연 */}
      <div className="board-meta">
        <div>작성자: {board.nickname}</div>
        <div>작성일: {new Date(board.createdAt).toLocaleString()}</div>
        {board.ticketId && board.ticketTitle && (
          <div>
            연결된 공연:{" "}
            <Link to={`/ticket/${board.ticketId}`} className="linked-ticket">
              {board.ticketTitle}
            </Link>
          </div>
        )}
      </div>

   {/* 본문 내용 */}
<div className="board-content">
  {board.content}
</div>
      <hr />

      {/* 댓글 목록 */}
      <div className="board-comments">
        <h2>댓글</h2>
        <ul>
          {comments.map(c => (
            <li key={c.id}>
              {c.content}
              <div style={{ fontSize: "0.85rem", color: "#666", marginTop: "4px" }}>
                작성자: {c.nickname}
              </div>
            </li>
          ))}
        </ul>
      </div>

      {/* 댓글 입력 */}
      <div className="comment-form">
        <textarea
          value={newComment}
          onChange={e => setNewComment(e.target.value)}
          placeholder="댓글을 입력하세요"
        />
        <button onClick={handleCommentSubmit}>댓글 작성</button>
      </div>
    </div>
  );
};

export default BoardDetail;
