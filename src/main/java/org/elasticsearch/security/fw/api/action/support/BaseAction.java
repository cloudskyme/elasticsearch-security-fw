package org.elasticsearch.security.fw.api.action.support;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestRequest;

public abstract class  BaseAction extends SupperActionHandler{

	public BaseAction(Settings settings) {
		super(settings);
	}
	
	@Override
	protected final RestChannelConsumer prepareRequest(RestRequest request, NodeClient client) throws IOException {
		return channel -> channel.sendResponse(processRequest(request, client));
	}
	
	protected abstract BytesRestResponse processRequest(RestRequest request, NodeClient client) throws IOException;
	
	protected Set<String> responseParams() {
		return new HashSet<String>(){
			private static final long serialVersionUID = 1L;
			public boolean contains(Object obj){
				return true;
			}
		};
    }
}
