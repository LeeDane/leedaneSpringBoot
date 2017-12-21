package com.cn.leedane.model.shop;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 商品统计的实体bean 
 * @author LeeDane
 * 2017年11月13日 下午3:04:48
 * version 1.0
 */
@Table(value="t_shop_statistics")
public class S_StatisticsBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//商品统计的状态：0：禁用， 1：正常
	/**
	 * 关联的商品id
	 */
	@Column(value="product_id")
	@Field
	private int productId;
	/**
	 * 统计的日期
	 */
	@Column(value="statistics_date")
	@Field
	private Date date;
	
	/**
	 * 统计的评论总数
	 */
	@Column(value="comment_total")
	@Field
	private int commentTotal;
	
	/**
	 * 统计的心愿单总数
	 */
	@Column(value="wish_total")
	@Field
	private int wishTotal;
	
	/**
	 * 统计的访问总数
	 */
	@Column(value="visitor_total")
	@Field
	private int visitorTotal;
	
	/**
	 * 统计的购买总数
	 */
	@Column(value="buy_total")
	@Field
	private int buyTotal;
	
	/**
	 * 统计的文本描述
	 */
	@Column(value="statistics_text")
	@Field
	private String text;
	
	public S_StatisticsBean() {
		
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public int getCommentTotal() {
		return commentTotal;
	}

	public void setCommentTotal(int commentTotal) {
		this.commentTotal = commentTotal;
	}

	public int getWishTotal() {
		return wishTotal;
	}

	public void setWishTotal(int wishTotal) {
		this.wishTotal = wishTotal;
	}

	public int getVisitorTotal() {
		return visitorTotal;
	}

	public void setVisitorTotal(int visitorTotal) {
		this.visitorTotal = visitorTotal;
	}

	public int getBuyTotal() {
		return buyTotal;
	}

	public void setBuyTotal(int buyTotal) {
		this.buyTotal = buyTotal;
	}
}
