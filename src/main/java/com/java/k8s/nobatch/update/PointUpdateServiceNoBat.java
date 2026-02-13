package com.java.k8s.nobatch.update;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.java.k8s.global.BusinessException;
import com.java.k8s.nobatch.MemberRepository;
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
			}
		);
	}
}
