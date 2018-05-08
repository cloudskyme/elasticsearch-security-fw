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
import org.elasticsearch.security.fw.dao.UserAclDao;
import org.elasticsearch.security.fw.dao.UserDao;
import org.elasticsearch.security.fw.dao.UserIndicesDao;

public class PostIndiceTransferRestAction extends BaseAction {
	Configuration config;
	
	@Inject
	public PostIndiceTransferRestAction(Configuration config, Settings settings, RestController controller) {
		super(settings);
		this.config = config;
		controller.registerHandler(RestRequest.Method.GET, config.getApiContext() + "/indices/transfer", this);
	}

	@Override
	protected BytesRestResponse processRequest(RestRequest request, NodeClient client) throws IOException {
		String fwIndicesname = config.getSecurityIndicename();
		UserIndicesDao userIndicesDao = new UserIndicesDao(client, fwIndicesname);
		
		UserDao userDao = new UserDao(client, fwIndicesname);
		UserAclDao userAclDao = new UserAclDao(client, fwIndicesname);
		  
		UserIndicesAction action = new UserIndicesAction();
		
		return action.transferUserIndices(userDao, userAclDao, userIndicesDao, request);
	}

}
