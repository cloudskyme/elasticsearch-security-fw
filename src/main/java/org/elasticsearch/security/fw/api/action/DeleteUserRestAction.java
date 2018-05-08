package org.elasticsearch.security.fw.api.action;

import java.io.IOException;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.security.fw.api.action.support.BaseAction;
import org.elasticsearch.security.fw.common.cfg.Configuration;
import org.elasticsearch.security.fw.dao.UserDao;

public class DeleteUserRestAction extends BaseAction {
	Configuration config;
	
	@Inject
	public DeleteUserRestAction(Configuration config, Settings settings, RestController controller){
		super(settings);
		this.config = config;
		controller.registerHandler(RestRequest.Method.DELETE, config.getApiContext()+"/user", this);
	}

	@Override
	protected BytesRestResponse processRequest(RestRequest request, NodeClient client) throws IOException {
		String indicesname = config.getSecurityIndicename();
		UserDao userDao = new UserDao(client, indicesname);
		
		UserAction action = new UserAction();
		
		return action.removeUser(userDao, request);
	}
	
}
