package org.elasticsearch.security.fw.acl.rule.support;

public class RuleNotConfiguredException extends Exception {
	private static final long serialVersionUID = 2696830208101519573L;

	public RuleNotConfiguredException() {
	}

	public RuleNotConfiguredException(String message) {
		super(message);
	}

	public RuleNotConfiguredException(Throwable cause) {
		super(cause);
	}

	public RuleNotConfiguredException(String message, Throwable cause) {
		super(message, cause);
	}

	public RuleNotConfiguredException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
