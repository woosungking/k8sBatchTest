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
@ComponentScan("")
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
		queue.offer(request);
	}

	@Scheduled(fixedDelay = 100)
	@Transactional
	public void processBatch(){
		if(queue.isEmpty()) return;
		List<PointUpdateRequest> batchList = new ArrayList<>();
		queue.drainTo(batchList,batchSize);
		int count = 0;
		for(PointUpdateRequest request : batchList){
			Member member = memberRepository.findById(request.getUserId()).orElseThrow();
			member.updatePoint(request.getPoint());
			if(count > 0 && count % batchSize ==0){
				em.flush();
				em.clear();
			}
			count++;
		}
		em.flush();
		em.clear();
	}
}
