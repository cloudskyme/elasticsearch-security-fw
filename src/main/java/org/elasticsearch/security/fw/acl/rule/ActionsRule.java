package org.elasticsearch.security.fw.acl.rule;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.security.fw.acl.RequestContext;
import org.elasticsearch.security.fw.acl.rule.support.MatcherWithWildcards;
import org.elasticsearch.security.fw.acl.rule.support.Rule;
import org.elasticsearch.security.fw.acl.rule.support.RuleNotConfiguredException;

public class ActionsRule extends Rule {
	protected MatcherWithWildcards m;
	
	public ActionsRule(Settings sts) throws RuleNotConfiguredException {
		super(sts);
		m = new MatcherWithWildcards(sts, "actions");
	}

	@Override
	public boolean match(RequestContext t) {
		if(m.match(t.getAction())){
		  return true;
		}
		return false;
	}

}
