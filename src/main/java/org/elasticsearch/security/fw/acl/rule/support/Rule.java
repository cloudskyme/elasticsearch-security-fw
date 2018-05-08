package org.elasticsearch.security.fw.acl.rule.support;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.security.fw.acl.RequestContext;

public abstract class Rule {
	Settings sts;
	public Rule(Settings sts) {
		this.sts = sts;
	}
	
	public abstract boolean match(RequestContext t);

	
}
