package com.cn.leedane.model.mall.promotion;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 推广管理的用户提交审核实体bean
 * @author LeeDane
 * 2018年3月26日 下午3:44:37
 * version 1.0
 */
@Table(value="t_mall_promotion_audit")
public class S_PromotionAuditBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 用户，用于展示
	 */
	@Column(required = false)
	private UserBean user;
	
	/**
	 * 联系电话
	 */
	@Column
	@Field
	private String phone;
	
	/**
	 * 联系email
	 */
	@Column
	@Field
	private String email;
	
	/**
	 * 申请原因
	 */
	@Column
	@Field
	private String reason;
	
	/**
	 * 申请图片的链接(多个用,分隔开来)
	 */
	@Column(value = "img_links")
	@Field
	private String imgLinks;

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getImgLinks() {
		return imgLinks;
	}

	public void setImgLinks(String imgLinks) {
		this.imgLinks = imgLinks;
	}
	
	
	
}
