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

  useEffect(() => {
    const token = localStorage.getItem('accessToken');
    console.log("🪪 게시글/댓글 조회 시 토큰:", token);

    axios.get(`http://localhost:8080/api/boards/${id}`, {
      headers: {
        Authorization: token ? `Bearer ${token}` : '',
      },
    })
      .then(res => setBoard(res.data))
      .catch(err => {
        console.error("❌ 게시글 조회 실패", err);
        setError('게시글을 불러오지 못했습니다.');
      });

    axios.get(`http://localhost:8080/api/comments?boardId=${id}`, {
      headers: {
        Authorization: token ? `Bearer ${token}` : '',
      },
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

    console.log("📝 댓글 작성 시 토큰:", token);

    axios.post('http://localhost:8080/api/comments', {
      content: newComment,
      boardId: id
    }, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(res => {
        setComments(prev => [...prev, res.data]);
        setNewComment('');
      })
      .catch((err) => {
        console.error("❌ 댓글 작성 실패", err);
        alert('댓글 작성 실패');
      });
  };

  if (error) return <p style={{ color: 'red' }}>{error}</p>;
  if (!board) return <p>로딩 중...</p>;

  return (
    <div className="board-detail-container">
      <h1>{board.title}</h1>
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
              {c.content}
              <div style={{ fontSize: "0.85rem", color: "#666", marginTop: "4px" }}>
                작성자: {c.nickname}
              </div>
            </li>
          ))}
        </ul>
      </div>

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
