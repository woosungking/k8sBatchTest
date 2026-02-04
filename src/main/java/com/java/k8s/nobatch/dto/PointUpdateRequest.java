package com.java.k8s.nobatch.dto;

public class PointUpdateRequest {
	private final Long userId;
	private final int point;

	public int getPoint() {
		return point;
	}

	public Long getUserId() {
		return userId;
	}

	public PointUpdateRequest(Long userId, int point) {
		this.userId = userId;
		this.point = point;
	}
}
