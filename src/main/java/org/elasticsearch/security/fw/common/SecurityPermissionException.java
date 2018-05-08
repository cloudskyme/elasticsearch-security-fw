package org.elasticsearch.security.fw.common;

public class SecurityPermissionException extends RuntimeException{
	
	private static final long serialVersionUID = 3599906611635315770L;

	public SecurityPermissionException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
