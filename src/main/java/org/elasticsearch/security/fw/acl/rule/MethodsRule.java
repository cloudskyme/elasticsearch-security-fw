package org.elasticsearch.security.fw.acl.rule;

import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.security.fw.acl.RequestContext;
import org.elasticsearch.security.fw.acl.rule.support.Rule;
import org.elasticsearch.security.fw.acl.rule.support.RuleNotConfiguredException;

public class MethodsRule extends Rule {
	private List<RestRequest.Method> allowedMethods;
	
	public MethodsRule(Settings sts) throws RuleNotConfiguredException{
		super(sts);
		String[] a = sts.getAsArray("methods");
	    if (a != null && a.length > 0) {
	      try {
	        for (String string : a) {
	          RestRequest.Method m = RestRequest.Method.valueOf(string.trim().toUpperCase());
	          if (allowedMethods == null) {
	            allowedMethods = new ArrayList<RestRequest.Method>();
	          }
	          allowedMethods.add(m);
	        }
	      } catch (Throwable t) {
	        throw new RuleNotConfiguredException("Invalid HTTP method found in configuration " + a, t);
	      }
	    } else {
	      throw new RuleNotConfiguredException();
	    }
	}

	@Override
	public boolean match(RequestContext t) {
		if(allowedMethods.contains(t.getRequest().method())) {
			return true;
		}
		return false;
	}

}
