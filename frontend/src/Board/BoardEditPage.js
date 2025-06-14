import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

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
    axios.get(`http://localhost:8080/api/boards/${id}`)
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
      type: board.type || 'general',     // ✅ 기본값 보장
      ticketId: board.ticketId || null,  // ✅ null 허용
    };

    console.log("📤 수정 전송 payload:", payload); // 확인용

    axios.put(`http://localhost:8080/api/boards/${id}`, payload, {
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
    <div style={{
      maxWidth: '600px',
      margin: '40px auto',
      padding: '24px',
      border: '1px solid #ccc',
      borderRadius: '8px',
      fontFamily: 'Segoe UI, sans-serif'
    }}>
      <h2 style={{ marginBottom: '20px' }}>{isNotice ? '공지사항 수정' : '게시글 수정'}</h2>

      <input
        value={board.title}
        onChange={e => setBoard({ ...board, title: e.target.value })}
        placeholder="제목"
        style={{ width: '100%', padding: '10px', marginBottom: '12px' }}
      />

      <textarea
        value={board.content}
        onChange={e => setBoard({ ...board, content: e.target.value })}
        placeholder="내용"
        style={{ width: '100%', padding: '10px', minHeight: '160px', marginBottom: '12px' }}
      />

      <button onClick={handleUpdate} style={{
        width: '100%',
        padding: '12px',
        backgroundColor: '#111',
        color: '#fff',
        border: 'none',
        borderRadius: '4px',
        fontWeight: 'bold',
        cursor: 'pointer'
      }}>
        수정 완료
      </button>
    </div>
  );
};

export default BoardEditPage;
