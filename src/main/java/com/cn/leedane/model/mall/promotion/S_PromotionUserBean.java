package com.cn.leedane.model.mall.promotion;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 推广管理的用户实体bean
 * @author LeeDane
 * 2018年3月26日 下午3:29:07
 * version 1.0
 */
@Table(value="t_mall_promotion_user")
public class S_PromotionUserBean extends RecordTimeBean{
	
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
	 * mm_xxx_xxx_xxx的第三位
	 * 后台管理员在阿里妈妈平台申请后填写的
	 */
	@Column(value="adzone_id")
	@Field
	private String adzoneId;
	
	/**
	 * mm_xxx_xxx_xxx的第二位
	 * 后台管理员在阿里妈妈平台申请后填写的
	 */
	@Column(value="site_id")
	@Field
	private String siteId;

	/**
	 * 等级(不能用户修改，只提供系统判断和设定)
	 */
	@Column(value="level")
	@Field
	private int level;
	
	/**
	 * 提成比例，在按照一定的等级对所推广的订单进行提成(不能用户修改，只提供系统判断和设定)
	 */
	@Column(value="ratio")
	@Field
	private float ratio;

	public UserBean getUser() {
		return user;
	}

	public void setUser(UserBean user) {
		this.user = user;
	}

	public String getAdzoneId() {
		return adzoneId;
	}

	public void setAdzoneId(String adzoneId) {
		this.adzoneId = adzoneId;
	}

	public String getSiteId() {
		return siteId;
	}

	public void setSiteId(String siteId) {
		this.siteId = siteId;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public float getRatio() {
		return ratio;
	}

	public void setRatio(float ratio) {
		this.ratio = ratio;
	}
	
}
