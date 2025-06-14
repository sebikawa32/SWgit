import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./LoginPage.css";

function LoginPage() {
  const navigate = useNavigate();
  const [userId, setUserId] = useState("");
  const [password, setPassword] = useState("");

  const onLogin = () => {
    axios
      .post("http://localhost:8080/api/users/login", {
        userId,
        password,
      })
      .then((res) => {
        alert("로그인 성공!");

        const { token, userId: userPk } = res.data.data;
        localStorage.setItem("accessToken", token);
        localStorage.setItem("userId", userPk);

        axios
          .get("http://localhost:8080/api/users/me", {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          })
          .then((meRes) => {
            const nickname = meRes.data.data.nickname;
            localStorage.setItem("nickname", nickname);
            navigate("/", { replace: true });
            window.location.reload();
          })
          .catch(() => {
            alert("닉네임 불러오기 실패");
          });
      })
      .catch(() => {
        alert("로그인 실패");
      });
  };

  return (
    <div className="login-page-wrapper">
      <div className="login-container">
        <h2>TicketPlanet 로그인</h2>

        {/* 일반 로그인 입력창 */}
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

        <button onClick={onLogin}>로그인</button>

        {/* 회원가입 안내 */}
        <p className="signup-text">
          아직 계정이 없나요? <a href="/signup">회원가입</a>
        </p>

        {/* 구분선 */}
        <div className="divider"><span>또는</span></div>

        {/* SNS 로그인 버튼들 (UI만, 연동은 추후) */}
        <div className="sns-login-buttons">
          <button className="sns-button kakao">카카오로 로그인</button>
          <button className="sns-button naver">네이버로 로그인</button>
          <button className="sns-button google">구글로 로그인</button>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
