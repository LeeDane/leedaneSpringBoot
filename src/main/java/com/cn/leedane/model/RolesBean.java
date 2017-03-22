package com.cn.leedane.model;


/**
 * 角色实体类
 * @author LeeDane
 * 2016年7月12日 上午10:52:50
 * Version 1.0
 */
//@Table(name="T_ROLE")
public class RolesBean extends StatusBean{

	private static final long serialVersionUID = 1L;	
	//状态： 0：禁止，1：正常，2：过期
	
	/**
	 * 角色名称(同一级别下是唯一的)
	 */
	private String name;
	
	/**
	 * 上级角色的id(为空或者为0表示是父角色)
	 */
	private int pid;
	
	/**
	 * 角色编码(唯一)
	 */
	private String code;
	
	//private Set<UserBean> users = new LinkedHashSet<UserBean>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	

	// cascade表示级联。CascadeType.REFRESH级联刷新 
	// optional表示该对象可有可无，它的值为true表示该外键可以为null，它的值为false表示该外键为not null 
	/*@ManyToMany(cascade = CascadeType.ALL, mappedBy="roles") 
	 // JoinColumn表示外键的列 
	//@Column(name="user_id")
	public Set<UserBean> getUsers() {
		return users;
	}

	public void setUsers(Set<UserBean> users) {
		this.users = users;
	}*/
	
}
