package com.java.k8s.global;

public class TechnicalException extends RuntimeException {
	private RestApiResponse restApiResponse;
	public TechnicalException(RestApiResponse restApiResponse){
		this.restApiResponse = restApiResponse;
	}

	public RestApiResponse getRestApiResponse() {
		return restApiResponse;
	}
}
