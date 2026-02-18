import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
  stages: [
    { duration: '10s', target: 2000 }, // 600명까지 램프업 (충분히 무거운 부하)
    { duration: '20s', target: 2000 }, // 유지
    { duration: '10s', target: 0 },
  ],
};

export default function () {
  // 핵심: ID 범위를 1~100으로 좁혀서 Row Lock 경합 유도!
  const userId = Math.floor(Math.random() * 10000) + 1;
  
  const url = 'http://172.16.0.202:30080/api/v1/member/point';
  const payload = JSON.stringify({ userId: userId, point: 100 });
  const params = { headers: { 'Content-Type': 'application/json' } };

  const res = http.put(url, payload, params);

  const isOk = check(res, { 'is status 200': (r) => r.status === 200 });

  if (!isOk) {
    // 여기서 어떤 에러가 나는지 관찰 (Connection Timeout vs Deadlock)
    console.error(`[${res.status}] ${res.body ? res.body : 'Timeout'}`);
  }

  sleep(0.01); // 처리 속도를 극대화하기 위해 sleep을 최소화
}


