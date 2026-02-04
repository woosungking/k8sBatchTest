package com.java.k8s.nobatch;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.k8s.global.RestApiResponse;
import com.java.k8s.nobatch.dto.LoginRequest;
import com.java.k8s.nobatch.dto.PointUpdateRequest;

import jakarta.websocket.server.PathParam;

@RestController
@RequestMapping("api/v1/member")
public class TestController {
	private final MemberService memberService;

	public TestController(MemberService memberService) {
		this.memberService = memberService;
	}

	@PostMapping("/login")
	public ResponseEntity<RestApiResponse> login(@RequestBody LoginRequest request){
		memberService.login(request);
		return ResponseEntity.ok(new RestApiResponse.Builder().httpStatus(HttpStatus.OK).build());
	}

	@PutMapping("/point")
	public ResponseEntity<RestApiResponse> updatePoint(@RequestBody PointUpdateRequest request){
		memberService.updateMemberPoint(request);
		return ResponseEntity.ok(new RestApiResponse.Builder().httpStatus(HttpStatus.OK).build());
	}

	@GetMapping("/point/{memberId}")
	public ResponseEntity<RestApiResponse> viewMemberPoint(@PathParam("memberId") long memberId){
		memberService.showPoint(memberId);
		return ResponseEntity.ok(new RestApiResponse.Builder().httpStatus(HttpStatus.OK).build());
	}
}
