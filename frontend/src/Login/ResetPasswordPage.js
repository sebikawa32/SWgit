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
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirm, setShowConfirm] = useState(false);

  const containerStyle = {
    maxWidth: "440px",
    margin: "80px auto",
    padding: "40px",
    border: "1px solid #e0e0e0",
    borderRadius: "18px",
    backgroundColor: "#ffffff",
    boxShadow: "0 8px 24px rgba(0, 0, 0, 0.08)",
    textAlign: "center",
  };

  const inputStyle = {
    width: "100%",
    padding: "14px",
    margin: "16px 0",
    borderRadius: "12px",
    border: "1px solid #ccc",
    fontSize: "16px",
    backgroundColor: "#fcfcfc",
    boxSizing: "border-box",
  };

  const inputWithEyeStyle = {
    ...inputStyle,
    paddingRight: "44px",
  };

  const eyeIconStyle = {
    position: "absolute",
    right: "16px",
    top: "50%",
    transform: "translateY(-50%)",
    cursor: "pointer",
    color: "#888",
    fontSize: "18px",
  };

  const inputWrapperStyle = {
    position: "relative",
    width: "100%",
  };

  const buttonStyle = {
    width: "100%",
    padding: "14px",
    backgroundColor: "#111111",
    color: "#ffffff",
    border: "none",
    borderRadius: "12px",
    cursor: "pointer",
    fontSize: "16px",
    marginTop: "16px",
    transition: "background-color 0.2s ease",
  };

  const isValidPassword = (password) => {
    const lengthValid = password.length >= 8;
    const hasLetter = /[a-zA-Z]/.test(password);
    const hasNumber = /\d/.test(password);
    const hasSpecial = /[!@#$%^&*()_+=\-{}[\]:;"'<>,.?/]/.test(password);
    return lengthValid && hasLetter && hasNumber && hasSpecial;
  };

  const sendAuthCode = () => {
    axios
      .post(`/api/auth/email/reset-password/send`, { email })
      .then(() => {
        alert("인증코드를 전송했습니다.");
        setStep(2);
      })
      .catch(() => alert("이메일 전송 실패"));
  };

  const verifyCode = () => {
    axios
      .post(`/api/auth/email/reset-password/verify`, { email, code })
      .then(() => {
        alert("인증 성공");
        setStep(3);
      })
      .catch(() => alert("인증 실패"));
  };

  const resetPassword = () => {
    if (!isValidPassword(newPassword)) {
      alert("비밀번호는 8자 이상이며, 영문, 숫자, 특수문자를 포함해야 합니다.");
      return;
    }

    if (newPassword !== passwordConfirm) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    axios
      .post(`/api/users/reset-password`, {
        email,
        newPassword,
        passwordConfirm,
      })
      .then(() => {
        alert("비밀번호가 변경되었습니다. 로그인해 주세요.");
        navigate("/login");
      })
      .catch(() => alert("비밀번호 변경 실패"));
  };

  return (
    <div style={containerStyle}>
      <h2 style={{ marginBottom: "32px", fontSize: "24px", fontWeight: "700" }}>비밀번호 재설정</h2>

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
          <div style={inputWrapperStyle}>
            <input
              type={showPassword ? "text" : "password"}
              placeholder="새 비밀번호"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              style={inputWithEyeStyle}
            />
            <span onClick={() => setShowPassword(!showPassword)} style={eyeIconStyle}>
              <i className={`bi ${showPassword ? "bi-eye-slash-fill" : "bi-eye-fill"}`}></i>
            </span>
          </div>

          <div style={inputWrapperStyle}>
            <input
              type={showConfirm ? "text" : "password"}
              placeholder="비밀번호 확인"
              value={passwordConfirm}
              onChange={(e) => setPasswordConfirm(e.target.value)}
              style={inputWithEyeStyle}
            />
            <span onClick={() => setShowConfirm(!showConfirm)} style={eyeIconStyle}>
              <i className={`bi ${showConfirm ? "bi-eye-slash-fill" : "bi-eye-fill"}`}></i>
            </span>
          </div>

          <button onClick={resetPassword} style={buttonStyle}>비밀번호 재설정</button>
        </>
      )}
    </div>
  );
}

export default ResetPasswordPage;
