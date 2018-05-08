package org.elasticsearch.security.fw.api.action;

import java.util.List;
import java.util.UUID;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.security.fw.dao.UserAclDao;
import org.elasticsearch.security.fw.dao.UserDao;
import org.elasticsearch.security.fw.dao.UserIndicesDao;
import org.elasticsearch.security.fw.domain.Page;
import org.elasticsearch.security.fw.domain.UserAcl;
import org.elasticsearch.security.fw.domain.UserIndices;

public class UserAclAction {
	public BytesRestResponse addUserAcl(UserDao userDao, UserIndicesDao userIndicesDao, UserAclDao dao, RestRequest restRequest) {
//		System.out.println("addUserAcl run.....in");
		String username = restRequest.param("username");
		String authkey = restRequest.param("authkey");
		String authuser = restRequest.param("authuser");
		String[] indicesname = restRequest.paramAsStringArray("indicesname", null);
		String type = restRequest.param("type","allow");
		
		String[] actions = restRequest.paramAsStringArray("actions", null);
		String[] method = restRequest.paramAsStringArray("method", null);
		boolean xforwarded = restRequest.paramAsBoolean("xforwarded", false);
		int maxBodyLength = restRequest.paramAsInt("maxBodyLength", 0);
		String[] hosts = restRequest.paramAsStringArray("hosts", null);
		UUID uuid = UUID.randomUUID();
		String str = uuid.toString();
		authkey = str.substring(0, 8) + str.substring(9, 13) + str.substring(14, 18) + str.substring(19, 23) + str.substring(24);
		String message="Add user acl success";
		String status = "ok";
		try {
			if(username==null || "".equals(username)) {
				throw new Exception("user name can not empty");
			}
//			if(authkey==null || "".equals(authkey)) {
//				throw new Exception("auth key can not empty");
//			}
			if(authuser==null || "".equals(authuser)){
				throw new Exception("auth user can not empty");
			}
			if(indicesname==null) {
				throw new Exception("indices name can not empty");
			}
			if(type != null && !"allow".equals(type) && !"forbid".equals(type)){
				throw new Exception("type must in [allow,forbid");
			}
			if(userDao.getUser(username)==null){
				throw new Exception("user name dose not exist"); 
			}
			if(actions != null && actions.length != 0){
			   for(String action: actions){
				   if(!action.startsWith("indices:data/") && !action.startsWith("indices:admin/") && !action.startsWith("indices:monitor/")){
					   throw new Exception("actions��ʽ����ȷ"); 
				   }
			   }
			}
			if(userDao.getUser(authuser)==null){
				throw new Exception("auth user dose not exist"); 
			}
			for(String name : indicesname){
				UserIndices tmp = userIndicesDao.getUserIndices(name);
				if(tmp==null || !tmp.getUsername().equals(username)){
					throw new Exception("indices name("+name+") dose not exist"); 
				}
			}
//			System.out.println(actions+","+method+",22222");
			if(actions == null || actions.length == 0){
				actions = new String[3];
				actions[0] = "indices:data/*";
				actions[1] = "indices:admin/*";
				actions[2] = "indices:monitor/*";
			}
//			System.out.println(actions+","+method+",33333");
			UserAcl useracl = new UserAcl();
			useracl.setUsername(username);
			useracl.setAuthkey(authkey);
			useracl.setAuthuser(authuser);
			useracl.setIndicesname(indicesname);
			useracl.setType(type);
			
			useracl.setActions(actions);
			useracl.setMethod(method);
			useracl.setXforwarded(xforwarded);			
			useracl.setMaxBodyLength(maxBodyLength);
			useracl.setHosts(hosts);
//			System.out.println(actions+","+method+",44444");
			dao.addUserAcl(useracl);
//			System.out.println(actions+","+method+",55555");
		} catch(Exception ex) {
			message = ex.getMessage();
			if("".equals(message)) {
				message = "Add user acl fail";
			}
			status = "error";
		}
		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("status", status)
			.field("message", message)
			.endObject();
			message = jsonBuild.string();
		} catch(Exception ex) {
		}
        
		BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, message);
		return bytesRestResponse;
	}
	
	public BytesRestResponse updateUserAcl(UserDao userDao, UserIndicesDao userIndicesDao, UserAclDao dao, RestRequest restRequest) {
		String id = restRequest.param("id");
		String username = restRequest.param("username");
		String authuser = restRequest.param("authuser");
		String[] indicesname = restRequest.paramAsStringArray("indicesname", null);
		String type = restRequest.param("type","allow");
		
		String[] actions = restRequest.paramAsStringArray("actions", null);
		String[] method = restRequest.paramAsStringArray("method", null);
		boolean xforwarded = restRequest.paramAsBoolean("xforwarded", false);
		int maxBodyLength = restRequest.paramAsInt("maxBodyLength", 0);
		String[] hosts = restRequest.paramAsStringArray("hosts", null);
		String message="Add user acl success";
		String status = "ok";
		try {
			if(id==null || "".equals(id)) {
				throw new Exception("acl id can not empty");
			}
			
			if(username==null || "".equals(username)) {
				throw new Exception("user name can not empty");
			}
//			if(authkey==null || "".equals(authkey)) {
//				throw new Exception("auth key can not empty");
//			}
			if(authuser==null || "".equals(authuser)){
				throw new Exception("auth user can not empty");
			}
			if(indicesname==null) {
				throw new Exception("indices name can not empty");
			}
			if(type != null && !"allow".equals(type) && !"forbid".equals(type)){
				throw new Exception("type must in [allow,forbid");
			}
			if(userDao.getUser(username)==null){
				throw new Exception("user name dose not exist"); 
			}
			if(actions != null && actions.length != 0){
			   for(String action: actions){
				   if(!action.startsWith("indices:data/") && !action.startsWith("indices:admin/") && !action.startsWith("indices:monitor/")){
					   throw new Exception("actions��ʽ����ȷ"); 
				   }
			   }
			}
			if(userDao.getUser(authuser)==null){
				throw new Exception("auth user dose not exist"); 
			}
			for(String name : indicesname){
				UserIndices tmp = userIndicesDao.getUserIndices(name);
				if(tmp==null || !tmp.getUsername().equals(username)){
					throw new Exception("indices name("+name+") dose not exist"); 
				}
			}
//			System.out.println(actions+","+method+",22222");
			if(actions == null || actions.length == 0){
				actions = new String[3];
				actions[0] = "indices:data/*";
				actions[1] = "indices:admin/*";
				actions[2] = "indices:monitor/*";
			}
//			System.out.println(actions+","+method+",33333");
			UserAcl useracl = new UserAcl();
			useracl.setId(id);
			useracl.setUsername(username);
			useracl.setAuthuser(authuser);
			useracl.setIndicesname(indicesname);
			useracl.setType(type);
			
			useracl.setActions(actions);
			useracl.setMethod(method);
			useracl.setXforwarded(xforwarded);			
			useracl.setMaxBodyLength(maxBodyLength);
			useracl.setHosts(hosts);
			dao.updateUserAcl(useracl);
		} catch(Exception ex) {
			message = ex.getMessage();
			if("".equals(message)) {
				message = "update user acl fail";
			}
			status = "error";
		}
		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("status", status)
			.field("message", message)
			.endObject();
			message = jsonBuild.string();
		} catch(Exception ex) {
		}
        
		BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, message);
		return bytesRestResponse;
	}
	
	public BytesRestResponse viewUserAcl(UserAclDao dao, RestRequest restRequest) {
		String id = restRequest.param("id");
		String username = restRequest.param("username");
		
		String message = "get user acl success";
		String status = "ok";
		UserAcl acl = null;
		try {
			if(id==null || "".equals(id)) {
				throw new Exception("acl id can not empty");
			}
			
			acl = dao.getUserAcl(id);
			if(acl==null || !acl.getUsername().equals(username)) {
				throw new Exception("acl dose not exist");
			}
		} catch(Exception ex) {
			message = ex.getMessage();
			if("".equals(message)) {
				message = "Add user acl fail";
			}
			status = "error";
		}
		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("status", status);
			if(status.equals("ok")) {
				jsonBuild.startObject("message")
				.field("id", acl.getId())
				.field("username",acl.getUsername())
				.field("authuser", acl.getAuthuser())
				.field("indicesname", acl.getIndicesname())
				.field("type", acl.getType())
				.field("createtime", acl.getCreatetime());
				if(acl.getActions() != null){
					jsonBuild.array("actions", acl.getActions());
				}
				if(acl.getMethod()!=null){
					jsonBuild.array("method", acl.getMethod());
				}
				jsonBuild.field("xforwarded", acl.isXforwarded());
				if(acl.isXforwarded()){
					jsonBuild.field("xforwarded", acl.isXforwarded());
				}
				if(acl.getMaxBodyLength()!=0){
					jsonBuild.field("maxBodyLength", acl.getMaxBodyLength());
				}
				if(acl.getHosts()!=null){
					jsonBuild.array("hosts", acl.getHosts());
				}
				if(acl.getAuthkey()!=null){
					jsonBuild.field("authkey",acl.getAuthkey());
				}
				jsonBuild.endObject();
			} else {
				jsonBuild.field("message", message);
			}
			jsonBuild.endObject();
			message = jsonBuild.string();
		} catch(Exception ex) {
		}
		BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, message);
		return bytesRestResponse;
	}
	
	public BytesRestResponse removeUserAcl(UserAclDao dao, RestRequest restRequest){
		String username = restRequest.param("username");
		String id = restRequest.param("id");
		
		String message="Remove user acl success";
		String status = "ok";
		try{
			if(username==null || "".equals(username)) {
				throw new Exception("user name can not empty");
			}
			if(id==null || "".equals(id)) {
				throw new Exception("acl id can not empty");
			}
			UserAcl tmp =dao.getUserAcl(id);
			if(tmp == null || !tmp.getUsername().equals(username)){
				throw new Exception("acl dose not exist");
			}
			dao.deleteUserAcl(id);
			
		}catch(Exception ex) {
			message = ex.getMessage();
			if("".equals(message)){
				message = "remove user fail";
			}
			status = "error";
		}
		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("status", status)
			.field("message", message)
			.endObject();
			message = jsonBuild.string();
		} catch(Exception ex) {
			message = ex.getMessage();
		}
		
		BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, message);
		return bytesRestResponse;
	}
	
	@SuppressWarnings("unchecked")
	public BytesRestResponse listUserAcl(UserAclDao dao, RestRequest restRequest) {
		String username = restRequest.param("username");
		int pageNum = restRequest.paramAsInt("pageNum", 0);
		int pageSize = restRequest.paramAsInt("pageSize", 50);
		String authuser = restRequest.param("authuser");
		String authkey = restRequest.param("authkey");
		String message="list user success";
		String status = "ok";
		Page page = null;
		try {
			page = dao.getUserAclList(username, authuser, authkey, pageNum, pageSize);
		} catch(Exception ex) {
			message = ex.getMessage();
			if("".equals(message)) {
				message = "update user fail";
			}
			status = "error";
		}
		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("status", status);
			if(status.equals("ok")){
				jsonBuild.startObject("message")
				.field("total", page.getTotal())
				.field("pageNum",page.getPageNum())
				.field("pageSize", page.getPageSize())
				.startArray("rows");
				
				for(UserAcl acl: (List<UserAcl>)page.getRows()){
					jsonBuild.startObject()					
					.field("id", acl.getId())
					.field("username",acl.getUsername())
					.field("authuser", acl.getAuthuser())
					.field("indicesname", acl.getIndicesname())
					.field("type", acl.getType())
					.field("createtime", acl.getCreatetime());
					if(acl.getActions() != null){
						jsonBuild.array("actions", acl.getActions());
					}
					if(acl.getMethod()!=null){
						jsonBuild.array("method", acl.getMethod());
					}
					jsonBuild.field("xforwarded", acl.isXforwarded());
					if(acl.isXforwarded()){
						jsonBuild.field("xforwarded", acl.isXforwarded());
					}
					if(acl.getMaxBodyLength()!=0){
						jsonBuild.field("maxBodyLength", acl.getMaxBodyLength());
					}
					if(acl.getHosts()!=null){
						jsonBuild.array("hosts", acl.getHosts());
					}
					if(acl.getAuthkey()!=null){
						jsonBuild.field("authkey",acl.getAuthkey());
					}
					jsonBuild.endObject();
				}
				jsonBuild.endArray()
				.endObject();
			} else {
				jsonBuild.field("message", message);
			}
			jsonBuild.endObject();
			message = jsonBuild.string();
		} catch(Exception ex) {
			message = ex.getMessage();
		}
		
		BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, message);
		return bytesRestResponse;
	}
	
	@SuppressWarnings("unchecked")
	public BytesRestResponse listAuthAcl(String authuser, String authkey, UserAclDao dao) {
		Page page = null;
		String status = "ok";
		String message = "";
		
//		if("elastic".equals(authuser)){
//			message = "access_control_rules:\n"
//					+ "- name: elastic\n"
//					+ "- type: allow\n"
//					//+ "- actions: [\"*\"]\n"
//					+ "- indices: [<no-index>]\n";
//			BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, message);
//			return bytesRestResponse;
//		}
		
		try {
			page = dao.getUserAclList(null, authuser, authkey, 1, 1000);
		} catch(Exception ex) {
			status = "error";
		}
		
		try {
			String str= "access_control_rules:\n";
			if(status.equals("ok")){
				for(UserAcl acl: (List<UserAcl>)page.getRows()){
					str += "- name: " + acl.getId() + "\n";
					str += "  type: " + acl.getType() +"\n";
					
					if(acl.getActions() != null && acl.getActions().length > 0){
						String[] tmp = acl.getActions();						
						str += "  actions: [";
						for(int j=0; j<tmp.length; j++){
							if(j!=0){
								str += ",";
							}
							str += "\""+tmp[j]+"\"";
						}
						str += "]\n";
					}
					if(acl.getMethod()!=null && acl.getActions().length > 0){
						String[] tmp = acl.getMethod();						
						str += "  methods: [";
						for(int j=0; j<tmp.length; j++){
							if(j!=0){
								str += ",";
							}
							str += "\""+tmp[j]+"\"";
						}
						str += "]\n";
					}
					if(acl.getMaxBodyLength()!=0){
						str += "  maxBodyLength: "+acl.getMaxBodyLength()+"\n";
					}
					if(acl.isXforwarded()){
						str += "  accept_x-forwarded-for_header: true\n";
					}
					if(acl.getHosts()!=null && acl.getHosts().length > 0){
						String[] tmp = acl.getHosts();						
						str += "  hosts: [";
						for(int j=0; j<tmp.length; j++){
							if(j!=0){
								str += ",";
							}
							str += "\""+tmp[j]+"\"";
						}
						str += "]\n";
					}
					String[] tmp = acl.getIndicesname();						
					str += "  indices: [";
					for(int j=0; j<tmp.length; j++){
						if(j!=0){
							str += ",";
						}
						str += "\""+tmp[j]+"\"";
					}
					str += "]\n";
				}
				message = str;
				BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, message);
				return bytesRestResponse;
			} else {
			}
			
		} catch(Exception ex) {
			//ex.printStackTrace();
		}
		BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.NOT_FOUND, "no acl");
		return bytesRestResponse;
	}
}
