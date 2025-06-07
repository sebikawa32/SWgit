import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import './Myprofile.css';

const MyProfile = () => {
  const [profile, setProfile] = useState(null);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  // 로그인한 유저 ID와 토큰을 localStorage에서 가져옴
  const userId = localStorage.getItem('userId');
  const token = localStorage.getItem('token');

  useEffect(() => {
    // 로그인 정보 없으면 로그인 페이지로 이동
    if (!userId || !token) {
      setError('로그인이 필요합니다.');
      navigate('/login');
      return;
    }

    const fetchProfile = async () => {
      try {
        const res = await axios.get(`/api/users/${userId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          }
        });
     setProfile(res.data.data);
      } catch (err) {
        setError('프로필 정보를 불러오지 못했습니다.');
      }
    };

    fetchProfile();
  }, [userId, token, navigate]);

  // 에러가 있을 경우 빨간색으로 에러 메시지 표시
  if (error) return <p style={{ color: 'red' }}>{error}</p>;

  // 프로필 데이터 로딩 중일 때
  if (!profile) return <p>로딩 중...</p>;

  // 프로필 정보 렌더링
  return (
    <div className="my-profile">
      <h1>내 프로필</h1>
      <p><strong>이름:</strong> {profile.realname}</p>
<p><strong>닉네임:</strong> {profile.nickname}</p>
<p><strong>이메일:</strong> {profile.email}</p>
<p><strong>전화번호:</strong> {profile.phoneNumber}</p>
      {/* 필요하면 추가 프로필 정보도 여기에 */}
    </div>
  );
};

export default MyProfile;
