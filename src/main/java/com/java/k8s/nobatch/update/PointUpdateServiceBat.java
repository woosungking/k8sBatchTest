package com.java.k8s.nobatch;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import com.java.k8s.nobatch.dto.PointUpdateRequest;

@ConditionalOnProperty(name = "point.update.strayegy", havingValue = "batch-jdbc")
@Component
public class PointUpdateServiceBat implements PointUpdateService{
	@Override
	public void updateMemberPoint(PointUpdateRequest request) {

	}
}
