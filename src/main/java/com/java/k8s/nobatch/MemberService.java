package com.java.k8s.nobatch;

import com.java.k8s.nobatch.dto.LoginRequest;
import com.java.k8s.nobatch.dto.PointUpdateRequest;

public interface MemberService {
	void updateMemberPoint(PointUpdateRequest request);
	void login(LoginRequest request);
	void showPoint(Long memberId);
}
