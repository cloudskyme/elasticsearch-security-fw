package org.elasticsearch.security.fw.api.action;

import java.io.IOException;
import java.util.Base64;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.security.fw.api.action.support.BaseAction;
import org.elasticsearch.security.fw.common.cfg.Configuration;
import org.elasticsearch.security.fw.common.util.MD5Util;
import org.elasticsearch.security.fw.dao.UserAclDao;
import org.elasticsearch.security.fw.dao.UserDao;
import org.elasticsearch.security.fw.domain.User;

public class GetAuthAclListRestAction extends BaseAction {

	Configuration config;
	
	@Inject
	public GetAuthAclListRestAction(Configuration config, Settings settings, RestController controller) {
		super(settings);
		this.config = config;
		controller.registerHandler(RestRequest.Method.GET, config.getApiContext() + "/acl/authlist", this);
	}

	@Override
	public BytesRestResponse processRequest(RestRequest request, NodeClient client) throws IOException {
		String fwIndicesname = config.getSecurityIndicename();
		try{
			  String authorization = ApiAcl.extractAuthFromHeader((String)request.header("Authorization"));
			  authorization = new String(Base64.getDecoder().decode(authorization.getBytes()));
			  int index = authorization.indexOf(":");
			  
			  String username = authorization.substring(0, index);
			  String secretkey = authorization.substring(index+1);
			  System.out.println("Authorization username:"+username);
			  logger.debug("Authorization username:"+username);
			  String authuser = username;
			  String authkey = secretkey;
			  if(!"api_user".equals(username)){
				  UserDao userDao = new UserDao(client, fwIndicesname);
				  User user = userDao.getUser(username);
				  secretkey = MD5Util.MD5(secretkey);
				  if(user==null || !user.getSecretkey().equals(secretkey)){
					  throw new Exception("The authorization code is not correct.");
				  }
				  authuser = username;
				  authkey = null;
			  } else {
				  authuser = null;
				  authkey = secretkey;
			  }
			  UserAclAction action = new UserAclAction();
			  UserAclDao userAclDao = new UserAclDao(client, fwIndicesname);
			  
			  return action.listAuthAcl(authuser, authkey, userAclDao);
		  }catch(Exception ex){
			  logger.error("get acl error;",ex);
			  return new BytesRestResponse(RestStatus.NOT_FOUND, "get acl info error");
		  }
	  }

}
