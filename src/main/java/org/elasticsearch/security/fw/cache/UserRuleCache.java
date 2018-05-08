package org.elasticsearch.security.fw.cache;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestController;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.security.fw.api.action.GetAuthAclListRestAction;
import org.elasticsearch.security.fw.common.cfg.Configuration;
import org.elasticsearch.security.fw.common.http.HttpRequest;

public class UserRuleCache {
	
	static Map<String, String[]> apiRule = new HashMap<String,String[]>();
	
	public static Settings getUserSettingsFromURL(Logger logger, String apiUrl, String authHeader, int catchTime){
		//System.out.println(apiUrl+","+authHeader);
		String ruleStr = null;
		try{
			String[] ruleArray = UserRuleCache.apiRule.get(authHeader);
			Date dt = new Date();
			
			if(ruleArray != null && Long.toString(dt.getTime()).compareTo(ruleArray[1]) < 0){
				ruleStr = ruleArray[2];
			}
			if(ruleStr == null) {
				try {
					if(UserRuleCache.apiRule.keySet() != null){
						Iterator<String> iterator = UserRuleCache.apiRule.keySet().iterator();
						if(iterator.hasNext()){
							String key = iterator.next();
							String[] tmp_ruleArray = UserRuleCache.apiRule.get(key);
							if(tmp_ruleArray != null && dt.getTime() > Long.parseLong(tmp_ruleArray[1]) + (catchTime+1) * 60){
								UserRuleCache.apiRule.remove(key);
							}
						}
					}
					logger.debug("cache no rule,get rule from api, Authorization:"+authHeader+",ruleStr:"+ruleStr);
					
					Map<String,String> headerMap = new HashMap<String,String>();
					headerMap.put("Authorization", authHeader);
					ruleStr = HttpRequest.sendPost(apiUrl, null, headerMap);
					
					if(ruleStr==null || ruleStr.equals("")){
						throw new Exception();
					}
					Calendar nowTime = Calendar.getInstance();
					nowTime.add(Calendar.SECOND, catchTime);					
					String[] tmp = {authHeader, Long.toString(nowTime.getTime().getTime()), ruleStr};
					UserRuleCache.apiRule.put(authHeader, tmp);
				} catch(Exception ex){
					if(ruleArray != null){
						ruleStr = ruleArray[2];
					}
				}
			}
			logger.debug("Authorization:"+authHeader+"ruleStr:"+ruleStr);
			Settings g = Settings.builder().loadFromSource(ruleStr).build();
			
			return g;
		}catch(Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	public static Settings getUserSettingsFromElas(Logger logger, Configuration config, Settings settings, RestController controller,RestRequest request, NodeClient client, int catchTime){
		//System.out.println(apiUrl+","+authHeader);
		String ruleStr = null;
		try{
			String authHeader = request.header("Authorization");
			String[] ruleArray = UserRuleCache.apiRule.get(authHeader);
			Date dt = new Date();
			
			if(ruleArray != null && Long.toString(dt.getTime()).compareTo(ruleArray[1]) < 0){
				ruleStr = ruleArray[2];
			}
			if(ruleStr == null) {
				try {
					if(UserRuleCache.apiRule.keySet() != null){
						Iterator<String> iterator = UserRuleCache.apiRule.keySet().iterator();
						if(iterator.hasNext()){
							String key = iterator.next();
							String[] tmp_ruleArray = UserRuleCache.apiRule.get(key);
							if(tmp_ruleArray != null && dt.getTime() > Long.parseLong(tmp_ruleArray[1]) + (catchTime+1) * 60){
								UserRuleCache.apiRule.remove(key);
							}
						}
					}
					logger.debug("cache no rule,get rule from api, Authorization:"+authHeader+",ruleStr:"+ruleStr);
					
					GetAuthAclListRestAction ListRestAction = new GetAuthAclListRestAction(config, settings, controller);
            	    BytesRestResponse res = ListRestAction.processRequest(request, client);
            	    if(res.status() == RestStatus.OK){
            		  ruleStr = res.content().utf8ToString();
            	    }
					
					if(ruleStr==null || ruleStr.equals("")){
						throw new Exception();
					}
					Calendar nowTime = Calendar.getInstance();
					nowTime.add(Calendar.SECOND, catchTime);					
					String[] tmp = {authHeader, Long.toString(nowTime.getTime().getTime()), ruleStr};
					UserRuleCache.apiRule.put(authHeader, tmp);
				} catch(Exception ex){
					ex.printStackTrace();
					if(ruleArray != null){
						ruleStr = ruleArray[2];
					}
				}
			}
			logger.debug("Authorization:"+authHeader+"ruleStr:"+ruleStr);
			Settings g = Settings.builder().loadFromSource(ruleStr).build();
			
			return g;
		}catch(Exception ex){
			ex.printStackTrace();
			
			return null;
		}
	}
}
