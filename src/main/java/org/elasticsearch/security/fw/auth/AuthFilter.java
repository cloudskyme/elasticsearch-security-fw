package org.elasticsearch.security.fw.auth;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.support.ActionFilter.Simple;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.indices.IndicesService;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.security.fw.acl.AclSet;
import org.elasticsearch.security.fw.acl.RequestContext;
import org.elasticsearch.security.fw.common.ThreadRepo;
import org.elasticsearch.threadpool.ThreadPool;

public class AuthFilter extends Simple {
	private final ThreadContext context;
	IndicesService indicesService;
	
	@Inject
	public AuthFilter(ThreadPool threadPool, Settings settings, IndicesService indicesService) {
		super(settings);
		this.indicesService = indicesService;
		context = threadPool.getThreadContext();		
	}

	@Override
	public int order() {
		return 0;
	}

	@Override
	protected boolean apply(String action, ActionRequest<?> request, ActionListener<?> listener) {
	    RestChannel channel = ThreadRepo.channel.get();
		RestRequest resetRequest = ThreadRepo.request.get();
		AclSet aclset = ThreadRepo.aclSet.get();
		
	    boolean reqNull = resetRequest == null;
	    boolean chanNull = channel == null;

	    // This was not a REST message
	    if (reqNull && chanNull) {
	      return true;
	    }
	    RequestContext rc = new RequestContext(channel, resetRequest, action, request);
	    
	    /////////////////////////////////
//	    String[] indices = rc.getIndices();
//	    String tmp = "";
//	    rc.getIndices();	    
//	    
//		for(String ids:indices){
//			tmp += ","+ids;
//		}
		//System.out.println("path:"+resetRequest.path()+",method:"+resetRequest.method().toString()+",action:"+action+", indices:"+tmp);
		//////////////////////////
	    if(aclset.check(rc)){
	    	return true;
	    }
	    if(resetRequest.path().equals("/")){
	    	return true;
	    }
	    String[] indices = rc.getIndices();
	    String tmp = "[";
	    rc.getIndices();	    
	    
		for(String ids:indices){
			tmp += ids + ",";
		}
		tmp +="]";
	    logger.warn("acl rule check false, path:"+resetRequest.path()+",method:"+resetRequest.method().toString()+",action:"+action+", indices:"+tmp);
	    //System.out.println(resetRequest.path()+","+resetRequest.method().toString());
    	if(!reqNull){
    		throw new AuthException("Access denied, no rights. indicename、http method、action、body length or host is not allowed。");
    	}
		return true;
	}

	@Override
	protected boolean apply(String action, ActionResponse response, ActionListener<?> listener) {
		return true;
	}

}
