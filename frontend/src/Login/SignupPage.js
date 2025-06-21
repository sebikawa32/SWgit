import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./SignupPage.css";

function SignupPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    userId: "",
    email: "",
    password: "",
    passwordConfirm: "",
    nickname: "",
    realname: "",
    phoneNumber: ""
  });

  const [emailCode, setEmailCode] = useState("");
  const [emailVerified, setEmailVerified] = useState(false);

  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);

  const onChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const checkUserId = () => {
    if (!form.userId) {
      alert("아이디를 입력해주세요!");
      return;
    }

    axios
      .post(`/api/users/check-id`, form.userId, {
        headers: { "Content-Type": "application/json" }
      })
      .then(res => {
        if (res.data.data === true) {
          alert("이미 사용 중인 아이디입니다.");
        } else {
          alert("사용 가능한 아이디입니다!");
        }
      })
      .catch(() => {
        alert("아이디 중복 검사 실패");
      });
  };

  const sendEmailCode = () => {
    if (!form.email) {
      alert("이메일을 입력해주세요.");
      return;
    }

    axios
      .post(`/api/auth/email/send?email=${form.email}`)
      .then(() => {
        alert("인증 코드가 이메일로 전송되었습니다.");
      })
      .catch(() => {
        alert("이메일 전송 실패");
      });
  };

  const verifyEmailCode = () => {
    axios
      .post(`/api/auth/email/verify?email=${form.email}&code=${emailCode}`)
      .then(() => {
        alert("이메일 인증 성공!");
        setEmailVerified(true);
      })
      .catch(() => {
        alert("인증 실패: 코드가 틀리거나 만료되었습니다.");
      });
  };

  const onSignup = () => {
    if (!emailVerified) {
      alert("이메일 인증을 완료해주세요.");
      return;
    }

    axios
      .post(`/api/users/signup`, form)
      .then((res) => {
        alert("회원가입 성공!");
        const token = res.data.data.token;
        if (token) {
          localStorage.setItem("token", token);
          alert("자동 로그인 완료!");
          navigate("/", { replace: true });
          window.location.reload();
        } else {
          navigate("/login");
        }
      })
      .catch((err) => {
        const message = err.response?.data || "회원가입 실패";
        alert(message);
      });
  };

  return (
    <div className="signup-page-wrapper">
      <div className="signup-container">
        <h2>TicketPlanet 회원가입</h2>

        <input
          type="text"
          name="realname"
          placeholder="이름"
          value={form.realname}
          onChange={onChange}
        />

        <div className="input-with-button">
          <input
            type="text"
            name="userId"
            placeholder="아이디"
            value={form.userId}
            onChange={onChange}
          />
          <button type="button" className="check-button" onClick={checkUserId}>
            중복확인
          </button>
        </div>

        <div className="input-with-eye">
          <input
            type={showPassword ? "text" : "password"}
            name="password"
            placeholder="비밀번호"
            value={form.password}
            onChange={onChange}
          />
          <span className="eye-toggle" onClick={() => setShowPassword(!showPassword)}>
            <i className={`bi ${showPassword ? "bi-eye-slash-fill" : "bi-eye-fill"}`}></i>
          </span>
        </div>

        <div className="input-with-eye">
          <input
            type={showConfirmPassword ? "text" : "password"}
            name="passwordConfirm"
            placeholder="비밀번호 확인"
            value={form.passwordConfirm}
            onChange={onChange}
          />
          <span className="eye-toggle" onClick={() => setShowConfirmPassword(!showConfirmPassword)}>
            <i className={`bi ${showConfirmPassword ? "bi-eye-slash-fill" : "bi-eye-fill"}`}></i>
          </span>
        </div>

        <div className="input-with-button">
          <input
            type="email"
            name="email"
            placeholder="이메일"
            value={form.email}
            onChange={onChange}
          />
          <button type="button" className="check-button" onClick={sendEmailCode}>
            인증 요청
          </button>
        </div>

        <div className="input-with-button">
          <input
            type="text"
            placeholder="인증 코드 입력"
            value={emailCode}
            onChange={(e) => setEmailCode(e.target.value)}
          />
          <button type="button" className="check-button" onClick={verifyEmailCode}>
            코드 확인
          </button>
        </div>

        <input
          type="text"
          name="phoneNumber"
          placeholder="휴대폰 번호"
          value={form.phoneNumber}
          onChange={onChange}
        />
        <input
          type="text"
          name="nickname"
          placeholder="닉네임"
          value={form.nickname}
          onChange={onChange}
        />

        <button className="submit-button" onClick={onSignup}>회원가입</button>

        <p className="login-text">
          이미 계정이 있으신가요? <a href="/login">로그인</a>
        </p>
      </div>
    </div>
  );
}

export default SignupPage;
