package org.elasticsearch.security.fw.auth;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Base64;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestChannel;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestFilter;
import org.elasticsearch.rest.RestFilterChain;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.security.fw.acl.AclSet;
import org.elasticsearch.security.fw.api.action.GetAuthAclListRestAction;
import org.elasticsearch.security.fw.cache.UserRuleCache;
import org.elasticsearch.security.fw.common.ThreadRepo;
import org.elasticsearch.security.fw.common.cfg.Configuration;
import org.elasticsearch.security.fw.system.InitSystem;

public class AuthAction extends BaseRestHandler {
	private final Configuration config;
	
	@Inject
    public AuthAction(Configuration config, Settings settings, RestController controller) {
		super(settings);
		this.config = config;
		
		controller.registerFilter(new RestFilter() {
            @Override
            public void process(RestRequest request, RestChannel channel, NodeClient client, RestFilterChain filterChain) throws Exception {
            	InitSystem init= new InitSystem();
            	init.initialization(logger, client, config);
              //ThreadRepo.
              //验证用户账号
              //request.getRemoteAddress().toString();
              logger.debug("request path:"+request.path()+" "+request.method().toString());
              
              ThreadRepo.request.set(null);
              ThreadRepo.channel.set(null);
              ThreadRepo.aclSet.set(null);
              
              if(!request.path().equals(config.getApiContext()+"/acl/authlist")){
	              String authHeader = request.header("Authorization");

	              Settings usersettings = null;
	              String authUrl = config.getUserFwApiUrl();
	              if(authUrl == null){
//	            	  InetSocketAddress servierAddress = ((InetSocketAddress) request.getLocalAddress());
//	            	  authUrl = "http://"+ servierAddress.getAddress().getHostAddress() + ":"+servierAddress.getPort() + config.getApiContext() + "/acl/authlist";
	            	  usersettings = UserRuleCache.getUserSettingsFromElas(logger, config, settings, controller, request, client, config.getUserFwCacheTime());
	              }else{
	            	  usersettings = UserRuleCache.getUserSettingsFromURL(logger, authUrl, authHeader, config.getUserFwCacheTime());
	              }
	              logger.info("authHeader:"+authHeader+",request:"+request.uri()+",authUrl:"+authUrl);
	              
	              
	              if(usersettings == null){
	            	  //认证失败
	            	  BytesRestResponse resp = new BytesRestResponse(RestStatus.UNAUTHORIZED, "认证失败");
	                  logger.debug("Sending login prompt header...");
	                  resp.addHeader("WWW-Authenticate", "Basic");
	            	  channel.sendResponse(resp);
	            	  //记录log
	            	  String username="";
	            	  try{
		            	  String authHeader_user = new String(Base64.getDecoder().decode(authHeader.getBytes()));
		    			  int index = authHeader_user.indexOf(":");		    			  
		    			  username = authHeader_user.substring(0, index);
	            	  }catch(Exception ex){	            		  
	            	  }
	            	  logger.warn("Authentication failure, request path:"+request.path()+",method:"+request.method().toString() + ",username:" + username);
	            	  return;
	              }
	              
	              AclSet aclset = new AclSet(usersettings, logger);
	              
	              ThreadRepo.request.set(request);
	              ThreadRepo.channel.set(channel);
	              ThreadRepo.aclSet.set(aclset);
              }else{
            	  ThreadRepo.request.set(null);
	              ThreadRepo.channel.set(null);
	              ThreadRepo.aclSet.set(null);
              }
              
              filterChain.continueProcessing(request, channel, client);
            }
          });
	}

	@Override
	protected RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
		return null;
	}

}
