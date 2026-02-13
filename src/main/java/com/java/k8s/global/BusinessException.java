package com.java.k8s.global;

import java.util.Objects;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException{
	private RestApiResponse restApiResponse;
	public BusinessException(RestApiResponse restApiResponse){
		this.restApiResponse = restApiResponse;
	}

	public RestApiResponse getRestApiResponse() {
		return restApiResponse;
	}
}
