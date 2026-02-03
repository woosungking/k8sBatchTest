package com.java.k8s.nobatch;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.java.k8s.nobatch.dto.PointUpdateRequest;

@ConditionalOnProperty(name="point.update.strategy", havingValue = "no-batch")
@Component
public class PointUpdateServiceNoBat implements PointUpdateService{
	private final MemberRepository memberRepository;

	public PointUpdateServiceNoBat(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Override
	public void updateMemberPoint(PointUpdateRequest request) {
		memberRepository.findById(request.getUserId()).ifPresentOrElse(
			member -> {
				member.updatePoint(request.getPoint());
			},
			()->{
				throw new RuntimeException("해당 유저는 존재하지 않습니다");
			}
		);
	}
}
