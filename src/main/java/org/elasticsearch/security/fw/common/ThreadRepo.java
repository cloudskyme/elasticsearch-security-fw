package org.elasticsearch.security.fw.common;

import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.security.fw.acl.AclSet;

public class ThreadRepo {
	public static ThreadLocal<RestRequest> request = new ThreadLocal<>();
	public static ThreadLocal<RestChannel> channel = new ThreadLocal<>();
	public static ThreadLocal<AclSet> aclSet = new ThreadLocal<>();
}
