import { useEffect } from "react";
//sse연결 커스텀 hook

// userId: 로그인된 유저 ID
// onNotification: 알림 받았을 때 실행할 함수 (콜백)
function useSseNotification(userId, onNotification) {
  useEffect(() => {
    if (!userId) return;

    
    // 서버와 SSE 연결
    const eventSource = new EventSource('http://localhost:8080/api/notifications/subscribe?userId=' + userId);


    // 알림 이벤트 수신
    eventSource.addEventListener("notification", (event) => {
      try {
        const data = JSON.parse(event.data);
        console.log("실시간 알림 도착!", data); 
        onNotification && onNotification(data); // 알림 콜백 실행
      } catch (err) {
        console.error("알림 데이터 파싱 오류", err);
      }
    });

    // 연결 에러 핸들링
    eventSource.onerror = (err) => {
      console.warn("SSE 연결 오류", err);
      eventSource.close();
    };

    // 언마운트시 연결 종료
    return () => {
      eventSource.close();
    };
  }, [userId, onNotification]);
}

export default useSseNotification;
