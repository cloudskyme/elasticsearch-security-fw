package org.elasticsearch.security.fw.dao;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.stats.IndicesStatsResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.security.fw.domain.Page;
import org.elasticsearch.security.fw.domain.UserIndices;

public class UserIndicesDao {
	Client client;
	String indices;
	public UserIndicesDao(Client client, String indices){
		this.client = client;
		this.indices = indices;
	}
	
	public UserIndices getUserIndices(String indicesname)throws Exception{
		SearchRequestBuilder search = client.prepareSearch(indices).setTypes("userindices");
		BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
		boolQuery.must(QueryBuilders.termQuery("indicesname", indicesname));
		search.setQuery(boolQuery);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");		
		try{
			ActionFuture<SearchResponse> res = client.search(search.request());
			SearchResponse r = res.get();
			for (SearchHit hit : r.getHits()) {
				UserIndices userIndices = new UserIndices();
				userIndices.setId(hit.getId());
				userIndices.setUsername((String)hit.getSource().get("username"));
				userIndices.setIndicesname((String)hit.getSource().get("indicesname"));
				userIndices.setDescribe((String)hit.getSource().get("describe"));
				try{
					String tt = (String)hit.getSource().get("createtime");
					Date createtime = df.parse(tt);
					userIndices.setCreatetime(createtime);
				}catch(Exception ex){				
				}
				userIndices.setMaxsize((int)hit.getSource().get("maxsize"));
				
				return userIndices;
			}
		}catch(ExecutionException ee){
		}
		return null;
	}
	
	public void addIndices(UserIndices userIndices, boolean addIndices)throws Exception{
		if(getUserIndices(userIndices.getIndicesname()) != null){
			throw new Exception("The indices name already  exists.");
		}
		
		//add ES
		if(addIndices){
			createUserIndicesFromES(userIndices.getIndicesname());
		}
		
		IndexRequestBuilder irb = client.prepareIndex(indices, "userindices");
		Map<String, Object> userMap = new HashMap<String,Object>();
		userMap.put("username", userIndices.getUsername());
		userMap.put("indicesname", userIndices.getIndicesname());
		userMap.put("maxsize", userIndices.getMaxsize());
		userMap.put("createtime", new Date());
		userMap.put("describe", userIndices.getDescribe());
		irb.setSource(userMap);
		irb.execute().actionGet();		
	}
	
	public void deleteUserIndices(String indicesname) throws Exception{
		UserIndices oldUserIndices = getUserIndices(indicesname);
		if(oldUserIndices == null){
			throw new Exception("The indices does not exists.");
		}
		
		try {
			deleteUserIndicesFromES(indicesname);
		} catch(IndexNotFoundException ex){
			
		}
		
		DeleteRequestBuilder rb = client.prepareDelete(indices, "userindices", oldUserIndices.getId());
		rb.execute().actionGet();

		
	}
	
