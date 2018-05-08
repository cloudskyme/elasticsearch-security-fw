package org.elasticsearch.security.fw.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
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
import org.elasticsearch.security.fw.common.util.MD5Util;
import org.elasticsearch.security.fw.domain.Page;
import org.elasticsearch.security.fw.domain.User;

public class UserDao {
	Client client;
	String indices;
	public UserDao(Client client, String indices){
		this.client = client;
		this.indices = indices;
	}
	
	public User getUser(String username)throws Exception{
//		if("elastic".equals(username)){
//			//判断用户是否为空
//		}
		SearchRequestBuilder search = client.prepareSearch(indices).setTypes("user");
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.termQuery("username", username));
		search.setQuery(boolQuery);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");		
		try{
			ActionFuture<SearchResponse> res = client.search(search.request());
			SearchResponse r = res.get();
			for (SearchHit hit : r.getHits()) {
				User user = new User();
				user.setId(hit.getId());
				user.setUsername((String)hit.getSource().get("username"));
				try{
					String tt = (String)hit.getSource().get("createtime");
					Date createtime = df.parse(tt);
					user.setCreatetime(createtime);
				}catch(Exception ex){					
				}
				user.setSecretkey((String)hit.getSource().get("secretkey"));
				return user;
			}
		}catch(ExecutionException ee){
			throw ee;
		}
		return null;
	}
	
	public void addUser(User user)throws Exception{
		if(getUser(user.getUsername()) != null){
			throw new Exception("user already  exists.");
		}
				
		IndexRequestBuilder irb = client.prepareIndex(indices, "user");
		Map<String, Object> userMap = new HashMap<String,Object>();
		userMap.put("username", user.getUsername());
		userMap.put("secretkey", MD5Util.MD5(user.getSecretkey()));
		userMap.put("createtime", new Date());
		userMap.put("metadata", user.getMetadata());
		irb.setSource(userMap);
		//IndexResponse ir = irb.execute().actionGet();
		irb.execute().actionGet();
		client.admin().indices().prepareFlush(indices).execute().actionGet();
	}
	
	public void updateUser(User user)throws Exception{
		User oldUser = getUser(user.getUsername());
		if(oldUser == null){
			throw new Exception("The user does not exists.");
		}
		UpdateRequestBuilder updaterb = client.prepareUpdate(indices, "user", oldUser.getId());
		Map<String,Object> userMap = new HashMap<String,Object>();
		userMap.put("username", user.getUsername());
		userMap.put("secretkey", MD5Util.MD5(user.getSecretkey()));
		//userMap.put("createtime", oldUser.getCreatetime());
		updaterb.setDoc(userMap);
		updaterb.execute().actionGet();
	}
	
	public void deleteUser(String username) throws Exception{
		User oldUser = getUser(username);
		if(oldUser == null){
			throw new Exception("The user does not exists.");
		}
		DeleteRequestBuilder rb = client.prepareDelete(indices, "user", oldUser.getId());
		rb.execute().actionGet();		
	}
	
	public Page getUserList(String username, int pageNum, int pageSize)throws Exception{
		SearchRequestBuilder search = client.prepareSearch(indices).setTypes("user");
		SortBuilder sortBuilder = SortBuilders.fieldSort("username");
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
			boolQuery.must(QueryBuilders.wildcardQuery("username", "*"+username+"*"));
			search.setQuery(boolQuery);
        }
		search.addSort(sortBuilder);
		search.setFrom(begin);
		search.setSize(pageSize);
		//search.putHeader(key, value);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
		List<User> list = new Vector<User>();
		Page page = new Page();
		page.setPageNum(pageNum);
		page.setPageSize(pageSize);
		page.setRows(list);
//		try{
			ActionFuture<SearchResponse> res = client.search(search.request());
			SearchResponse r = res.actionGet();
			long total = r.getHits().getTotalHits();
			page.setTotal(total);
			for (SearchHit hit : r.getHits()) {
				User user = new User();
				user.setId(hit.getId());
				user.setUsername((String)hit.getSource().get("username"));
				try{
					String tt = (String)hit.getSource().get("createtime");
					Date createtime = df.parse(tt);
					user.setCreatetime(createtime);
				}catch(Exception ex){					
				}
				user.setSecretkey((String)hit.getSource().get("secretkey"));
				list.add(user);
			}
//		}catch(ExecutionException ee){
//		}
		return page;
	}
}
