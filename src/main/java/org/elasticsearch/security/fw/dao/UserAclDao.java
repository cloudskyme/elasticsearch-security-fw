package org.elasticsearch.security.fw.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.security.fw.domain.Page;
import org.elasticsearch.security.fw.domain.UserAcl;

public class UserAclDao {
	Client client;
	String indices;
	public UserAclDao(Client client, String indices){
		this.client = client;
		this.indices = indices;
	}
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public UserAcl getUserAcl(String id)throws Exception{
		
//		try{
		GetRequestBuilder reqeust = client.prepareGet(indices, "useracl", id);
				
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");		
		GetResponse res = reqeust.execute().actionGet();
		
		if(res.isExists()==false){
			return null;
		}
		UserAcl acl = new UserAcl();
		acl.setId(res.getId());
		String type = (String)res.getSource().get("type");
		
		ArrayList<String> methodlist = (ArrayList<String>)res.getSource().get("method");
		String[] method = methodlist==null?null:(String[])methodlist.toArray(new String[0]);
		boolean xforwarded = (boolean)res.getSource().get("xforwarded");
		int maxBodyLength = (int)res.getSource().get("maxBodyLength");

		ArrayList<String> indicesnamelist = (ArrayList<String>)res.getSource().get("indicesname");
		String[] indicesname = indicesnamelist==null?null:(String[])indicesnamelist.toArray(new String[0]);
		ArrayList<String> actionslist = (ArrayList<String>)res.getSource().get("actions");
		String[] actions = actionslist==null?null:(String[])actionslist.toArray(new String[0]);
		ArrayList<String> hostslist = (ArrayList<String>)res.getSource().get("hosts");
		String[] hosts = hostslist==null?null:(String[])hostslist.toArray(new String[0]);
		
		String username = (String)res.getSource().get("username");
		String authkey = (String)res.getSource().get("authkey");
		String authuser = (String)res.getSource().get("authuser");
		
		try{
			String tt = (String)res.getSource().get("createtime");
			Date createtime = df.parse(tt);
			acl.setCreatetime(createtime);
		}catch(Exception ex){
		}
		acl.setType(type);
		acl.setMethod(method);
		acl.setXforwarded(xforwarded);
		acl.setMaxBodyLength(maxBodyLength);
		acl.setIndicesname(indicesname);
		acl.setActions(actions);
		acl.setHosts(hosts);
		acl.setUsername(username);
		acl.setAuthkey(authkey);
		acl.setAuthuser(authuser);
		return acl;
//		}catch(ExecutionException ee){
//		}
//		return null;
	}
	
	public void addUserAcl(UserAcl useracl)throws Exception {		
		IndexRequestBuilder irb = client.prepareIndex(indices, "useracl");
		Map<String, Object> aclMap = new HashMap<String,Object>();
		aclMap.put("type", useracl.getType());
		aclMap.put("method", useracl.getMethod());
		aclMap.put("xforwarded", useracl.isXforwarded());
		aclMap.put("maxBodyLength", useracl.getMaxBodyLength());
		aclMap.put("indicesname", useracl.getIndicesname());
		aclMap.put("actions", useracl.getActions());
		aclMap.put("hosts", useracl.getHosts());
		aclMap.put("username", useracl.getUsername());
		aclMap.put("authkey", useracl.getAuthkey());
		aclMap.put("authuser", useracl.getAuthuser());
		aclMap.put("createtime", new Date());
		irb.setSource(aclMap);
		//IndexResponse ir = irb.execute().actionGet();
		irb.execute().actionGet();
	}
	
	public void deleteUserAcl(String id) throws Exception {
		UserAcl useracl = getUserAcl(id);
		if(useracl == null) {
			throw new Exception("The user acl does not exists.");
		}
		DeleteRequestBuilder rb = client.prepareDelete(indices, "useracl", useracl.getId());
		rb.execute().actionGet();		
	}
	
