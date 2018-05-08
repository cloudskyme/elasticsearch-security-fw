package org.elasticsearch.security.fw.api.action;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.security.fw.auth.AuthException;
import org.elasticsearch.security.fw.dao.UserAclDao;
import org.elasticsearch.security.fw.dao.UserDao;
import org.elasticsearch.security.fw.dao.UserIndicesDao;
import org.elasticsearch.security.fw.domain.Page;
import org.elasticsearch.security.fw.domain.UserAcl;
import org.elasticsearch.security.fw.domain.UserIndices;

public class UserIndicesAction {

	public BytesRestResponse addUserIndices(String[] reserveindicesnames, UserDao userDao, UserIndicesDao dao, RestRequest restRequest){
		String username = restRequest.param("username");
		String indicesname = restRequest.param("indicesname");
		long maxsize = restRequest.paramAsLong("maxsize", 0);
		String describe = restRequest.param("describe");
		
		String message = "Add indices success";
		String status = "ok";
		try {
			if(username == null || "".equals(username)) {
				throw new Exception("User name can not empty");
			}
			if(indicesname==null || "".equals(indicesname)) {
				throw new Exception("Indices name can not empty");
			}
			if(!Pattern.compile("^[A-Z,a-z,0-9,_,-]+$").matcher(indicesname).find()){
				throw new Exception("Indices name must in [A-Z,a-z,0-9,_,-]");
			}
			if(indicesname.length()>255){
				throw new Exception("The max length of indices name is 255");
			}
			if(reserveindicesnames != null && reserveindicesnames.length > 0){
				for(String tmp:reserveindicesnames){
					boolean flag= false;
					if(tmp.indexOf('?')!=-1){
						tmp = tmp.replaceAll("\\?", "\\.");
						flag = true;
			        }
			        if(tmp.indexOf('*')!=-1){
			        	tmp = tmp.replaceAll("\\*", "\\.\\*");
			        	flag = true;
			        }
			        if(flag){
			        	tmp = "^"+tmp+"$";
			        	if(Pattern.compile(tmp).matcher(indicesname).find()){
			        		throw new Exception("The index name cannot be "+indicesname);
			        	}
			        }else if(indicesname.equalsIgnoreCase(tmp)){
						throw new Exception("The index name cannot be "+indicesname);
					}
				}
			}
			if(userDao.getUser(username)==null){
				throw new Exception("User name dose not exist");
			}
			UserIndices userindices = new UserIndices();
			userindices.setUsername(username);
			userindices.setIndicesname(indicesname);
			userindices.setMaxsize(maxsize);
			userindices.setDescribe(describe);
			dao.addIndices(userindices, true);
		} catch(Exception ex) {
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
			if("".equals(message)) {
				message = "Add indices fail";
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
		
	public BytesRestResponse removeUserIndices(UserAclDao aclDao, UserIndicesDao dao, RestRequest restRequest){
		String indicesname = restRequest.param("indicesname");
		String username = restRequest.param("username");
		
		String message="Remove user indices success";
		String status = "ok";
		try{
			if(indicesname==null || "".equals(indicesname)){
				throw new Exception("indices name can not empty");
			}
			if(username == null || "".equals(username)){
				throw new Exception("user name can not empty");
			}
			UserIndices userIndices = dao.getUserIndices(indicesname);
			if(userIndices==null || !userIndices.getUsername().equals(username)){
				throw new Exception("user indices does not exist");
			}
			String[] tmp = {indicesname};
			@SuppressWarnings("unchecked")
			List<UserAcl> useracl = (List<UserAcl>)aclDao.getAclListByIndicesname(username, tmp, 1, 2).getRows();
			if(useracl != null && useracl.size() != 0){
				throw new Exception("indices exists in the ACL, please delete the ACL");
			}
			dao.deleteUserIndices(indicesname);
		}catch(Exception ex){
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
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
		}
		
		BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, message);
		return bytesRestResponse;
	}
	
	public BytesRestResponse updateUserIndices(UserIndicesDao dao, RestRequest restRequest){
		System.out.println("uri="+restRequest.uri());
		String username = restRequest.param("username");
		String indicesname = restRequest.param("indicesname");
		long maxsize = restRequest.paramAsLong("maxsize", 0);
		String describe = restRequest.param("describe");
		
		String message="update indices success";
		String status = "ok";
		try{
			if(username==null || "".equals(username)){
				throw new Exception("user name can not empty");
			}
			if(indicesname==null || "".equals(indicesname)){
				throw new Exception("indices name can not empty");
			}
			UserIndices oldUserIndices = dao.getUserIndices(indicesname);
			if(oldUserIndices == null || !username.equals(oldUserIndices.getUsername())) {
				throw new Exception("The indices dose not exist");
			}
			
			UserIndices userIndices = new UserIndices();
			userIndices.setUsername(username);
			userIndices.setIndicesname(indicesname);
			userIndices.setMaxsize(maxsize);
			userIndices.setDescribe(describe);
			
			dao.updateUserIndices(userIndices);
		}catch(Exception ex){
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
			if("".equals(message)){
				message = "update user fail";
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
	
	public BytesRestResponse viewUserIndices(UserIndicesDao dao, RestRequest restRequest){
		String indicesname = restRequest.param("indicesname");
		String username = restRequest.param("username");
		
		String message="get user indices success";
		String status = "ok";
		UserIndices userIndices = null;
		try {
			if(indicesname==null || "".equals(indicesname)) {
				throw new Exception("The Indices name can not empty");
			}
			if(username==null || "".equals(username)){
				throw new Exception("user name can not empty");
			}
			userIndices = dao.getUserIndices(indicesname);
			if(userIndices==null || !username.equals(userIndices.getUsername())) {
				throw new Exception("The indices dose not exist");
			}
		} catch(Exception ex) {
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
			if("".equals(message)) {
				message = "view user fail";
			}
			status = "error";
		}
		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("status", status);
			if(status.equals("ok")) {
				jsonBuild.startObject("message")
				.field("id", userIndices.getId())
				.field("username",userIndices.getUsername())
				.field("indicesname", userIndices.getIndicesname())
				.field("maxsize",userIndices.getMaxsize())
				.field("createtime", userIndices.getCreatetime());
				if(userIndices.getDescribe()!=null){
					jsonBuild.field("describe", userIndices.getDescribe());
				}else{
					jsonBuild.field("describe", "");
				}
				jsonBuild.endObject();
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
	public BytesRestResponse listUserIndices(UserIndicesDao dao, RestRequest restRequest){
		String username = restRequest.param("username");
		String indicesname = restRequest.param("indicesname");
		
		int pageNum = restRequest.paramAsInt("pageNum", 0);
		int pageSize = restRequest.paramAsInt("pageSize", 50);
		
		String message="list user indices success";
		String status = "ok";
		Page page = null;
		try{
			page = dao.getUserIndicesList(username, indicesname, pageNum, pageSize);
		}catch(Exception ex){
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
			if("".equals(message)){
				message = "list user indices in fail";
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
				
				for(UserIndices userIndices: (List<UserIndices>)page.getRows()) {
					jsonBuild.startObject()
					.field("id",userIndices.getId())
					.field("username", userIndices.getUsername())
					.field("indicesname", userIndices.getIndicesname())
					.field("maxsize",userIndices.getMaxsize())
					.field("createtime", userIndices.getCreatetime());
					if(userIndices.getDescribe()!=null){
						jsonBuild.field("describe", userIndices.getDescribe());
					}else{
						jsonBuild.field("describe", "");
					}
					jsonBuild.endObject();
				}
				jsonBuild.endArray();
				jsonBuild.endObject();
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
	
	/**
	 * 1、检查“用户和转移目标用户”不能相同？
	 * 2、检查“转移目标用户”和“用户”是否存在？
	 * 3、检查“转移的索引”是否存在？
	 * 4、查询出所有的“acl中包含转移索引”，检查acl中是否包含其它索引名称？
	 * 
	 * 5、修改转移的所有索引的username->aimusername
	 * 6、修改第四步中查询的所有acl修改，username->aimusername
	 * 
	 * 7、修改完成。
	 * @param userdao
	 * @param userAcldao
	 * @param dao
	 * @param restRequest
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public BytesRestResponse transferUserIndices(UserDao userdao, UserAclDao userAcldao, UserIndicesDao dao, RestRequest restRequest){
		String[] indicesname = restRequest.paramAsStringArray("indicesname", null);
		String username = restRequest.param("username");
		String aimusername = restRequest.param("aimusername");
		
		
		String message="transfer user indices success";
		String status = "ok";
		try {
			if(indicesname == null || indicesname.length == 0) {
				throw new Exception("The Indices name can not empty");
			}
			if(username == null || "".equals(username)){
				throw new Exception("user name can not empty");
			}
			if(aimusername == null || "".equals(aimusername)){
				throw new Exception("transfer target user name can not empty");
			}
			
			if(aimusername.equals(username)){
				throw new Exception("user can not be the same as the transfer of user");
			}
			
			//check user
			if(userdao.getUser(username)==null){
				throw new Exception("user name dose not exist");
			}
			
			if(userdao.getUser(aimusername)==null){
				throw new Exception("transfer user name dose not exist");
			}
			String[] indicesIds = new String[indicesname.length];
			
			//check indices
			for(int i=0; i < indicesname.length; i++) {
				String tmp = indicesname[i];
				UserIndices tmp_userIndices = dao.getUserIndices(tmp);
				if(tmp_userIndices == null || !username.equals(tmp_userIndices.getUsername())) {
					throw new Exception("indices name("+tmp+") dose not exist");
				}
				indicesIds[i] = tmp_userIndices.getId();
			}
			//check acl
			int pageNum=1;
			int pageSize = 10000;
			Page aclPage = userAcldao.getAclListByIndicesname(username, indicesname, pageNum, pageSize);
			if(aclPage.getTotal() > pageSize){
				throw new Exception("need to transfer the ACL too much, the maximum support "+pageSize);
			}
			
			
			String conflictInfo = "";
			List<UserAcl> list = (List<UserAcl>)aclPage.getRows();
			for(UserAcl useracl : list){
				String aclId = useracl.getId();
				String[] aclIndicesname = useracl.getIndicesname();
				String conflictName = "";
				
				//maaclIds
				for(int i = 0; aclIndicesname != null && aclIndicesname.length > i; i++){
					boolean flag = false;					
					for(int j=0; j<indicesname.length; j++){
						if(indicesname[j].equals(aclIndicesname[i])){
							flag = true;
							break;
						}
					}
					if(!flag){
						conflictName += " "+aclIndicesname[i];
					}
				}
				if(!conflictName.equals("")){
					conflictInfo = " 授权id=" + aclId+"，冲突索引："+conflictName;
				}
			}
			
			if(!conflictInfo.equals("")){
				throw new Exception("转移的索引中，存在冲突的授权（授权中存在未转移的索引），请解除后在转移，具体信息："+conflictInfo);
			}
			//转移索引
			for(String id : indicesIds){
				dao.transferUserIndices(id, aimusername);
			}
			//转移acl
			for(UserAcl useracl : list){
				userAcldao.transferUserAcl(useracl.getId(), aimusername);
			}
		} catch(Exception ex) {
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
			if("".equals(message)) {
				message = "view user fail";
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
	
	public BytesRestResponse healthUserIndices(UserIndicesDao dao, RestRequest restRequest){
		String indicesname = restRequest.param("indicesname");
		String username = restRequest.param("username");
		
		String message="get user indices health success";
		String status = "ok";
		UserIndices userIndices = null;
		Map<String,Object> map = null;
		try {
			if(indicesname==null || "".equals(indicesname)) {
				throw new Exception("The Indices name can not empty");
			}
			if(username==null || "".equals(username)){
				throw new Exception("user name can not empty");
			}
			userIndices = dao.getUserIndices(indicesname);
			if(userIndices==null || !username.equals(userIndices.getUsername())) {
				throw new Exception("The indices dose not exist");
			}
			
			if(dao.existsUserIndices(indicesname)){
				map = dao.healthUserIndices(indicesname);
			}
		} catch(Exception ex) {
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
			if("".equals(message)) {
				message = "view user indices health fail";
			}
			status = "error";
		}
		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("status", status);
			if(status.equals("ok")) {
				jsonBuild.field("message",map);
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
	
	public BytesRestResponse statsUserIndices(UserIndicesDao dao, RestRequest restRequest){
		String indicesname = restRequest.param("indicesname");
		String username = restRequest.param("username");
		
		String message="get user indices stats success";
		String status = "ok";
		UserIndices userIndices = null;
		Map<String,Object> map = null;
		try {
			if(indicesname==null || "".equals(indicesname)) {
				throw new Exception("The Indices name can not empty");
			}
			if(username==null || "".equals(username)) {
				throw new Exception("user name can not empty");
			}
			userIndices = dao.getUserIndices(indicesname);
			if(userIndices==null || !username.equals(userIndices.getUsername())) {
				throw new Exception("The indices dose not exist");
			}
			
			if(dao.existsUserIndices(indicesname)) {
				map = dao.statsUserIndices(indicesname);
			}
		} catch(Exception ex) {
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
			if("".equals(message)) {
				message = "view user indices stats fail";
			}
			status = "error";
		}
		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("status", status);
			if(status.equals("ok")) {
				jsonBuild.field("message",map);
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
	
	public BytesRestResponse settingsUserIndices(UserIndicesDao dao, RestRequest restRequest){
		String indicesname = restRequest.param("indicesname");
		String username = restRequest.param("username");
		
		String message="get user indices settings success";
		String status = "ok";
		UserIndices userIndices = null;
		Map<String,Object> map = null;
		try {
			if(indicesname==null || "".equals(indicesname)) {
				throw new Exception("The Indices name can not empty");
			}
			if(username==null || "".equals(username)){
				throw new Exception("user name can not empty");
			}
			userIndices = dao.getUserIndices(indicesname);
			if(userIndices==null || !username.equals(userIndices.getUsername())) {
				throw new Exception("The indices dose not exist");
			}
			
			if(dao.existsUserIndices(indicesname)){
				map = dao.settingsUserIndices(indicesname);
			}
		} catch(Exception ex) {
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
			if("".equals(message)) {
				message = "view user indices settings fail";
			}
			status = "error";
		}
		try {
			XContentBuilder jsonBuild = XContentFactory.jsonBuilder();
			jsonBuild.startObject()
			.field("status", status);
			if(status.equals("ok")) {
				jsonBuild.field("message",map);
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
	
}
