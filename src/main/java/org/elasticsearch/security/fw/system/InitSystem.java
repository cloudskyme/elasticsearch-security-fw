package org.elasticsearch.security.fw.system;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.security.fw.common.cfg.Configuration;
import org.elasticsearch.security.fw.dao.UserAclDao;
import org.elasticsearch.security.fw.dao.UserDao;
import org.elasticsearch.security.fw.dao.UserIndicesDao;
import org.elasticsearch.security.fw.domain.User;
import org.elasticsearch.security.fw.domain.UserAcl;
import org.elasticsearch.security.fw.domain.UserIndices;

public class InitSystem {
	private static boolean isInited = false;
	
	public boolean initialization(Logger logger, NodeClient client, Configuration config){
		if(!InitSystem.isInited){
			if(!checkInited(client, config.getSecurityIndicename())){
				initSysIndice(client, config.getSecurityIndicename(), getSysMapping(config));
				try {
					initUser(client, config.getSecurityIndicename());
				} catch (Exception e) {
					logger.error("init system error", e);
					InitSystem.isInited = false;
					return false;
				}
			}
			InitSystem.isInited = true;
		}
		return true;
	}
	
	private boolean checkInited(NodeClient client, String indicesname){
		return client.admin().indices().prepareExists(indicesname).execute().actionGet().isExists();		
	}
	
	private void initSysIndice(NodeClient client, String indicesname, Map<String, String> mapping){
//		CreateIndexRequest request = new CreateIndexRequest(); 
		//request.source(source)
//		client.admin().indices().create(request);
		//client.admin().indices().prepareCreate("dd").addMapping("", "").addMapping(type, source)
		
		client.admin().indices()
			.prepareCreate(indicesname)
			.addMapping("user", mapping.get("user"))
			.addMapping("useracl", mapping.get("useracl"))
			.addMapping("userindices", mapping.get("userindices"))
			.execute().actionGet();	
		
	}

	private void initUser(NodeClient client, String indicesname)throws Exception{
//		Map<String, Object> userMap = new HashMap<String,Object>();
		Map<String, Object> metadata = new HashMap<String,Object>();
		metadata.put("candelete", false);
//		userMap.put("username", "elastic");
//		userMap.put("secretkey", MD5Util.MD5("changeme"));
//		userMap.put("createtime", new Date());		
//		userMap.put("metadata", metadata);
//		client.prepareIndex(indicesname, "user").setSource(userMap).execute().actionGet();
		
		//elatic user
		UserDao userDao = new UserDao(client, indicesname);
		User user = new User();
		user.setUsername("elastic");
		user.setSecretkey("changeme");
		user.setCreatetime(new Date());
		user.setMetadata(metadata);
		userDao.addUser(user);
		
		//elastic indice 
		UserIndicesDao userIndicesDao = new UserIndicesDao(client, indicesname);
		
		UserIndices userIndice = new UserIndices();
		userIndice.setIndicesname("<no-index>");
		userIndice.setUsername("elastic");
		userIndice.setMaxsize(0);
		userIndice.setCreatetime(new Date());
		userIndice.setDescribe("system init, for all indices");
		userIndicesDao.addIndices(userIndice, false);
		
	    //elastic acl
		UserAclDao useraclDao = new UserAclDao(client, indicesname);
		UserAcl useracl = new UserAcl();
		String[] ary_indicesname = {"<no-index>"};
		useracl.setIndicesname(ary_indicesname);
		useracl.setAuthkey("elastic");
		useracl.setUsername("elastic");
		useracl.setAuthuser("elastic");
		useracl.setCreatetime(new Date());
		useracl.setType("allow");
		useraclDao.addUserAcl(useracl);	   
	}
	
