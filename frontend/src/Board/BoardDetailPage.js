import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './BoardDetailPage.css';

const BoardDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const [board, setBoard] = useState(null);
  const [comments, setComments] = useState([]);
  const [newComment, setNewComment] = useState('');
  const [error, setError] = useState(null);

  const user = {
    id: localStorage.getItem('userId'),
    role: localStorage.getItem('role'),
  };

  useEffect(() => {
    const token = localStorage.getItem('accessToken');

    axios.get(`http://localhost:8080/api/boards/${id}`, {
      headers: { Authorization: token ? `Bearer ${token}` : '' },
    })
      .then(res => setBoard(res.data))
      .catch(err => {
        console.error("❌ 게시글 조회 실패", err);
        setError('게시글을 불러오지 못했습니다.');
      });

    axios.get(`http://localhost:8080/api/comments?boardId=${id}`, {
      headers: { Authorization: token ? `Bearer ${token}` : '' },
    })
      .then(res => setComments(res.data))
      .catch(err => {
        console.error("❌ 댓글 조회 실패", err);
        setError('댓글을 불러오지 못했습니다.');
      });
  }, [id]);

  const handleCommentSubmit = () => {
    const token = localStorage.getItem('accessToken');

    if (!token) {
      alert('로그인이 필요합니다.');
      return;
    }

    if (!newComment.trim()) {
      alert('댓글을 입력하세요.');
      return;
    }

    axios.post('http://localhost:8080/api/comments', {
      content: newComment,
      boardId: id
    }, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => {
        setComments(prev => [...prev, res.data]);
        setNewComment('');
      })
      .catch(err => {
        console.error("❌ 댓글 작성 실패", err);
        alert('댓글 작성 실패');
      });
  };

  const handleDeleteComment = (commentId) => {
    const token = localStorage.getItem('accessToken');
    if (!window.confirm("댓글을 삭제하시겠습니까?")) return;

    axios.delete(`http://localhost:8080/api/comments/${commentId}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(() => {
        setComments(prev => prev.filter(c => c.id !== commentId));
      })
      .catch(err => {
        console.error("❌ 댓글 삭제 실패", err);
        alert("댓글 삭제에 실패했습니다.");
      });
  };

  const handleDeleteBoard = () => {
    const token = localStorage.getItem('accessToken');
    if (!window.confirm("게시글을 삭제하시겠습니까?")) return;

    axios.delete(`http://localhost:8080/api/boards/${id}`, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(() => {
        alert("게시글이 삭제되었습니다.");
        navigate("/board");
      })
      .catch(err => {
        console.error("❌ 게시글 삭제 실패", err);
        alert("게시글 삭제에 실패했습니다.");
      });
  };

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!board) return <p>로딩 중...</p>;

  const isOwnerOrAdmin = user?.id === String(board.writerId) || user?.role === 'ADMIN';

  return (
    <div className="board-detail-container">
      {/* 제목 + 버튼 한 줄 */}
      <div className="board-title-row">
        <h1>{board.title}</h1>
        {isOwnerOrAdmin && (
          <div className="board-actions-inline">
            <button onClick={() => navigate(`/board/edit/${id}`)}>수정</button>
            <button onClick={handleDeleteBoard}>삭제</button>
          </div>
        )}
      </div>

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

      <div className="board-content">{board.content}</div>
      <hr />

      <div className="board-comments">
        <h2>댓글</h2>
        <ul>
          {comments.map(c => (
            <li key={c.id}>
              <div className="comment-header-inline">
                <span className="nickname">{c.nickname}</span>
                <span className="comment-date">· {new Date(c.createdAt).toLocaleString()}</span>
              </div>
              <div className="comment-content">{c.content}</div>
              {(user?.id === String(c.writerId) || user?.role === 'ADMIN') && (
                <div className="comment-actions">
                  <button onClick={() => handleDeleteComment(c.id)}>삭제</button>
                </div>
              )}
            </li>
          ))}
        </ul>
      </div>

      {user?.id ? (
        <div className="comment-form">
          <textarea
            value={newComment}
            onChange={e => setNewComment(e.target.value)}
            placeholder="댓글을 입력하세요"
          />
          <button onClick={handleCommentSubmit}>댓글 작성</button>
        </div>
      ) : (
        <p style={{ color: '#888' }}>댓글 작성은 로그인 후 이용하실 수 있습니다.</p>
      )}
    </div>
  );
};

export default BoardDetail;
