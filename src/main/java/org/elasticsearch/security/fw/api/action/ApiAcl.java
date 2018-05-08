package org.elasticsearch.security.fw.api.action;

public class ApiAcl {

	public static String extractAuthFromHeader(String authorizationHeader) {
	    if (authorizationHeader == null || authorizationHeader.trim().length() == 0 || !authorizationHeader.contains("Basic "))
	      return null;
	    String interestingPart = authorizationHeader.split("Basic")[1].trim();
	    if (interestingPart.length() == 0) {
	        return null;
	    }
	    return interestingPart;
	}
}
