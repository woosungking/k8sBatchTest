package com.java.k8s.nobatch.update;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.java.k8s.global.BusinessException;
import com.java.k8s.global.RestApiResponse;
import com.java.k8s.nobatch.entity.Member;
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
	@Value("${point.update.batch.delay}")
	private int batchDelay;

	public PointUpdateJpaBat(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public void updateMemberPoint(PointUpdateRequest request) {
		System.out.println("씨ㅏㅏㅏㅏ발");
		boolean isAdded = queue.offer(request);
		if(!isAdded){
			throw new BusinessException(new RestApiResponse.Builder()
				.httpStatus(HttpStatus.IM_USED)
				.response("배치 큐 사이즈 초과!! 큐 사이즈 ==>"+ batchSize + "  배치 시간 ==>"  + batchDelay)
				.build());
		}
	}

	@Scheduled(fixedDelayString = "${point.update.batch.delay}")
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
