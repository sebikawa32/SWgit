import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { GoogleOAuthProvider, GoogleLogin } from "@react-oauth/google";
import "./LoginPage.css";

function LoginPage({ setIsLoggedIn }) {
  const navigate = useNavigate();
  const [userId, setUserId] = useState("");
  const [password, setPassword] = useState("");

  // 일반 로그인
  const onLogin = () => {
    axios
      .post("http://localhost:8080/api/users/login", {
        userId,
        password,
      })
      .then((res) => {
        alert("로그인 성공!");

        const { token, userId: userPk, role } = res.data.data;
        localStorage.setItem("accessToken", token);
        localStorage.setItem("userId", userPk);
        localStorage.setItem("role", role);
        localStorage.setItem("provider", "LOCAL"); // ✅ 일반 로그인은 LOCAL로 저장

        setIsLoggedIn(true);  // ← 로그인 상태 반영 추가

        // 사용자 닉네임도 가져오기
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
        alert("아이디 혹은 비밀번호가 일치하지 않습니다.");
      });
  };

  // 구글 로그인 성공 시 처리
  const handleGoogleLoginSuccess = async (credentialResponse) => {
    const idToken = credentialResponse.credential;
    console.log("✅ 받은 구글 ID 토큰:", idToken);

    try {
      const res = await axios.post("http://localhost:8080/api/auth/google-login", {
        idToken,
      });

      const { token, userId: userPk, role, nickname, provider } = res.data;

      localStorage.setItem("accessToken", token);
      localStorage.setItem("userId", userPk);
      localStorage.setItem("role", role);
      localStorage.setItem("nickname", nickname);
      localStorage.setItem("provider", provider); // ✅ provider 저장 (예: GOOGLE)

      setIsLoggedIn(true);  // ← 로그인 상태 반영 추가

      alert("구글 로그인 성공!");
      navigate("/", { replace: true });
      window.location.reload();
    } catch (err) {
      if (err.response?.status === 409) {
        alert("이미 일반 회원으로 가입된 이메일입니다. 일반 로그인을 이용해 주세요.");
      } else {
        alert("구글 로그인 실패");
        console.error(err);
      }
    }
  };

  return (
    <GoogleOAuthProvider clientId="770869707380-tsrqjmdd8s4u1tssoqkqf5un7din8u9g.apps.googleusercontent.com">
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
            <a href="/reset-password">비밀번호를 잊으셨나요?</a>
          </p>

          <p className="signup-text">
            아직 계정이 없나요? <a href="/signup">회원가입</a>
          </p>

          <div className="divider"><span>또는</span></div>

          <div className="sns-login-buttons">
            <div className="sns-button google">
              <GoogleLogin
                onSuccess={handleGoogleLoginSuccess}
                onError={() => console.log("구글 로그인 실패")}
              />
            </div>
          </div>
        </div>
      </div>
    </GoogleOAuthProvider>
  );
}

export default LoginPage;
