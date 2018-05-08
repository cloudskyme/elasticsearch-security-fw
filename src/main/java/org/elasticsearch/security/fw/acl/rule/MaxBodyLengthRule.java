package org.elasticsearch.security.fw.acl.rule;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.security.fw.acl.RequestContext;
import org.elasticsearch.security.fw.acl.rule.support.Rule;
import org.elasticsearch.security.fw.acl.rule.support.RuleNotConfiguredException;

public class MaxBodyLengthRule extends Rule {
	private Integer maxBodyLength;
	
	public MaxBodyLengthRule(Settings sts) throws RuleNotConfiguredException{
		super(sts);
		maxBodyLength = sts.getAsInt("maxBodyLength", null);
	    if (maxBodyLength == null) {
	      throw new RuleNotConfiguredException();
	    }
	}

	@Override
	public boolean match(RequestContext t) {
		return (t.getRequest().content().length() > maxBodyLength) ? false : true;
	}

}