	public void updateUserAcl(UserAcl useracl) throws Exception{
		Map<String, Object> aclMap = new HashMap<String,Object>();
		aclMap.put("type", useracl.getType());
		if(useracl.getMethod()==null){
			aclMap.put("method", new String[0]);
		}else{
			aclMap.put("method", useracl.getMethod());
		}
		aclMap.put("xforwarded", useracl.isXforwarded());
		aclMap.put("maxBodyLength", useracl.getMaxBodyLength());
		aclMap.put("indicesname", useracl.getIndicesname());
		if(useracl.getActions()==null){
			aclMap.put("actions", new String[0]);
		}else{
			aclMap.put("actions", useracl.getActions());
		}
		if(useracl.getHosts()==null){
			aclMap.put("hosts", new String[0]);
		}else{
			aclMap.put("hosts", useracl.getHosts());
		}
		aclMap.put("authuser", useracl.getAuthuser());
		
		UpdateRequestBuilder updaterb = client.prepareUpdate(indices, "useracl", useracl.getId());
		updaterb.setDoc(aclMap);
		updaterb.execute().actionGet();
	}
	
	public void transferUserAcl(String id, String username){
		Map<String, Object> aclMap = new HashMap<String,Object>();
		aclMap.put("username", username);
		
		UpdateRequestBuilder updaterb = client.prepareUpdate(indices, "useracl", id);
		updaterb.setDoc(aclMap);
		updaterb.execute().actionGet();
	}
	
	public Page getAclListByIndicesname(String username, String[] indicesname, int pageNum, int pageSize) throws Exception{
		int begin = 0;
        if(pageSize<1){
        	pageSize = 100;
        }
        if(pageNum < 1){
        	pageNum = 1;
        }
		begin = (pageNum - 1) * pageSize;

		SearchRequestBuilder search = client.prepareSearch(indices).setTypes("useracl");
		if (username != null && !"".equals(username)) {
			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			boolQuery.must(QueryBuilders.termQuery("username", username));
			search.setQuery(boolQuery);
		}
		SortBuilder sortBuilder = SortBuilders.fieldSort("createtime");
        sortBuilder.order(SortOrder.ASC);
        
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.termsQuery("indicesname", indicesname));
		