	@SuppressWarnings("rawtypes")
	public Page getUserIndicesList(String username, String indicesname, int pageNum, int pageSize)throws Exception{
		SearchRequestBuilder search = client.prepareSearch(indices).setTypes("userindices");
		SortBuilder sortBuilder = SortBuilders.fieldSort("indicesname");
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
        if(indicesname != null && !"".equals(indicesname)){
        	BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
			boolQuery.must(QueryBuilders.wildcardQuery("indicesname", "*"+indicesname+"*"));
			search.setQuery(boolQuery);
        }
        
		search.addSort(sortBuilder);
		search.setFrom(begin);
		search.setSize(pageSize);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S'Z'");
		List<UserIndices> list = new Vector<UserIndices>();
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
				UserIndices userIndices = new UserIndices();
				userIndices.setId(hit.getId());
				userIndices.setUsername((String)hit.getSource().get("username"));
				userIndices.setIndicesname((String)hit.getSource().get("indicesname"));
				userIndices.setDescribe((String)hit.getSource().get("describe"));
				try{
					String tt = (String)hit.getSource().get("createtime");
					Date createtime = df.parse(tt);
					userIndices.setCreatetime(createtime);
				}catch(Exception ex){
				}
				userIndices.setMaxsize((int)hit.getSource().get("maxsize"));
				list.add(userIndices);
			}
		}catch(ExecutionException ee){
			throw ee;
		}
		return page;
	}
	
	public void updateUserIndices(UserIndices userIndices) throws Exception{
		UserIndices oldUserIndices = getUserIndices(userIndices.getIndicesname());
		if(oldUserIndices == null){
			throw new Exception("The indices does not exists.");
		}
		
		UpdateRequestBuilder updaterb = client.prepareUpdate(indices, "userindices", oldUserIndices.getId());
		Map<String,Object> userMap = new HashMap<String,Object>();
		userMap.put("describe", userIndices.getDescribe());
		userMap.put("maxsize", userIndices.getMaxsize());
		updaterb.setDoc(userMap);
		updaterb.execute().actionGet();
	}
	
	public void transferUserIndices(String id, String username){
		UpdateRequestBuilder updaterb = client.prepareUpdate(indices, "userindices", id);
		Map<String,Object> userMap = new HashMap<String,Object>();
		userMap.put("username", username);
		updaterb.setDoc(userMap);
		updaterb.execute().actionGet();
	}
	
	public boolean existsUserIndices(String indicesname){
		return client.admin().indices().prepareExists(indicesname).execute().actionGet(10).isExists();
	}
	
	public Map<String,Object> healthUserIndices(String indicesname){
		ClusterHealthResponse res =  client.admin().cluster().prepareHealth(indicesname).execute().actionGet(10);
		String status = res.getStatus().toString();
		boolean timed_out = res.isTimedOut();
		int number_of_data_nodes = res.getNumberOfDataNodes();
		int active_primary_shards = res.getActivePrimaryShards();
		int active_shards = res.getActiveShards();
		double active_shards_percent_as_number = res.getActiveShardsPercent();
		int initializing_shards = res.getInitializingShards();
		int unassigned_shards = res.getUnassignedShards();
		int relocating_shards = res.getRelocatingShards();
		int delayed_unassigned_shards = res.getDelayedUnassignedShards();
		int number_of_pending_tasks = res.getNumberOfPendingTasks();
		int number_of_in_flight_fetch = res.getNumberOfInFlightFetch();
		String task_max_waiting_in_queue_millis = res.getTaskMaxWaitingTime().toString();
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", status);
		map.put("number_of_data_nodes", number_of_data_nodes);
		map.put("active_primary_shards", active_primary_shards);
		map.put("active_shards", active_shards);
		map.put("active_shards_percent_as_number", active_shards_percent_as_number);
		map.put("relocating_shards", relocating_shards);
		map.put("initializing_shards", initializing_shards);
		map.put("unassigned_shards", unassigned_shards);
		map.put("delayed_unassigned_shards", delayed_unassigned_shards);
		map.put("number_of_pending_tasks", number_of_pending_tasks);
		map.put("number_of_in_flight_fetch", number_of_in_flight_fetch);
		map.put("task_max_waiting_in_queue_millis", task_max_waiting_in_queue_millis);
		map.put("timed_out", timed_out);
		
		return map;
	}
	
	public Map<String, Object> statsUserIndices(String indicesname){
		IndicesStatsResponse res = client.admin().indices().prepareStats(indicesname).execute().actionGet();
		long primaries_docs_count = res.getPrimaries().getDocs().getCount();
		long primaries_docs_delete = res.getPrimaries().getDocs().getDeleted();		
		long primaries_store_size_in_bytes = res.getPrimaries().getStore().getSizeInBytes();
		
		long total_docs_count = res.getTotal().getDocs().getCount();
		long total_docs_delete = res.getTotal().getDocs().getDeleted();		
		long total_store_size_in_bytes = res.getTotal().getStore().getSizeInBytes();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("primaries_docs_count", primaries_docs_count);
		map.put("primaries_docs_delete", primaries_docs_delete);
		map.put("primaries_store_size_in_bytes", primaries_store_size_in_bytes);
		map.put("total_docs_count", total_docs_count);
		map.put("total_docs_delete", total_docs_delete);
		map.put("total_store_size_in_bytes", total_store_size_in_bytes);
		
		return map;
	}
	
	public Map<String, Object> settingsUserIndices(String indicesname){
		Map<String, Object> map = new HashMap<String, Object>();
		Settings st = client.admin().indices().prepareGetSettings(indicesname).execute().actionGet().getIndexToSettings().get(indicesname).getAsSettings("index");
		String creation_date = st.get("creation_date");
		String number_of_shards = st.get("number_of_shards");
		String number_of_replicas = st.get("number_of_replicas");
		String uuid = st.get("uuid");
		String version_created = st.get("version.created");
		
		map.put("creation_date", creation_date);
		map.put("number_of_shards", number_of_shards);
		map.put("number_of_replicas", number_of_replicas);
		map.put("uuid", uuid);
		map.put("version_created", version_created);
		
		return map;
	}
	
	public void createUserIndicesFromES(String indicesname){
		//IndexAlreadyExistsException
		client.admin().indices().prepareCreate(indicesname).execute().actionGet();
	}
	
	public void deleteUserIndicesFromES(String indicesname){
		//IndexNotFoundException
		client.admin().indices().prepareDelete(indicesname).execute().actionGet();
	}
}
