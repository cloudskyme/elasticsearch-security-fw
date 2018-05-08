package org.elasticsearch.security.fw.api.action;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.rest.BytesRestResponse;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.security.fw.auth.AuthException;
import org.elasticsearch.security.fw.dao.UserDao;
import org.elasticsearch.security.fw.domain.Page;
import org.elasticsearch.security.fw.domain.User;

public class UserAction {

	public BytesRestResponse addUser(UserDao dao, RestRequest restRequest){
		String username = restRequest.param("username");
		String secretkey = restRequest.param("secretkey");
		String message="Add user success";
		String status = "ok";
		try{
			if(username==null || "".equals(username)){
				throw new Exception("user name can not empty");
			}
			if(secretkey==null || "".equals(secretkey)){
				throw new Exception("user secretkey can not empty");
			}
			User user = new User();
			user.setUsername(username);
			user.setSecretkey(secretkey);
			dao.addUser(user);
		}catch(Exception ex){
			if(ex.getCause()!=null && ex.getCause() instanceof AuthException){
				message = ((AuthException)ex.getCause()).getMessage();
			}else{
				message = ex.getMessage();
			}
			if("".equals(message)){
				message = "Add user fail";
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
	
	public BytesRestResponse removeUser(UserDao dao, RestRequest restRequest){
		String username = restRequest.param("username");
		
		String message="Remove user success";
		String status = "ok";
		try{
			if(username==null || "".equals(username)){
				throw new Exception("user name can not empty");
			}
			dao.deleteUser(username);
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
	
	public BytesRestResponse viewUser(UserDao dao, RestRequest restRequest){
		String username = restRequest.param("username");
		
		String message="get user success";
		String status = "ok";
		String code = "0";
		User user = null;
		
		try {
			if(username==null || "".equals(username)) {
				throw new Exception("user name can not empty");
			}
			user = dao.getUser(username);
			if(user==null) {
				code = "101";
				throw new Exception("user dose not exist");
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
			.field("code", code);
			if(status.equals("ok")) {
				jsonBuild.startObject("message")
				.field("id", user.getId())
				.field("username",user.getUsername())
				.field("secretkey", user.getSecretkey())
				.field("createtime", user.getCreatetime())
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
	
	public BytesRestResponse updateUser(UserDao dao, RestRequest restRequest){
		String username = restRequest.param("username");
		String secretkey = restRequest.param("secretkey");
		
		String message="update user success";
		String status = "ok";
		try{
			if(username==null || "".equals(username)){
				throw new Exception("user name can not empty");
			}
			if(secretkey==null || "".equals(secretkey)){
				throw new Exception("user name can not empty");
			}
			User user = new User();
			user.setUsername(username);
			user.setSecretkey(secretkey);
			dao.updateUser(user);
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
	
	public BytesRestResponse listUser(UserDao dao, RestRequest restRequest){
		String username = restRequest.param("username");
		int pageNum = restRequest.paramAsInt("pageNum", 0);
		int pageSize = restRequest.paramAsInt("pageSize", 50);
		
		String message="list user success";
		String status = "ok";
		Page page = null;
		try{
			page = dao.getUserList(username, pageNum, pageSize);			
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
			.field("status", status);
			if(status.equals("ok")){
				jsonBuild.startObject("message")
				.field("total", page.getTotal())
				.field("pageNum",page.getPageNum())
				.field("pageSize", page.getPageSize())
				.startArray("rows");
				
				for(User user: (List<User>)page.getRows()){
					jsonBuild.startObject()
					.field("id",user.getId())
					.field("username", user.getUsername())
					.field("secretkey", user.getSecretkey())
					.field("createtime", user.getCreatetime())
					.endObject();
				}
				jsonBuild.endArray()
				.endObject();
			}else{
				jsonBuild.field("message", message);
			}
			message = jsonBuild.string();
		} catch(Exception ex) {
		}
		
		BytesRestResponse bytesRestResponse = new BytesRestResponse(RestStatus.OK, message);
		return bytesRestResponse;
	}
	
	public static void main(String[] args){
		try{
			List<Object> userlist = new Vector<Object>();
			User user1 = new User();
			User user2 = new User();
			
			user1.setId("dadsa");
			user1.setUsername("dadsa");
			user1.setSecretkey("dwwew");
			user1.setCreatetime(new Date());

			user2.setId("dadsae");
			user2.setUsername("dadsawe");
			user2.setSecretkey("dwwewew");
			user2.setCreatetime(new Date());
			Map<String,Object> a1 = new HashMap<String,Object>();
			a1.put("das", "dsadsa");
			a1.put("dda", new Date());
			Map<String,Object>  a2=  new HashMap<String,Object>();
			a2.put("das", "dsadsa");
			a2.put("dda", new Date());
			a1.put("a2", a2);
			a1.put("ddddd", null);
			userlist.add(a1);
			//userlist.add(user2);
		XContentBuilder jsonBuild = XContentFactory.yamlBuilder();
		jsonBuild.startObject();
        jsonBuild.field("dsads",userlist);
        jsonBuild.endObject();
//        jsonBuild.startArray("abcd");        
//        jsonBuild.startObject()
//        .field("dsa","dds")
//        .field("cc",new Date())
//        .endObject();
//        
//        jsonBuild.endArray();
        
        String jsonData = jsonBuild.string();
        
        //System.out.println(Pattern.compile("^[A-Z,a-z,0-9,_]+$").matcher("dads23adsad").find());
        String ss= "readonlyr?st*";
        if(ss.indexOf('?')>0){
        	ss = ss.replaceAll("\\?", "\\.");
        }
        if(ss.indexOf('*')>0){
        	ss = ss.replaceAll("\\*", "\\.\\*");
        }
        ss = "^"+ss+"$";
//        System.out.println(ss);
//        System.out.println(Pattern.compile(ss).matcher("readonlyrestdds").find());
        //System.out.println(jsonData);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
