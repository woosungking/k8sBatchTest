package com.java.k8s.nobatch.dto;

public class LoginRequest {
	private final String userName;
	private final String password;

	public String getUserName() {
		return userName;
	}

	public String getPassword() {
		return password;
	}

	public LoginRequest(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}
}
