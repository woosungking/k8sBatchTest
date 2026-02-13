import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '10s', target: 500 }, // 10초 동안 1000명까지 램프업
    { duration: '1m', target: 500 },  // 1분 동안 1000명 유지
    { duration: '10s', target: 0 },    // 10초 동안 0명으로 종료
  ],
};

export default function () {
  // 1. 유저 ID 랜덤 생성 (1 ~ 10000)
  const userId = Math.floor(Math.random() * 10000) + 1;
  
  const url = 'http://172.16.0.202:30080/api/v1/member/point';
  const payload = JSON.stringify({
    userId: userId,
    point: 100
  });

  const params = {
    headers: {
      'Content-Type': 'application/json',
    },
  };

  // 2. PUT 요청 전송
  const res = http.put(url, payload, params);

  // 3. 성공 여부 체크 (200 OK)
  const isOk = check(res, {
    'is status 200': (r) => r.status === 200,
  });

  // 4. 실패 시 상세 사유 로컬 로깅 (파일 저장용)
  if (!isOk) {
    let errorMsg = '서버 응답 없음 (Network Error/Timeout)';
    
    try {
      // 서버가 던진 RestApiResponse를 파싱하여 response 필드 추출
      if (res.body) {
        const body = JSON.parse(res.body);
        errorMsg = body.response || '응답 메시지 누락';
      }
    } catch (e) {
      // JSON 파싱이 안 되는 경우 (예: 깡통 502 Bad Gateway 등)
      errorMsg = `Raw Error: ${res.status} - ${res.error || 'Unknown'}`;
    }

    // [시간] 상태코드 | 상세메시지 형태로 표준 에러(stderr)에 기록
    // 실행 시 '2> error.log'를 붙여야 파일로 들어갑니다.
    console.error(`[${new Date().toISOString()}] ${res.status} | ${errorMsg}`);
  }

  // 부하 조절을 위해 필요시 sleep(0.1); 주석 해제하여 사용
  // sleep(0.1);
}