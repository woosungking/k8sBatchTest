package com.java.k8s.nobatch.update;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.java.k8s.global.BusinessException;
import com.java.k8s.global.RestApiResponse;
import com.java.k8s.nobatch.dto.PointUpdateRequest;

import jakarta.transaction.Transactional;

// 1. strategy 오타 수정 (strayegy -> strategy)
@ConditionalOnProperty(name = "point.update.strategy", havingValue = "batch-jdbc")
@Component
public class PointUpdateServiceBat implements PointUpdateService {

	private final BlockingQueue<PointUpdateRequest> queue;
	private final JdbcTemplate jdbcTemplate;

	@Value("${point.update.batch.size}")
	private int batchSize;
	@Value("${point.update.batch.delay}")
	private int batchDelay;

	// 2. 생성자에서 큐 사이즈 안전하게 초기화
	public PointUpdateServiceBat(JdbcTemplate jdbcTemplate, @Value("${point.update.batch.size}") int batchSize) {
		this.jdbcTemplate = jdbcTemplate;
		this.batchSize = batchSize;
		this.queue = new LinkedBlockingQueue<>(batchSize * 10000);
	}

	@Override
	public void updateMemberPoint(PointUpdateRequest request) {
		// 큐에 넣을 때 로그 확인 (필요시 주석 해제)
		// System.out.println("JDBC 큐 인입: " + request.getUserId());
		boolean isAdded = queue.offer(request);

		if(!isAdded){
			throw new BusinessException(new RestApiResponse.Builder()
				.httpStatus(HttpStatus.IM_USED)
				.response("배치 큐 사이즈 초과!! 큐 사이즈 ==>"+ batchSize + "  배치 시간 ==>"  + batchDelay)
				.build());
		}
	}
	@Transactional
	@Scheduled(fixedDelay = 100)
	public void processBatch() {
		if (queue.isEmpty()) return;

		List<PointUpdateRequest> batchList = new ArrayList<>();
		queue.drainTo(batchList, batchSize);
		batchList.sort(Comparator.comparingLong(PointUpdateRequest::getUserId));
		System.out.println("로그: JDBC 배치 처리 시작! 데이터 개수: " + batchList.size());

		jdbcTemplate.batchUpdate("UPDATE members SET point = point + ? WHERE id = ?",
			new BatchPreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					PointUpdateRequest request = batchList.get(i);
					ps.setInt(1, request.getPoint()); // 포인트(int)
					ps.setLong(2, request.getUserId()); // 유저ID(long)
				}

				@Override
				public int getBatchSize() {
					return batchList.size();
				}
			});

		System.out.println("로그: JDBC 배치 쿼리 실행 완료!");
	}
}