		search.setQuery(boolQuery);
		search.addSort(sortBuilder);
		search.setFrom(begin);
		search.setSize(pageSize);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
		List<UserAcl> list = new Vector<UserAcl>();
		Page page = new Page();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		page.setRows(list);
		try{
			ActionFuture<SearchResponse> res = client.search(search.request());
			SearchResponse r = res.get();
			long total = r.getHits().getTotalHits();
			page.setTotal(total);
			for (SearchHit hit : r.getHits()) {
				UserAcl acl = new UserAcl();
				String id = hit.getId();
				String type = (String)hit.getSource().get("type");
				
				ArrayList<String> methodlist = (ArrayList<String>)hit.getSource().get("method");
				String[] method = methodlist==null?null:(String[])methodlist.toArray(new String[0]);
				boolean xforwarded = (boolean)hit.getSource().get("xforwarded");
				int maxBodyLength = (int)hit.getSource().get("maxBodyLength");

				ArrayList<String> indicesnamelist = (ArrayList<String>)hit.getSource().get("indicesname");
				String[] resindicesname = indicesnamelist==null?null:(String[])indicesnamelist.toArray(new String[0]);
				ArrayList<String> actionslist = (ArrayList<String>)hit.getSource().get("actions");
				String[] actions = actionslist==null?null:(String[])actionslist.toArray(new String[0]);
				ArrayList<String> hostslist = (ArrayList<String>)hit.getSource().get("hosts");
				String[] hosts = hostslist==null?null:(String[])hostslist.toArray(new String[0]);
				
				String resusername = (String)hit.getSource().get("username");
				String resauthkey = (String)hit.getSource().get("authkey");
				String resauthuser = (String)hit.getSource().get("authuser");
				try{
					String tt = (String)hit.getSource().get("createtime");
					Date createtime = df.parse(tt);
					acl.setCreatetime(createtime);
				}catch(Exception ex){
				}
				acl.setId(id);
				acl.setType(type);
				acl.setMethod(method);
				acl.setXforwarded(xforwarded);
				acl.setMaxBodyLength(maxBodyLength);
				acl.setIndicesname(resindicesname);
				acl.setActions(actions);
				acl.setHosts(hosts);
				acl.setUsername(resusername);
				acl.setAuthkey(resauthkey);	
				acl.setAuthuser(resauthuser);
				list.add(acl);
			}
		}catch(ExecutionException ee){
			throw ee;
		}
		return page;		  
	}
	
	@SuppressWarnings("unchecked")
	public Page getUserAclList(String username, String authuser, String authkey, int pageNum, int pageSize)throws Exception{
		SearchRequestBuilder search = client.prepareSearch(indices).setTypes("useracl");
		SortBuilder sortBuilder = SortBuilders.fieldSort("createtime");
        sortBuilder.order(SortOrder.ASC);
        
        int begin = 0;
        if(pageSize<1){
        	pageSize = 100;
        }
        if(pageNum < 1){
        	pageNum = 1;
        }
        begin = (pageNum-1) * pageSize;
        
        if(username != null && !"".equals(username)){
			BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			boolQuery.must(QueryBuilders.termQuery("username", username));
			search.setQuery(boolQuery);
        }
        if(authuser != null && !"".equals(authuser)){
        	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			boolQuery.must(QueryBuilders.wildcardQuery("authuser", authuser));
			search.setQuery(boolQuery);
        }
        
        if(authkey != null && !"".equals(authkey)){
        	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			boolQuery.must(QueryBuilders.wildcardQuery("authkey", authkey));
			search.setQuery(boolQuery);
        }
        
		search.addSort(sortBuilder);
		search.setFrom(begin);
		search.setSize(pageSize);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
		List<UserAcl> list = new Vector<UserAcl>();
		Page page = new Page();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		page.setRows(list);
		try{
			ActionFuture<SearchResponse> res = client.search(search.request());
			SearchResponse r = res.get();
			long total = r.getHits().getTotalHits();
			page.setTotal(total);
			for (SearchHit hit : r.getHits()) {
				UserAcl acl = new UserAcl();
				String id = hit.getId();
				String type = (String)hit.getSource().get("type");
				
				ArrayList<String> methodlist = (ArrayList<String>)hit.getSource().get("method");
				String[] method = methodlist==null?null:(String[])methodlist.toArray(new String[0]);
				boolean xforwarded = (boolean)hit.getSource().get("xforwarded");
				int maxBodyLength = (int)hit.getSource().get("maxBodyLength");

				ArrayList<String> indicesnamelist = (ArrayList<String>)hit.getSource().get("indicesname");
				String[] indicesname = indicesnamelist==null?null:(String[])indicesnamelist.toArray(new String[0]);
				ArrayList<String> actionslist = (ArrayList<String>)hit.getSource().get("actions");
				String[] actions = actionslist==null?null:(String[])actionslist.toArray(new String[0]);
				ArrayList<String> hostslist = (ArrayList<String>)hit.getSource().get("hosts");
				String[] hosts = hostslist==null?null:(String[])hostslist.toArray(new String[0]);
				
				String resusername = (String)hit.getSource().get("username");
				String resauthkey = (String)hit.getSource().get("authkey");
				String resauthuser = (String)hit.getSource().get("authuser");
				try{
					String tt = (String)hit.getSource().get("createtime");
					Date createtime = df.parse(tt);
					acl.setCreatetime(createtime);
				}catch(Exception ex){
				}
				acl.setId(id);
				acl.setType(type);
				acl.setMethod(method);
				acl.setXforwarded(xforwarded);
				acl.setMaxBodyLength(maxBodyLength);
				acl.setIndicesname(indicesname);
				acl.setActions(actions);
				acl.setHosts(hosts);
				acl.setUsername(resusername);
				acl.setAuthkey(resauthkey);	
				acl.setAuthuser(resauthuser);
				list.add(acl);
			}
		}catch(ExecutionException ee){
			throw ee;
		}
		return page;
	}
}
