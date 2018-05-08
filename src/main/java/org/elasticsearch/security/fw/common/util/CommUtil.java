package org.elasticsearch.security.fw.common.util;

import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.security.fw.common.cfg.Configuration;

public class CommUtil {

	public static String getXForwardedForHeader(RestRequest request) {
	    if (!Configuration.isNullOrEmpty(request.header("X-Forwarded-For"))) {
	      String[] parts = request.header("X-Forwarded-For").split(",");
	      if (!Configuration.isNullOrEmpty(parts[0])) {
	        return parts[0];
	      }
	    }
	    return null;
	}
}
