import React, { useEffect, useState } from 'react';
import axios from 'axios';
import './UserProfilePage.css';

function UserProfilePage() {
  const [profile, setProfile] = useState({
    id: null,
    email: '',
    nickname: '',
    realname: '',
    phoneNumber: '',
    provider: '',
  });

  const [editMode, setEditMode] = useState(false);
  const [editSuccess, setEditSuccess] = useState(false);

  const [passwordData, setPasswordData] = useState({
    currentPassword: '',
    newPassword: '',
  });
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showPassword, setShowPassword] = useState({
    current: false,
    new: false,
    confirm: false,
  });
  const [passwordSuccess, setPasswordSuccess] = useState(false);
  const [passwordError, setPasswordError] = useState('');
  const [validationError, setValidationError] = useState('');

  const fetchProfile = () => {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    axios.get('/api/users/me', {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(res => {
        const userData = res.data?.data || {};
        setProfile(userData);
      })
      .catch(err => {
        console.error('프로필 조회 실패:', err);
      });
  };

  useEffect(() => {
    fetchProfile();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProfile(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    const token = localStorage.getItem('accessToken');
    if (!token) return;

    axios.put('/api/users/me', profile, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(() => {
        setEditSuccess(true);
        setEditMode(false);
        setTimeout(() => setEditSuccess(false), 3000);
      })
      .catch(err => {
        console.error('정보 수정 실패:', err);
      });
  };

  const handleCancel = () => {
    fetchProfile();
    setEditMode(false);
  };

  const handlePasswordChange = (e) => {
    const { name, value } = e.target;
    setPasswordData(prev => ({ ...prev, [name]: value }));
  };

  const validatePassword = (password) => {
    return password.length >= 8 && /[a-zA-Z]/.test(password) && /\d/.test(password);
  };

  const handleChangePassword = () => {
    const { currentPassword, newPassword } = passwordData;

    if (!validatePassword(newPassword)) {
      setValidationError('비밀번호는 8자 이상이며, 영문과 숫자를 포함해야 합니다.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setValidationError('새 비밀번호와 확인이 일치하지 않습니다.');
      return;
    }

    const token = localStorage.getItem('accessToken');
    if (!token) return;

    axios.post('/api/users/me/change-password', passwordData, {
      headers: { Authorization: `Bearer ${token}` }
    })
      .then(() => {
        setPasswordSuccess(true);
        setPasswordError('');
        setValidationError('');
        setPasswordData({ currentPassword: '', newPassword: '' });
        setConfirmPassword('');
        setTimeout(() => setPasswordSuccess(false), 3000);
      })
      .catch(err => {
        console.error('비밀번호 변경 실패:', err);
        setPasswordError('현재 비밀번호가 올바르지 않거나 오류가 발생했습니다.');
      });
  };

  const isGoogleUser = (profile.provider || '').toLowerCase() === 'google';
  const isIncomplete = !profile.nickname || !profile.realname || !profile.phoneNumber;

  return (
    <div className="userprofile-container">
      <h2>내 프로필</h2>

      {isGoogleUser && (
        isIncomplete ? (
          <div className="google-warning-box">
            <p>🔔 구글 연동 계정입니다. 추가 정보를 등록해주세요.</p>
            <button onClick={() => setEditMode(true)}>정보 등록하기</button>
          </div>
        ) : (
          <p style={{ color: 'gray' }}>이 계정은 구글 연동 계정입니다. 비밀번호 변경은 불가능합니다.</p>
        )
      )}

      <div className="userprofile-form">
        <label>이메일</label>
        <input type="email" value={profile.email} readOnly />

        <label>닉네임</label>
        {editMode ? (
          <input type="text" name="nickname" value={profile.nickname} onChange={handleChange} />
        ) : (
          <p>{profile.nickname}</p>
        )}

        <label>이름</label>
        {editMode ? (
          <input type="text" name="realname" value={profile.realname} onChange={handleChange} />
        ) : (
          <p>{profile.realname}</p>
        )}

        <label>전화번호</label>
        {editMode ? (
          <input type="text" name="phoneNumber" value={profile.phoneNumber} onChange={handleChange} />
        ) : (
          <p>{profile.phoneNumber}</p>
        )}

        {editMode ? (
          <div className="button-group">
            <button onClick={handleSave}>저장</button>
            <button onClick={handleCancel}>취소</button>
          </div>
        ) : (
          !isIncomplete && <button onClick={() => setEditMode(true)}>수정하기</button>
        )}

        {editSuccess && <p className="success-msg">정보가 성공적으로 수정되었습니다.</p>}
      </div>

      <hr style={{ margin: '40px 0' }} />

      {!isGoogleUser && (
        <div className="userprofile-form">
          <h3>비밀번호 변경</h3>

          <label>현재 비밀번호</label>
          <div className="input-with-eye">
            <input
              type={showPassword.current ? 'text' : 'password'}
              name="currentPassword"
              value={passwordData.currentPassword}
              onChange={handlePasswordChange}
            />
            <span className="eye-toggle" onClick={() => setShowPassword(prev => ({ ...prev, current: !prev.current }))}>
              <i className={`bi ${showPassword.current ? 'bi-eye-slash-fill' : 'bi-eye-fill'}`}></i>
            </span>
          </div>

          <label>새 비밀번호</label>
          <div className="input-with-eye">
            <input
              type={showPassword.new ? 'text' : 'password'}
              name="newPassword"
              value={passwordData.newPassword}
              onChange={handlePasswordChange}
            />
            <span className="eye-toggle" onClick={() => setShowPassword(prev => ({ ...prev, new: !prev.new }))}>
              <i className={`bi ${showPassword.new ? 'bi-eye-slash-fill' : 'bi-eye-fill'}`}></i>
            </span>
          </div>

          <label>새 비밀번호 확인</label>
          <div className="input-with-eye">
            <input
              type={showPassword.confirm ? 'text' : 'password'}
              value={confirmPassword}
              onChange={e => setConfirmPassword(e.target.value)}
            />
            <span className="eye-toggle" onClick={() => setShowPassword(prev => ({ ...prev, confirm: !prev.confirm }))}>
              <i className={`bi ${showPassword.confirm ? 'bi-eye-slash-fill' : 'bi-eye-fill'}`}></i>
            </span>
          </div>

          <button onClick={handleChangePassword}>비밀번호 변경</button>

          {passwordSuccess && <p className="success-msg">비밀번호가 성공적으로 변경되었습니다.</p>}
          {passwordError && <p className="error-msg">{passwordError}</p>}
          {validationError && <p className="error-msg">{validationError}</p>}
        </div>
      )}
    </div>
  );
}

export default UserProfilePage;
