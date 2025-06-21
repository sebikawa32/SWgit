import { useEffect, useRef } from "react";

// userId: 로그인된 유저 ID
// onNotification: 알림 받았을 때 실행할 함수 (콜백)
function useSseNotification(userId, onNotification) {
  const eventSourceRef = useRef(null);
  const apiUrl = process.env.REACT_APP_API_URL;
  

  useEffect(() => {
    if (!userId) {
      console.warn("🔕 userId 없음 → SSE 연결 안함");
      return;
    }

    // 이미 연결된 경우 중복 방지
    if (eventSourceRef.current) {
      console.log("🔁 이미 SSE 연결되어 있음. 중복 연결 방지");
      return;
    }

    const url = `${apiUrl}/notifications/subscribe?userId=${userId}`;
    console.log(`🔌 SSE 연결 시작 → ${url}`);

    const eventSource = new EventSource(url);
    eventSourceRef.current = eventSource;

    // 알림 이벤트 수신
    eventSource.addEventListener("notification", (event) => {
      try {
        const data = JSON.parse(event.data);
        console.log("📨 실시간 알림 도착!", data);
        onNotification && onNotification(data); // 콜백 실행
      } catch (err) {
        console.error("❌ 알림 데이터 파싱 오류", err);
      }
    });

    // 연결 에러 핸들링
    eventSource.onerror = (err) => {
      console.warn("⚠️ SSE 연결 오류", err);
      eventSource.close();
      eventSourceRef.current = null;
    };

    // 언마운트 시 연결 종료
    return () => {
      console.log("🔌 SSE 연결 종료");
      eventSource.close();
      eventSourceRef.current = null;
    };
  }, [userId, onNotification]);
}

export default useSseNotification;
