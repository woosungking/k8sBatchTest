package com.java.k8s.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<RestApiResponse> handleBusinessException(BusinessException e){
		return ResponseEntity.status(e.getRestApiResponse().getStatus()).body(e.getRestApiResponse());
	}

	@ExceptionHandler(TechnicalException.class)
	public ResponseEntity<RestApiResponse> handleTechException(TechnicalException e){
		return ResponseEntity.status(e.getRestApiResponse().getStatus()).body(e.getRestApiResponse());
	}
}
