package com.java.k8s.nobatch.update;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.java.k8s.nobatch.dto.PointUpdateRequest;

import jakarta.transaction.Transactional;

@ConditionalOnProperty(name = "point.update.strayegy", havingValue = "batch-jdbc")
@Component
public class PointUpdateServiceBat implements PointUpdateService{
	@Value("${point.update.batch.size}")
	private int batchSize;
	private final BlockingQueue<PointUpdateRequest> queue = new LinkedBlockingQueue<>(batchSize*10);
	private final JdbcTemplate jdbcTemplate;

	public PointUpdateServiceBat(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
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
		jdbcTemplate.batchUpdate("UPDATE member SET point = point + ? WHERE id = ?",
			new BatchPreparedStatementSetter(){

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					PointUpdateRequest request = batchList.get(i);
					long userId = request.getUserId();
					int point = request.getPoint();
					ps.setLong(1, point);
					ps.setLong(2, userId);
				}

				@Override
				public int getBatchSize() {
					return batchList.size();
				}
			});
		}
	}

