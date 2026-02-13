package com.java.k8s.nobatch;

import org.hibernate.QueryTimeoutException;
import org.hibernate.exception.JDBCConnectionException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Service;

import com.java.k8s.global.RestApiResponse;
import com.java.k8s.global.TechnicalException;
import com.java.k8s.nobatch.dto.LoginRequest;
import com.java.k8s.nobatch.dto.PointUpdateRequest;
import com.java.k8s.nobatch.entity.Member;
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
		try{
			pointUpdateService.updateMemberPoint(request);
		}catch (JDBCConnectionException | CannotGetJdbcConnectionException e){
			throw new TechnicalException(new RestApiResponse.Builder()
				.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
				.response("JDBC 커넥션 이슈")
				.build());
		}catch (QueryTimeoutException e){
			throw new TechnicalException(new RestApiResponse.Builder()
				.httpStatus(HttpStatus.TOO_MANY_REQUESTS)
				.response("쿼리 시간 초과")
				.build());

		}catch (Exception e){
			throw new TechnicalException(new RestApiResponse.Builder()
				.httpStatus(HttpStatus.GONE)
				.response("예상치 못한 이슈")
				.build());

		}

	}

	@Override
	public void login(LoginRequest request) {
		Member member = memberRepository.findByPasswordAndName(request.getPassword(),request.getUserName()).orElseThrow();
	}

	@Override
	public void showPoint(Long memberId) {

	}
}
