package org.elasticsearch.security.fw.domain;

import java.util.Date;

public class UserAcl {
	
	String id;
	String type;
	String[] method;
	boolean xforwarded;
	int maxBodyLength;
	String[] indicesname;
	String[] actions;
	String[] hosts;
	String username;
	Date createtime;
	String authkey;
	String authuser;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String[] getMethod() {
		return method;
	}
	public void setMethod(String[] method) {
		this.method = method;
	}
	public boolean isXforwarded() {
		return xforwarded;
	}
	public void setXforwarded(boolean xforwarded) {
		this.xforwarded = xforwarded;
	}
	public int getMaxBodyLength() {
		return maxBodyLength;
	}
	public void setMaxBodyLength(int maxBodyLength) {
		this.maxBodyLength = maxBodyLength;
	}
	public String[] getIndicesname() {
		return indicesname;
	}
	public void setIndicesname(String[] indicesname) {
		this.indicesname = indicesname;
	}
	public String[] getActions() {
		return actions;
	}
	public void setActions(String[] actions) {
		this.actions = actions;
	}
	public String[] getHosts() {
		return hosts;
	}
	public void setHosts(String[] hosts) {
		this.hosts = hosts;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getAuthkey() {
		return authkey;
	}
	public void setAuthkey(String authkey) {
		this.authkey = authkey;
	}
	public String getAuthuser() {
		return authuser;
	}
	public void setAuthuser(String authuser) {
		this.authuser = authuser;
	}
	
}
