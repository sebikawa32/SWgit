import { useEffect } from "react";
import { useLocation } from "react-router-dom";

function ScrollToTop() {
  const { pathname } = useLocation();

  useEffect(() => {
    window.scrollTo(0, 0); // 페이지 최상단으로 스크롤 이동
  }, [pathname]); // URL 경로 바뀔 때마다 실행됨

  return null; // UI 요소 없음
}

export default ScrollToTop;
