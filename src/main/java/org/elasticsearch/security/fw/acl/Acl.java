package org.elasticsearch.security.fw.acl;

import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.set.Sets;
import org.elasticsearch.security.fw.acl.rule.ActionsRule;
import org.elasticsearch.security.fw.acl.rule.HostsRule;
import org.elasticsearch.security.fw.acl.rule.IndicesRule;
import org.elasticsearch.security.fw.acl.rule.MaxBodyLengthRule;
import org.elasticsearch.security.fw.acl.rule.MethodsRule;
import org.elasticsearch.security.fw.acl.rule.support.Rule;
import org.elasticsearch.security.fw.acl.rule.support.RuleNotConfiguredException;
import org.elasticsearch.security.fw.common.cfg.Configuration;

public class Acl {
	private final String name;
	private final Policy policy;
	private Set<Rule> conditionsToCheck = Sets.newHashSet();

	public Acl(Settings sts, Logger logger) {
		name = sts.get("name");
		String sPolicy = sts.get("type");
		if (Configuration.isNullOrEmpty(sPolicy)) {
			policy = Acl.Policy.ALLOW;
		} else {
			policy = Acl.Policy.valueOf(sPolicy.toUpperCase());
		}

		try {
			conditionsToCheck.add(new HostsRule(sts));
		} catch (RuleNotConfiguredException e) {
		}

		try {
			conditionsToCheck.add(new ActionsRule(sts));
		} catch (RuleNotConfiguredException e) {
		}

		try {
			conditionsToCheck.add(new IndicesRule(sts));
		} catch (RuleNotConfiguredException e) {
		}

		try {
			conditionsToCheck.add(new MethodsRule(sts));
		} catch (RuleNotConfiguredException e) {
		}

		try {
			conditionsToCheck.add(new MaxBodyLengthRule(sts));
		} catch (RuleNotConfiguredException e) {
		}
	}

	public String getName() {
		return name;
	}

	public Policy getPolicy() {
		return policy;
	}

	public enum Policy {
		ALLOW, FORBID;

		public static String valuesString() {
			StringBuilder sb = new StringBuilder();
			for (Policy v : values()) {
				sb.append(v.toString()).append(",");
			}
			sb.deleteCharAt(sb.length() - 1);
			return sb.toString();
		}
	}

	public boolean check(RequestContext t) {
		boolean match = true;
		for (Rule condition : conditionsToCheck) {
			boolean condExitResult = condition.match(t);
			//System.out.println(condition.getClass().getName()+",condExitResult="+condExitResult);
			match &= condExitResult;
		}
		return match;
	}

	@Override
	public String toString() {
		return "Rules Block :: { name: '" + name + "', policy: " + policy + "}";
	}
}
