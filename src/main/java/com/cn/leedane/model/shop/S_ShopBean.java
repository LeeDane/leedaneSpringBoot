package com.cn.leedane.model.shop;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 商店实体bean 
 * @author LeeDane
 * 2017年11月14日 下午6:05:30
 * version 1.0
 */
@Table(value="t_shop")
public class S_ShopBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//商品的状态：0：禁用， 1：正常
	
	
	/**
	 * 是否验证
	 */
	@Field
	private boolean validation;
	
	/**
	 * 商店的描述信息
	 */
	@Field
	private String detail;
	
	/**
	 * 商店的是否是官方的
	 */
	@Column(value="is_official")
	@Field
	private boolean isOfficial;
	
	/**
	 * 商店的审核信息信息
	 */
	@Field
	@Column(value="validation_detail")
	private String validationDetail;
	
	/**
	 * 商店的名称
	 */
	@Column(value="shop_name")
	@Field
	private String name;
	
	/**
	 * 商店的链接(如果是分享的就展示分享的链接，如果不是就没有链接)
	 */
	@Field
	private String link;
	
	/**
	 * 商店的图标
	 */
	@Field
	private String img;

	public boolean isValidation() {
		return validation;
	}

	public void setValidation(boolean validation) {
		this.validation = validation;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public boolean isOfficial() {
		return isOfficial;
	}

	public void setOfficial(boolean isOfficial) {
		this.isOfficial = isOfficial;
	}

	public String getValidationDetail() {
		return validationDetail;
	}

	public void setValidationDetail(String validationDetail) {
		this.validationDetail = validationDetail;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

}
