package org.elasticsearch.security.fw.plugin;

import static java.util.Collections.singletonList;

import java.util.Arrays;
import java.util.List;

import org.elasticsearch.action.support.ActionFilter;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestHandler;
import org.elasticsearch.security.fw.api.action.DeleteAclRestAction;
import org.elasticsearch.security.fw.api.action.DeleteIndiceRestAction;
import org.elasticsearch.security.fw.api.action.DeleteUserRestAction;
import org.elasticsearch.security.fw.api.action.GetAclListRestAction;
import org.elasticsearch.security.fw.api.action.GetAclRestAction;
import org.elasticsearch.security.fw.api.action.GetAuthAclListRestAction;
import org.elasticsearch.security.fw.api.action.GetIndiceHealthRestAction;
import org.elasticsearch.security.fw.api.action.GetIndiceListRestAction;
import org.elasticsearch.security.fw.api.action.GetIndiceRestAction;
import org.elasticsearch.security.fw.api.action.GetIndiceSettingsRestAction;
import org.elasticsearch.security.fw.api.action.GetIndiceStatsRestAction;
import org.elasticsearch.security.fw.api.action.GetUserListRestAction;
import org.elasticsearch.security.fw.api.action.GetUserRestAction;
import org.elasticsearch.security.fw.api.action.PostAclRestAction;
import org.elasticsearch.security.fw.api.action.PostIndiceRestAction;
import org.elasticsearch.security.fw.api.action.PostIndiceTransferRestAction;
import org.elasticsearch.security.fw.api.action.PostUserRestAction;
import org.elasticsearch.security.fw.api.action.PutAclRestAction;
import org.elasticsearch.security.fw.api.action.PutIndiceRestAction;
import org.elasticsearch.security.fw.api.action.PutUserRestAction;
import org.elasticsearch.security.fw.auth.AuthAction;
//import org.elasticsearch.security.fw.auth.AuthAction;
import org.elasticsearch.security.fw.auth.AuthFilter;

public class SecurityFwPlugin extends Plugin implements ActionPlugin{
	
	@Override
    public List<Class<? extends RestHandler>> getRestHandlers() {
		
		return Arrays.asList(
				GetUserRestAction.class, 
				PostUserRestAction.class,
				PutUserRestAction.class,
				DeleteUserRestAction.class,
				GetUserListRestAction.class,
				
				GetIndiceRestAction.class, 
				PostIndiceRestAction.class,
				PutIndiceRestAction.class,
				DeleteIndiceRestAction.class,
				
				GetIndiceListRestAction.class,
				PostIndiceTransferRestAction.class,
				GetIndiceHealthRestAction.class,
				GetIndiceStatsRestAction.class,
				GetIndiceSettingsRestAction.class,
				
				GetAclRestAction.class, 
				PostAclRestAction.class,
				PutAclRestAction.class,
				DeleteAclRestAction.class,
				GetAuthAclListRestAction.class,
				GetAclListRestAction.class,
				
				AuthAction.class);
    }

	@Override
    public List<Class<? extends ActionFilter>> getActionFilters() {
        return singletonList(AuthFilter.class);
    }
	
}
