package org.elasticsearch.security.fw.acl.rule.support;

import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.security.fw.common.cfg.Configuration;

public class MatcherWithWildcards {

    protected List<String> allMatchers = new Vector<String>();
    protected List<Pattern> wildcardMatchers = new Vector<Pattern>();

    public List<String> getMatchers() {
        return allMatchers;
    }

    public MatcherWithWildcards(Settings s, String key) throws RuleNotConfiguredException {
        // Will work with single, non array conf.
        String[] a = s.getAsArray(key);

        if (a == null || a.length == 0) {
            throw new RuleNotConfiguredException();
        }

        for (int i = 0; i < a.length; i++) {
            a[i] = normalizePlusAndMinusIndex(a[i]);
            if (Configuration.isNullOrEmpty(a[i])) {
                continue;
            }
            if (a[i].contains("*")) {
                // Patch the simple star wildcard to become a regex: ("*" -> ".*")
                String regex = ("\\Q" + a[i] + "\\E").replace("*", "\\E.*\\Q");

                // Pre-compile the regex pattern matcher to validate the regex
                // AND faster matching later on.
                wildcardMatchers.add(Pattern.compile(regex));

                // Let's match this also literally
                allMatchers.add(a[i]);
            } else {
                // A plain word can be matched as string
                allMatchers.add(a[i].trim());
            }
        }
    }

    /**
     * Returns null if the matchable is not worth processing because it's invalid or starts with "-"
     */
    private static String normalizePlusAndMinusIndex(String s) {
        if (Configuration.isNullOrEmpty(s)) {
            return null;
        }
        // Ignore the excluded indices
        if (s.startsWith("-")) {
            return null;
        }
        // Call included indices with their name
        if (s.startsWith("+")) {
            if (s.length() == 1) {
                return null;
            }
            return s.substring(1, s.length());
        }
        return s;
    }

    public boolean match(String matchable) {

        matchable = normalizePlusAndMinusIndex(matchable);

        if (matchable == null) {
            return false;
        }

        // Try to match plain strings first
        if (allMatchers.contains(matchable)) {
            return true;
        }

        for (Pattern p : wildcardMatchers) {
            Matcher m = p.matcher(matchable);
            if (m == null) {
                continue;
            }
            if (m.find()) {
                return true;
            }
        }

        return false;
    }
}
