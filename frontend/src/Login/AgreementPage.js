import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./SignupPage.css";

function AgreementPage() {
  const navigate = useNavigate();
  const [agreements, setAgreements] = useState({
    all: false,
    termsOfService: false,
    privacyPolicy: false,
    marketing: false,
  });

  const [showTerms, setShowTerms] = useState(false);
  const [showPrivacy, setShowPrivacy] = useState(false);
  const [showMarketing, setShowMarketing] = useState(false);

  const onChange = (e) => {
    const { name, checked } = e.target;
    if (name === "all") {
      setAgreements({
        all: checked,
        termsOfService: checked,
        privacyPolicy: checked,
        marketing: checked,
      });
    } else {
      const updated = { ...agreements, [name]: checked };
      updated.all =
        updated.termsOfService && updated.privacyPolicy && updated.marketing;
      setAgreements(updated);
    }
  };

  const onNext = () => {
    if (!agreements.termsOfService || !agreements.privacyPolicy) {
      alert("필수 약관에 모두 동의해주세요.");
      return;
    }
    navigate("/signup/form", { state: agreements });
  };

  return (
    <div className="signup-page-wrapper">
      <div className="signup-container">
        <h2>이용 약관에 동의해주세요</h2>

        <div className="agreement-section">
          <label className="agreement-all">
            <input
              type="checkbox"
              name="all"
              checked={agreements.all}
              onChange={onChange}
            />
            <strong>전체 동의</strong>
            <p className="agreement-description">
              아래 필수 및 선택 항목을 모두 확인하고 동의합니다.
            </p>
          </label>

          <hr />

          {/* 이용약관 */}
          <label>
            <input
              type="checkbox"
              name="termsOfService"
              checked={agreements.termsOfService}
              onChange={onChange}
            />
            [필수] 이용약관 동의
          </label>
          <div className="agreement-box scrollable">
            {showTerms ? (
              <p>
                제1조(목적)<br />
                본 약관은 TicketPlanet이 제공하는 서비스의 이용과 관련하여 회사와 회원 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.<br /><br />
                제2조(정의)<br />
                "회원"이란 본 약관에 동의하고 서비스를 이용하는 이용자를 말합니다.<br /><br />
                제3조(회원가입)<br />
                회원 가입은 사용자의 동의 하에 이루어지며, 부정확한 정보 제공 시 서비스 이용에 제한이 있을 수 있습니다.<br /><br />
                제4조(서비스 이용)<br />
                회사는 연중무휴, 1일 24시간 서비스를 제공합니다. 다만, 시스템 점검 등의 사유로 일시 중지될 수 있습니다.<br /><br />
                제5조(계약해지 및 서비스 중지)<br />
                회원은 언제든지 탈퇴할 수 있으며, 부정 이용 시 계정이 제한될 수 있습니다.
              </p>
            ) : (
              <>
                <p className="preview-text">
                  제1조(목적) 본 약관은 TicketPlanet이 제공하는 서비스 이용과 관련하여...<br />
                  "회원"이란 본 약관에 동의하고 서비스를 이용하는 이용자를 말합니다.
                </p>
                <p className="blurred-text">...전체 보기</p>
              </>
            )}
            <button className="expand-button" onClick={() => setShowTerms(!showTerms)}>
              {showTerms ? "접기" : "펼치기"}
            </button>
          </div>

          {/* 개인정보 */}
          <label>
            <input
              type="checkbox"
              name="privacyPolicy"
              checked={agreements.privacyPolicy}
              onChange={onChange}
            />
            [필수] 개인정보 수집 및 이용 동의
          </label>
          <div className="agreement-box scrollable">
            {showPrivacy ? (
              <p>
                1. 수집 항목: 이름, 이메일, 휴대폰 번호, 아이디 등<br />
                2. 수집 목적: 회원가입, 본인 확인, 서비스 이용 기록 분석<br />
                3. 보유 기간: 회원 탈퇴 시까지, 단 법령에 의해 보관이 필요한 경우 해당 기간 동안 보관<br /><br />
                당사는 수집한 개인정보를 명시된 목적 외로 사용하지 않으며, 회원의 동의 없이 제3자에게 제공하지 않습니다.
              </p>
            ) : (
              <>
                <p className="preview-text">
                  이름, 이메일, 휴대폰 번호 등은 회원가입 및 본인확인을 위해 수집됩니다.
                </p>
                <p className="blurred-text">...전체 보기</p>
              </>
            )}
            <button className="expand-button" onClick={() => setShowPrivacy(!showPrivacy)}>
              {showPrivacy ? "접기" : "펼치기"}
            </button>
          </div>

          {/* 마케팅 */}
          <label>
            <input
              type="checkbox"
              name="marketing"
              checked={agreements.marketing}
              onChange={onChange}
            />
            [선택] 마케팅 정보 수신 동의
          </label>
          <div className="agreement-box scrollable small">
            {showMarketing ? (
              <p>
                마케팅 수신 동의를 하시면 TicketPlanet에서 제공하는 공연 소식, 할인 정보, 이벤트 혜택을 문자 또는 이메일로 받아보실 수 있습니다.<br />
                수신을 원하지 않으시면 언제든지 철회할 수 있습니다.
              </p>
            ) : (
              <>
                <p className="preview-text">
                  공연 소식, 할인 정보 등을 이메일이나 문자로 받아보실 수 있습니다.
                </p>
                <p className="blurred-text">...전체 보기</p>
              </>
            )}
            <button className="expand-button" onClick={() => setShowMarketing(!showMarketing)}>
              {showMarketing ? "접기" : "펼치기"}
            </button>
          </div>
        </div>

        <button onClick={onNext} className="submit-button">
          다음 단계로
        </button>
      </div>
    </div>
  );
}

export default AgreementPage;