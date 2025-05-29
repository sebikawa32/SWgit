import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "./SignupPage.css";

function SignupPage() {
  const navigate = useNavigate();

  // 사용자 입력 상태 관리
  const [form, setForm] = useState({
    userId: "",
    email: "",
    password: "",
    passwordConfirm: "",
    nickname: "",
    realname: "",
    phoneNumber: ""
  });

  // 입력값 변경 처리
  const onChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  // ✅ 아이디 중복확인 함수
  const checkUserId = () => {
    if (!form.userId) {
      alert("아이디를 입력해주세요!");
      return;
    }

    axios.post("http://localhost:8080/api/users/check-id", form.userId, {
      headers: {
        "Content-Type": "application/json"
      }
    })
    .then(res => {
      if (res.data.data === true) {
        alert("이미 사용 중인 아이디입니다.");
      } else {
        alert("사용 가능한 아이디입니다!");
      }
    })
    .catch(err => {
      console.error(err);
      alert("아이디 중복 검사 실패");
    });
  };

  // ✅ 회원가입 + 토큰 저장 + 홈 이동
const onSignup = () => {
  axios.post("http://localhost:8080/api/users/signup", form)
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
      console.error(err);
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

        <div style={{ display: "flex", alignItems: "center", gap: "8px" }}>
          <input
            type="text"
            name="userId"
            placeholder="아이디"
            value={form.userId}
            onChange={onChange}
          />
          <button type="button" onClick={checkUserId}>중복확인</button>
        </div>

        <input
          type="password"
          name="password"
          placeholder="비밀번호"
          value={form.password}
          onChange={onChange}
        />
        <input
          type="password"
          name="passwordConfirm"
          placeholder="비밀번호 확인"
          value={form.passwordConfirm}
          onChange={onChange}
        />
        <input
          type="email"
          name="email"
          placeholder="이메일"
          value={form.email}
          onChange={onChange}
        />
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

        <button onClick={onSignup}>회원가입</button>

        <p className="login-text">
          이미 계정이 있으신가요? <a href="/login">로그인</a>
        </p>
      </div>
    </div>
  );
}

export default SignupPage;


