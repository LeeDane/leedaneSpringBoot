package com.cn.leedane.model;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.alibaba.fastjson.annotation.JSONField;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 记录时间的基类
 * @author LeeDane
 * 2015年4月3日 上午10:06:22
 * Version 1.0
 */
//@MappedSuperclass
public abstract class RecordTimeBean extends StatusBean{
	
	private static final long serialVersionUID = 813069600926983191L;

	/**
	 * 创建时间
	 */
	@Column("create_time")
	@Field
	protected Date createTime;
	
	/**
	 * 创建作者(人)
	 */
	/*@Column("create_user")
	private UserBean createUser;*/
	
	@Column("create_user_id")
	@Field
	protected int createUserId;
	
	/**
	 * 最后修改时间
	 */
	@Column("modify_time")
	protected Date modifyTime; 
	
	/**
	 * 最后修改者(人)
	 */
	/*@Column("modify_user")
	private UserBean modifyUser; */
	
	@Column("modify_user_id")
	protected int modifyUserId;
	
	//@Column(name="create_time")
	//@JSON(format = "yyyy-MM-dd HH:mm:ss")
	public Date getCreateTime() {
		return createTime;
	}
	
	@JSONField(name="create_time", format="yyyy-MM-dd HH:mm:ss")
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
		setModifyTime(createTime);
	}
	
	//@ManyToOne(targetEntity = UserBean.class)
	//@JoinColumn(name="create_user_id", referencedColumnName="id")//外键为create_user_id，与user中的id关联
	/*public UserBean getCreateUser() {
		return createUser;
	}
	public void setCreateUser(UserBean createUser) {
		this.createUser = createUser;
	}*/
	//@Column(name="modify_time")
	//@JSON(format = "yyyy-MM-dd HH:mm:ss")
	@JSONField(name="modify_time", format="yyyy-MM-dd HH:mm:ss")
	public Date getModifyTime() {
		return modifyTime;
	}
	
	@JSONField(name="modify_time", format="yyyy-MM-dd HH:mm:ss")
	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}
	
	//@ManyToOne(targetEntity = UserBean.class)
	//@JoinColumn(name="modify_user_id", referencedColumnName="id")//外键为create_user_id，与user中的id关联
	/*public UserBean getModifyUser() {
		return modifyUser;
	}
	public void setModifyUser(UserBean modifyUser) {
		this.modifyUser = modifyUser;
	}*/
	
	@JSONField(name="create_user_id")
	public int getCreateUserId() {
		return createUserId;
	}
	@JSONField(name="create_user_id")
	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
		setModifyUserId(createUserId);
	}
	
	@JSONField(name="modify_user_id")
	public int getModifyUserId() {
		return modifyUserId;
	}
	
	@JSONField(name="modify_user_id")
	public void setModifyUserId(int modifyUserId) {
		this.modifyUserId = modifyUserId;
	}
	
}
