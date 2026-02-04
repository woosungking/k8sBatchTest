package com.java.k8s.nobatch.update;

import com.java.k8s.nobatch.dto.PointUpdateRequest;

public interface PointUpdateService {
	public void updateMemberPoint(PointUpdateRequest request);
}
