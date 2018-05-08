package org.elasticsearch.security.fw.acl.rule;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.security.fw.acl.RequestContext;
import org.elasticsearch.security.fw.acl.rule.support.MatcherWithWildcards;
import org.elasticsearch.security.fw.acl.rule.support.Rule;
import org.elasticsearch.security.fw.acl.rule.support.RuleNotConfiguredException;

public class IndicesRule extends Rule {
	protected MatcherWithWildcards m;
	public IndicesRule(Settings sts) throws RuleNotConfiguredException{
		super(sts);
		m = new MatcherWithWildcards(sts, "indices");
	}

	@Override
	public boolean match(RequestContext t) {
		String[] indices = t.getIndices();
		
		//System.out.println("match:"+indices.length+","+m.getMatchers().contains("<no-index>"));
	    if(m.getMatchers().contains("<no-index>")){
	      return true;
	    }
	    if(indices.length == 0 && m.getMatchers().size()>0){
	        return false;
	    }
	    for (String in:t.getIndices()) {
	      if(!m.match(in)){
	        return false;
	      }
	    }
	    
		return true;
	}

}