	private Map<String, String> getSysMapping(Configuration config){
		String mapping = "{"
			+ "\"mappings\":{"
				+ "\"user\":{"
					+ "\"properties\":{"
						+ "\"username\":{"
							+ "\"type\":\"string\","
							+ "\"index\":\"not_analyzed\""
						+ "},"
						+ "\"secretkey\":{"
							+ "\"type\":\"string\","
							+ "\"index\":\"not_analyzed\""
						+ "}"
						+ "\"createtime\":{"
							+ "\"format\":\"strict_date_optional_time||epoch_millis\","
							+ "\"type\":\"date\""
						+ "}"
					+ "}"
				+ "},"
				+ "\"useracl\":{"
					+ "\"properties\": {"
						+ "\"authkey\":{"
							+ "\"type\":\"string\","
							+ "\"index\":\"not_analyzed\""
						+ "},"
						+ "\"method\":{"
							+ "\"type\":\"string\""
						+ "},"
						+ "\"hosts\":{"
							+ "\"type\": \"string\","
							+ "\"index\":\"not_analyzed\""
						+ "},"
						+ "\"xforwarded\":{"
							+ "\"type\": \"boolean\""
						+ "},"
						+ "\"type\": {"
							+ "\"type\": \"boolean\","
							+ "\"index\":\"not_analyzed\""
						+ "},"
						+ "\"actions\": {"
						+ "\"type\": \"string\""
						+ "},"
						+ "\"authuser\":{"
							+ "\"type\": \"string\","
							+ "\"index\":\"not_analyzed\""
						+ "},"
						+ "\"maxBodyLength\":{"
							+ "\"type\": \"long\""
						+ "},"
						+ "\"username\": {"
							+ "\"type\": \"string\","
							+ "\"index\":\"not_analyzed\""
						+ "},"
						+ "\"createtime\":{"
							+ "\"format\":\"strict_date_optional_time||epoch_millis\","
							+ "\"type\":\"date\""
						+ "}"
					+ "}"
				+ "},"
				+ "\"userindices\":{"
					+ "\"properties\": {"
						+ "\"username\": {"
							+ "\"type\": \"string\","
							+ "\"index\":\"not_analyzed\""
						+ "},"
						+ "\"indicesname\":{"
							+ "\"type\": \"string\","
							+ "\"index\":\"not_analyzed\""
						+ "},"
						+ "\"maxsize\":{"
							+ "\"type\": \"long\""
						+ "},"
						+ "\"describe\":{"
							+ "\"type\": \"string\""
						+ "},"
						+ "\"createtime\":{"
							+ "\"format\":\"strict_date_optional_time||epoch_millis\","
							+ "\"type\":\"date\""
						+ "}"
					+ "}"
				+ "}"
			+ "}"
		+ "}";
		
		String userMapping = "{"
				+ "\"properties\":{"
				+ "\"username\":{"
					+ "\"type\":\"string\","
					+ "\"index\":\"not_analyzed\""
				+ "},"
				+ "\"secretkey\":{"
					+ "\"type\":\"string\","
					+ "\"index\":\"not_analyzed\""
				+ "},"
				+ "\"createtime\":{"
					+ "\"format\":\"strict_date_optional_time||epoch_millis\","
					+ "\"type\":\"date\""
				+ "}"
			+ "}"
		+ "}";
		
		String useraclMapping = "{"
				+ "\"properties\": {"
				+ "\"authkey\":{"
					+ "\"type\":\"string\","
					+ "\"index\":\"not_analyzed\""
				+ "},"
				+ "\"method\":{"
					+ "\"type\":\"string\""
				+ "},"
				+ "\"hosts\":{"
					+ "\"type\": \"string\","
					+ "\"index\":\"not_analyzed\""
				+ "},"
				+ "\"xforwarded\":{"
					+ "\"type\": \"boolean\""
				+ "},"
				+ "\"type\": {"
					+ "\"type\": \"boolean\","
					+ "\"index\":\"not_analyzed\""
				+ "},"
				+ "\"actions\": {"
				+ "\"type\": \"string\""
				+ "},"
				+ "\"authuser\":{"
					+ "\"type\": \"string\","
					+ "\"index\":\"not_analyzed\""
				+ "},"
				+ "\"maxBodyLength\":{"
					+ "\"type\": \"long\""
				+ "},"
				+ "\"username\": {"
					+ "\"type\": \"string\","
					+ "\"index\":\"not_analyzed\""
				+ "},"
				+ "\"createtime\":{"
					+ "\"format\":\"strict_date_optional_time||epoch_millis\","
					+ "\"type\":\"date\""
				+ "}"
			+ "}"
		+ "}";
		
		String userindicesMapping = "{"
				+ "\"properties\": {"
				+ "\"username\": {"
					+ "\"type\": \"string\","
					+ "\"index\":\"not_analyzed\""
				+ "},"
				+ "\"indicesname\":{"
					+ "\"type\": \"string\","
					+ "\"index\":\"not_analyzed\""
				+ "},"
				+ "\"maxsize\":{"
					+ "\"type\": \"long\""
				+ "},"
				+ "\"describe\":{"
					+ "\"type\": \"string\""
				+ "},"
				+ "\"createtime\":{"
					+ "\"format\":\"strict_date_optional_time||epoch_millis\","
					+ "\"type\":\"date\""
				+ "}"
			+ "}"
		+ "}";
		Map<String, String> mappings = new HashMap<String, String>();
		mappings.put("user", userMapping);
		mappings.put("useracl", useraclMapping);
		mappings.put("userindices", userindicesMapping);
		
		return mappings;
	}

}
