import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

function ResetPasswordPage() {
  const navigate = useNavigate();

  const [step, setStep] = useState(1);
  const [email, setEmail] = useState("");
  const [code, setCode] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [passwordConfirm, setPasswordConfirm] = useState("");

  const containerStyle = {
    maxWidth: "400px",
    margin: "60px auto",
    padding: "30px",
    border: "1px solid #ccc",
    borderRadius: "10px",
    backgroundColor: "#f9f9f9",
    boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
    textAlign: "center",
  };

  const inputStyle = {
    width: "100%",
    padding: "10px",
    margin: "10px 0",
    borderRadius: "5px",
    border: "1px solid #ddd",
    fontSize: "16px",
  };

const buttonStyle = {
  width: "100%",
  padding: "10px",
  backgroundColor: "#000000", // 검은색 배경
  color: "#ffffff",           // 흰색 글씨
  border: "none",
  borderRadius: "5px",
  cursor: "pointer",
  fontSize: "16px",
  marginTop: "10px",
};

  const sendAuthCode = () => {
    axios
      .post("http://localhost:8080/api/auth/email/reset-password/send", { email })
      .then(() => {
        alert("인증코드를 전송했습니다.");
        setStep(2);
      })
      .catch(() => alert("이메일 전송 실패"));
  };

  const verifyCode = () => {
    axios
      .post("http://localhost:8080/api/auth/email/reset-password/verify", { email, code })
      .then(() => {
        alert("인증 성공");
        setStep(3);
      })
      .catch(() => alert("인증 실패"));
  };

  const resetPassword = () => {
    if (newPassword !== passwordConfirm) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    axios
      .post("http://localhost:8080/api/users/reset-password", { email, newPassword })
      .then(() => {
        alert("비밀번호가 변경되었습니다. 로그인해 주세요.");
        navigate("/login");
      })
      .catch(() => alert("비밀번호 변경 실패"));
  };

  return (
    <div style={containerStyle}>
      <h2>비밀번호 재설정</h2>

      {step === 1 && (
        <>
          <input
            type="email"
            placeholder="이메일 입력"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            style={inputStyle}
          />
          <button onClick={sendAuthCode} style={buttonStyle}>인증코드 전송</button>
        </>
      )}

      {step === 2 && (
        <>
          <input
            type="text"
            placeholder="인증코드 입력"
            value={code}
            onChange={(e) => setCode(e.target.value)}
            style={inputStyle}
          />
          <button onClick={verifyCode} style={buttonStyle}>인증코드 확인</button>
        </>
      )}

      {step === 3 && (
        <>
          <input
            type="password"
            placeholder="새 비밀번호"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            style={inputStyle}
          />
          <input
            type="password"
            placeholder="비밀번호 확인"
            value={passwordConfirm}
            onChange={(e) => setPasswordConfirm(e.target.value)}
            style={inputStyle}
          />
          <button onClick={resetPassword} style={buttonStyle}>비밀번호 재설정</button>
        </>
      )}
    </div>
  );
}

export default ResetPasswordPage;
