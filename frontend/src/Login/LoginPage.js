import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { GoogleOAuthProvider, GoogleLogin } from "@react-oauth/google";
import "./LoginPage.css";

function LoginPage({ setIsLoggedIn }) {
  const navigate = useNavigate();
  const [userId, setUserId] = useState("");
  const [password, setPassword] = useState("");

  const onLogin = () => {
    axios
      .post("http://localhost:8080/api/users/login", { userId, password })
      .then(async (res) => {
        alert("로그인 성공!");
        const { token, userId: userPk, role } = res.data.data;
        localStorage.setItem("accessToken", token);
        localStorage.setItem("userId", userPk);
        localStorage.setItem("role", role);
        localStorage.setItem("provider", "LOCAL");
        setIsLoggedIn(true);

        try {
          const meRes = await axios.get("http://localhost:8080/api/users/me", {
            headers: { Authorization: `Bearer ${token}` },
          });
          const profile = meRes.data.data;
          localStorage.setItem("nickname", profile.nickname || "");
          navigate("/", { replace: true });
          window.location.reload();
        } catch {
          alert("닉네임 불러오기 실패");
          navigate("/", { replace: true });
        }
      })
      .catch(() => {
        alert("아이디 혹은 비밀번호가 일치하지 않습니다.");
      });
  };

  const handleGoogleLoginSuccess = async (credentialResponse) => {
    const idToken = credentialResponse.credential;
    console.log("✅ 받은 구글 ID 토큰:", idToken);

    try {
      const res = await axios.post("http://localhost:8080/api/auth/google-login", { idToken });
      const { token, userId: userPk, role, nickname, provider } = res.data;

      localStorage.setItem("accessToken", token);
      localStorage.setItem("tempGoogleToken", token);
      localStorage.setItem("userId", userPk);
      localStorage.setItem("role", role);
      localStorage.setItem("nickname", nickname);
      localStorage.setItem("provider", provider);

      setIsLoggedIn(true);

      try {
        const meRes = await axios.get("http://localhost:8080/api/users/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        const profile = meRes.data.data;
        const needsAdditionalInfo =
          !profile.nickname || !profile.realname || !profile.phoneNumber;

        if (needsAdditionalInfo) {
          navigate("/additional-info", { replace: true });
        } else {
          navigate("/", { replace: true });
          window.location.reload();
        }
      } catch {
        alert("프로필 조회 실패, 추가 정보 입력이 필요합니다.");
        navigate("/additional-info", { replace: true });
      }
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

          <div className="divider">
            <span>또는</span>
          </div>

          <div className="sns-login-buttons">
            <div className="sns-button google">
              <GoogleLogin
                onSuccess={handleGoogleLoginSuccess}
                onError={() => console.log("구글 로그인 실패")}
                theme="outline"
                ux_mode="popup"
                render={(renderProps) => (
                  <button
                    className="custom-google-button"
                    onClick={renderProps.onClick}
                    disabled={renderProps.disabled}
                  >
                    <img
                      src="https://developers.google.com/identity/images/g-logo.png"
                      alt="Google logo"
                      style={{ width: 18, height: 18, marginRight: 8 }}
                    />
                    구글로 로그인
                  </button>
                )}
              />
            </div>
          </div>
        </div>
      </div>
    </GoogleOAuthProvider>
  );
}

export default LoginPage;
