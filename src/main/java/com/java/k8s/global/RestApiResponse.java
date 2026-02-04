package com.java.k8s.global;

import java.util.Objects;

import org.springframework.http.HttpStatus;

public class RestApiResponse {
	private final HttpStatus status;
	private Objects response;

	private RestApiResponse(HttpStatus status, Objects response) {
		this.status = status;
		this.response = response;
	}

	public Objects getResponse() {
		return response;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public static class Builder{
		private HttpStatus httpStatus;
		private Objects response;
		public Builder httpStatus(HttpStatus httpStatus){
			this.httpStatus = httpStatus;
			return this;
		}
		public Builder response(Objects response){
			this.response = response;
			return this;
		}
		public RestApiResponse build(){
			return new RestApiResponse(this.httpStatus, this.response);
		}

	}
}
