import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '10s', target: 1000 }, // 동시 접속자 1000명으로 점프
    { duration: '1m', target: 1000 }, 
    { duration: '10s', target: 0 },
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
  check(res, {
    'is status 200': (r) => r.status === 200,
  });

  // 짤짤이 연타 속도 조절 (0.1초 대기, 너무 빠르면 로컬 배치가 못 버틸 수 있음)
  // 아예 극한의 성능을 보고 싶으면 아래 라인을 주석 처리하세요.

}