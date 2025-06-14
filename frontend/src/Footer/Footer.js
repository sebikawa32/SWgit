import React from "react";
import "./Footer.css";

const Footer = () => {
  return (
    <footer className="footer">
      <div className="footer-links">
        <a href="#">회사소개</a>
        <a href="#">이용약관</a>
        <a href="#">개인정보처리방침</a>
        <a href="#">위치기반서비스 이용약관</a>
        <a href="#">티켓판매안내</a>
        <a href="#">공지사항</a>
      </div>

      <div className="footer-info">
        <p><strong>티켓플래닛 프로젝트</strong></p>
        <p>서경대학교 SW인재양성사업 클라우드백엔드트랙</p>
        <p>개발자: 원세빈, 조여진</p>
        <p>주소: 서울특별시 성북구 서경로 124, 서경대학교</p>
      </div>

      <div className="footer-contact">
        <p><strong>문의</strong></p>
        <p>이메일:pinduwls0826@gmail.com</p>
      </div>

      <div className="footer-disclaimer">
        <p>
          본 서비스는 서경대학교 SW인재양성사업의 일환으로 개발된 학습용 프로젝트입니다.
          실제 티켓 예매는 제공하지 않으며, 상업적 목적이 없습니다.
        </p>
        <p>&copy; 2025 TicketPlanet Project. All rights reserved.</p>
      </div>
    </footer>
  );
};

export default Footer;
