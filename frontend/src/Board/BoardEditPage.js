import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';
import './BoardCreatePage.css'; // 생성과 동일한 스타일 적용

const BoardEditPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const location = useLocation();

  const isNotice = location.pathname.startsWith('/notice/edit');
  const [board, setBoard] = useState({
    title: '',
    content: '',
    type: isNotice ? 'notice' : 'general',
    ticketId: null,
  });

  useEffect(() => {
    axios.get(`/api/boards/${id}`)
      .then(res => {
        setBoard({
          title: res.data.title,
          content: res.data.content,
          type: res.data.type || (isNotice ? 'notice' : 'general'),
          ticketId: res.data.ticketId || null,
        });
      })
      .catch(err => console.error("게시글 로딩 실패", err));
  }, [id, isNotice]);

  const handleUpdate = () => {
    const token = localStorage.getItem('accessToken');

    const payload = {
      title: board.title,
      content: board.content,
      type: board.type || 'general',
      ticketId: board.ticketId || null,
    };

    axios.put(`/api/boards/${id}`, payload, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(() => {
        alert("게시글이 수정되었습니다.");
        navigate(`/boards/${id}`);
      })
      .catch(err => {
        console.error("❌ 게시글 수정 실패:", err.response?.data || err);
        alert("수정 실패: " + (err.response?.data?.error || '알 수 없는 오류'));
      });
  };

  return (
    <div className="board-create-container">
      <h1>{isNotice ? '공지사항 수정' : '게시글 수정'}</h1>

      <div className="form-row">
        <label>제목</label>
        <input
          type="text"
          placeholder="제목을 입력하세요"
          value={board.title}
          onChange={e => setBoard({ ...board, title: e.target.value })}
        />
      </div>

      <div className="form-row">
        <label>내용</label>
        <textarea
          placeholder="내용을 입력하세요"
          value={board.content}
          onChange={e => setBoard({ ...board, content: e.target.value })}
        />
      </div>

      <div className="button-row">
        <button onClick={handleUpdate}>수정 완료</button>
      </div>
    </div>
  );
};

export default BoardEditPage;
