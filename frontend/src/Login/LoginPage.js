import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom"; 
import "./LoginPage.css";

function LoginPage() {
  const navigate = useNavigate();
  const [userId, setUserId] = useState("");   // 로그인 입력 아이디
  const [password, setPassword] = useState("");

  const onLogin = () => {
    axios.post("http://localhost:8080/api/users/login", {
      userId,
      password,
    })
    .then(res => {
      alert("로그인 성공!");

      // ✅ 백엔드 응답에서 token과 userId(PK)를 받아오기
      const { token, userId: userPk } = res.data.data;

      // ✅ 토큰과 userId(PK)를 localStorage에 저장
      localStorage.setItem("token", token);
      localStorage.setItem("userId", userPk);   // 🔥 백엔드 응답에서 받은 userId(PK)를 저장!

      navigate("/", { replace: true }); // 🚀 홈으로 이동
      window.location.reload(); // 🚀 새로고침으로 로그인 상태 반영!
    })
    .catch(() => {
      alert("로그인 실패");
    });
  };

  return (
    <div className="login-page-wrapper">
      <div className="login-container">
        <h2>TicketPlanet 로그인</h2>

        <input
          type="text"
          placeholder="아이디"
          value={userId}
          onChange={(e) => setUserId(e.target.value)}
        />
        <input
          type="password"
          placeholder="비밀번호"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        {/* ✅ 로그인 버튼 클릭시 onLogin 연결 */}
        <button onClick={onLogin}>로그인</button>

        <p className="signup-text">
          아직 계정이 없나요? <a href="/signup">회원가입</a>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
