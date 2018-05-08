package org.elasticsearch.security.fw.domain;

import java.util.Date;

public class UserIndices {
	String id;
	String indicesname;
	String username;
	long maxsize;
	Date createtime;
	String describe;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIndicesname() {
		return indicesname;
	}

	public void setIndicesname(String indicesname) {
		this.indicesname = indicesname;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getMaxsize() {
		return maxsize;
	}

	public void setMaxsize(long maxsize) {
		this.maxsize = maxsize;
	}

	public Date getCreatetime() {
		return createtime;
	}

	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
	
}
