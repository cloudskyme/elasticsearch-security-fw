package org.elasticsearch.security.fw.acl.rule;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.security.fw.acl.RequestContext;
import org.elasticsearch.security.fw.acl.rule.support.IPMask;
import org.elasticsearch.security.fw.acl.rule.support.Rule;
import org.elasticsearch.security.fw.acl.rule.support.RuleNotConfiguredException;
import org.elasticsearch.security.fw.common.SecurityPermissionException;
import org.elasticsearch.security.fw.common.cfg.Configuration;
import org.elasticsearch.security.fw.common.util.CommUtil;

public class HostsRule extends Rule {
	private List<String> allowedAddresses;
	private Boolean acceptXForwardedForHeader;
	public HostsRule(Settings sts) throws RuleNotConfiguredException {
		super(sts);
		acceptXForwardedForHeader = sts.getAsBoolean("accept_x-forwarded-for_header", false);
		String[] a = sts.getAsArray("hosts");
	    if (a != null && a.length > 0) {
	    	
	      allowedAddresses = new Vector<String>();
	      for (int i = 0; i < a.length; i++) {
	        if (!Configuration.isNullOrEmpty(a[i])) {
	          try {
	            IPMask.getIPMask(a[i]);
	          } catch (Exception e) {
	            throw new RuleNotConfiguredException("invalid address", e);	            
	          }
	          allowedAddresses.add(a[i].trim());
	        }
	      }
	    } else {
	      throw new RuleNotConfiguredException();
	    }
	}
	
	private boolean matchesAddress(String address, String xForwardedForHeader) {
	    if (address == null) {
	    	throw new SecurityPermissionException("For some reason the origin address of this call could not be determined. Abort!", null);
	    }
	    if (allowedAddresses == null) {
	    	return true;
	    }

	    if (acceptXForwardedForHeader && xForwardedForHeader != null) {
	      // Give it a try with the header
	      boolean attemptXFwdFor = matchesAddress(xForwardedForHeader, null);
	      if (attemptXFwdFor) {
	        return true;
	      }
	    }
	    for (String allowedAddress : allowedAddresses) {
	      if (allowedAddress.indexOf("/") > 0) {
	        try {
	          IPMask ipmask = IPMask.getIPMask(allowedAddress);
	          if (ipmask.matches(address)) {
	            return true;
	          }
	        } catch (UnknownHostException e) {
	        }
	      }
	      if (allowedAddress.equals(address)) {
	        return true;
	      }
	    }
	    return false;
	}
	
	public boolean match(RequestContext requestContext){
		boolean res = matchesAddress(requestContext.getRemoteAddress(), CommUtil.getXForwardedForHeader(requestContext.getRequest()));
		return res;
	}
	
}
