package org.elasticsearch.security.fw.acl;

import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.settings.Settings;

public class AclSet {
	Settings sts;
	Logger logger;
	ArrayList<Acl> aclList;
	public AclSet(Settings sts, Logger logger) {
		//Settings g = Settings.builder().loadFromSource(ruleStr).build();
		this.sts = sts;
		this.logger = logger;
		aclList = new ArrayList<Acl>();
		Map<String, Settings> s = sts.getGroups("access_control_rules");
		for (String k : s.keySet()){
			Acl acl = new Acl(s.get(k), logger);
			aclList.add(acl);
		}
	}
	
	public boolean check(RequestContext t){
		for(Acl acl:this.aclList){
			if(acl.check(t) && acl.getPolicy().equals(Acl.Policy.ALLOW)){
				return true;
			}
		}
		return false;
	}
}
