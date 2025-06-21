import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './BoardDetailPage.css';

// UTC → KST 변환 및 YYYY-MM-DD HH:mm:ss 포맷 함수
function toKST(dateStr) {
  if (!dateStr) return "";
  const utc = new Date(dateStr);
  const kst = new Date(utc.getTime() + 9 * 60 * 60 * 1000);
  const Y = kst.getFullYear();
  const M = String(kst.getMonth() + 1).padStart(2, "0");
  const D = String(kst.getDate()).padStart(2, "0");
  const h = String(kst.getHours()).padStart(2, "0");
  const m = String(kst.getMinutes()).padStart(2, "0");
  const s = String(kst.getSeconds()).padStart(2, "0");
  return `${Y}-${M}-${D} ${h}:${m}:${s}`;
}

const BoardDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const apiUrl = process.env.REACT_APP_API_URL; // ✅ 환경변수 선언

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
    const headers = { Authorization: token ? `Bearer ${token}` : '' };

    const fetchBoardAndComments = async () => {
      try {
        const boardRes = await axios.get(`${apiUrl}/boards/${id}`, { headers });
        setBoard(boardRes.data);

        try {
          // ✅ 댓글 조회 API 경로 수정
          const commentRes = await axios.get(`${apiUrl}/comments?boardId=${id}`, { headers });
          setComments(commentRes.data);
        } catch (commentErr) {
          console.error("❌ 댓글 조회 실패", commentErr);
          setComments([]); // fallback
        }
      } catch (boardErr) {
        console.error("❌ 게시글 조회 실패", boardErr);
        setError('게시글을 불러오지 못했습니다.');
      }
    };

    fetchBoardAndComments();
  }, [id, apiUrl]);

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

    axios.post(`${apiUrl}/comments`, {
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

    axios.delete(`${apiUrl}/comments/${commentId}`, {
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

    axios.delete(`${apiUrl}/boards/${id}`, {
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
        <div>작성일: {toKST(board.createdAt)}</div>
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
                <span className="comment-date">
                  · {toKST(c.createdAt)}
                </span>
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
