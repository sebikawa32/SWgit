import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./GoogleSignupPage.css";

function GoogleSignupPage() {
  const navigate = useNavigate();

  const token = localStorage.getItem("tempGoogleToken"); // 임시 저장된 토큰

  const [formData, setFormData] = useState({
    nickname: "",
    realname: "",
    phoneNumber: "",
  });

  const [error, setError] = useState("");

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const onSubmit = async () => {
    if (!token) {
      alert("인증 토큰이 없습니다. 다시 로그인 해주세요.");
      navigate("/login");
      return;
    }

    try {
      await axios.put(
        "/users/me/google-additional-info",  // ✅ 수정된 경로
        formData,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      alert("회원가입이 완료되었습니다.");
      localStorage.removeItem("tempGoogleToken");
      navigate("/"); // 홈으로 이동
    } catch (err) {
      setError(err.response?.data?.message || "회원가입 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="google-signup-wrapper">
      <h2>추가 회원정보 입력</h2>
      {/* 아이디는 구글 유저에게 필요 없으므로 제거 */}
      <input
        type="text"
        name="nickname"
        placeholder="닉네임"
        value={formData.nickname}
        onChange={handleChange}
      />
      <input
        type="text"
        name="realname"
        placeholder="이름"
        value={formData.realname}
        onChange={handleChange}
      />
      <input
        type="text"
        name="phoneNumber"
        placeholder="전화번호"
        value={formData.phoneNumber}
        onChange={handleChange}
      />

      <button onClick={onSubmit}>가입 완료</button>
      {error && <p style={{ color: "red" }}>{error}</p>}
    </div>
  );
}

export default GoogleSignupPage;
