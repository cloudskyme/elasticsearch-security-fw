package org.elasticsearch.security.fw.auth;

public class AuthException extends RuntimeException {
	private static final long serialVersionUID = -4054655229561402156L;

	public AuthException() {
	}

	public AuthException(String message) {
		super(message);
	}

}
