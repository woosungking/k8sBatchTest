package com.java.k8s.global;

import java.util.Objects;

import org.springframework.http.HttpStatus;

public class RestApiResponse {
	private final HttpStatus status;
	private String response;

	private RestApiResponse(HttpStatus status, String response) {
		this.status = status;
		this.response = response;
	}

	public String getResponse() {
		return response;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public static class Builder{
		private HttpStatus httpStatus;
		private String response;
		public Builder httpStatus(HttpStatus httpStatus){
			this.httpStatus = httpStatus;
			return this;
		}
		public Builder response(String response){
			this.response = response;
			return this;
		}
		public RestApiResponse build(){
			return new RestApiResponse(this.httpStatus, this.response);
		}

	}
}
