package com.java.k8s.nobatch.update;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.java.k8s.nobatch.Member;
import com.java.k8s.nobatch.MemberRepository;
import com.java.k8s.nobatch.dto.PointUpdateRequest;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
//메인 함수에 필히 적으시오 @EnableScheduling
@ConditionalOnProperty(name = "point.update.strategy", havingValue = "batch-jpa")
@Component
public class PointUpdateJpaBat implements PointUpdateService{
	private final BlockingQueue<PointUpdateRequest> queue = new LinkedBlockingQueue<>(10000);
	private final MemberRepository memberRepository;
	@PersistenceContext
	private EntityManager em;
	@Value("${point.update.batch.size}")
	private int batchSize;

	public PointUpdateJpaBat(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public void updateMemberPoint(PointUpdateRequest request) {
		System.out.println("씨ㅏㅏㅏㅏ발");
		queue.offer(request);
	}

	@Scheduled(fixedDelay = 1000) // 너무 빠르면 로그 보기 힘드니까 일단 1초로
	@Transactional
	public void processBatch(){
		if(queue.isEmpty()) return;

		System.out.println("로그: 배치 처리 시작! 현재 큐 사이즈: " + queue.size()); // 확인용

		List<PointUpdateRequest> batchList = new ArrayList<>();
		queue.drainTo(batchList, batchSize);

		for(PointUpdateRequest request : batchList){
			// 1. 여기서 데이터가 제대로 오는지 확인
			System.out.println("로그: 유저 " + request.getUserId() + " 포인트 " + request.getPoint() + " 업데이트 시도");

			Member member = memberRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("유저 없음: " + request.getUserId()));

			member.updatePoint(request.getPoint());
		}
		em.flush();
		em.clear();
		System.out.println("로그: DB에 쿼리 날림 완료!");
	}
}
