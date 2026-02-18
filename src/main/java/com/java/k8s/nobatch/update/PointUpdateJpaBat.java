package com.java.k8s.nobatch.update;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

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
public class PointUpdateJpaBat implements PointUpdateService {

	private final MemberRepository memberRepository;
	@PersistenceContext
	private EntityManager em;

	@Value("${point.update.batch.size}")
	private int batchSize;

	@Value("${point.update.batch.delay}")
	private int batchDelay;
	private final BlockingQueue<PointUpdateRequest> queue = new LinkedBlockingQueue<>(100000);

	public PointUpdateJpaBat(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public void updateMemberPoint(PointUpdateRequest request) {
		boolean isAdded = queue.offer(request);
		if(!isAdded){
			throw new BusinessException(new RestApiResponse.Builder()
				.httpStatus(HttpStatus.IM_USED)
				.response("배치 큐 사이즈 초과!!")
				.build());
		}
	}
	@Transactional
	@Scheduled(fixedDelayString = "${point.update.batch.delay}")
	public void processBatch(){
		if(queue.isEmpty()) return;

		List<PointUpdateRequest> batchList = new ArrayList<>();
		queue.drainTo(batchList, batchSize);

		System.out.println("로그: 배치 처리 시작! 현재 큐 사이즈: " + queue.size() + ", 처리 대상: " + batchList.size());


		List<Long> userIds = batchList.stream()
			.map(PointUpdateRequest::getUserId)
			.distinct()
			.toList();


		Map<Long, Member> memberMap = memberRepository.findAllById(userIds).stream()
			.collect(Collectors.toMap(Member::getId, m -> m));


		for(PointUpdateRequest request : batchList){
			Member member = memberMap.get(request.getUserId());

			if (member != null) {
				member.updatePoint(request.getPoint());
			} else {
				System.out.println("로그: 존재하지 않는 유저 스킵 - ID: " + request.getUserId());
			}
		}

		em.flush();
		em.clear();
		System.out.println("로그: " + batchList.size() + "건 처리 및 DB 반영 완료!");
	}
}