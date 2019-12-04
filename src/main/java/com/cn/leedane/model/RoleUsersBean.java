package com.cn.leedane.model;

import com.cn.leedane.mybatis.table.annotation.Column;
import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 角色对应用户列表
 */
public class RoleUsersBean implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private List<Map<String, Object>> users;

	public List<Map<String, Object>> getUsers() {
		return users;
	}

	public void setUsers(List<Map<String, Object>> users) {
		this.users = users;
	}
}
