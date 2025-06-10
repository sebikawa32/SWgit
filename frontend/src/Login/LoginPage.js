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

        // ✅ 백엔드 응답에서 토큰과 userId(PK) 추출
        const { token, userId: userPk } = res.data.data;

        // ✅ 토큰과 userId(PK) 저장
       localStorage.setItem("accessToken", token); 
        localStorage.setItem("userId", userPk);

        // ✅ 닉네임 조회해서 저장
        axios
          .get("http://localhost:8080/api/users/me", {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          })
          .then((meRes) => {
            const nickname = meRes.data.data.nickname;
            localStorage.setItem("nickname", nickname);

            // 홈으로 이동하고 새로고침
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

        <p className="signup-text">
          아직 계정이 없나요? <a href="/signup">회원가입</a>
        </p>
      </div>
    </div>
  );
}

export default LoginPage;
