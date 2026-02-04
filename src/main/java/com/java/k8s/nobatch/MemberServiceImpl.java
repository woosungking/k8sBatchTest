package com.java.k8s.nobatch;

import org.springframework.stereotype.Service;

import com.java.k8s.nobatch.dto.LoginRequest;
import com.java.k8s.nobatch.dto.PointUpdateRequest;
import com.java.k8s.nobatch.update.PointUpdateService;

@Service
public class MemberServiceImpl implements MemberService{
	private final PointUpdateService pointUpdateService;
	private final MemberRepository memberRepository;
	public MemberServiceImpl(PointUpdateService pointUpdateService, MemberRepository memberRepository) {
		this.pointUpdateService = pointUpdateService;
		this.memberRepository = memberRepository;
	}

	@Override
	public void updateMemberPoint(PointUpdateRequest request) {
		pointUpdateService.updateMemberPoint(request);
	}

	@Override
	public void login(LoginRequest request) {
		Member member = memberRepository.findByPasswordAndName(request.getPassword(),request.getUserName()).orElseThrow();
	}

	@Override
	public void showPoint(Long memberId) {

	}
}
