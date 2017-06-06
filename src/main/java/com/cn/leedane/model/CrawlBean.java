package com.cn.leedane.model;

import java.util.Date;

import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 爬虫的实体bean
 * @author LeeDane
 * 2016年7月12日 上午10:42:52
 * Version 1.0
 */
//@Table(name="T_CRAWL")
public class CrawlBean extends RecordTimeBean{

	/**
	 * create time 2015年6月24日 上午9:38:50
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 链接地址
	 */
	private String url;
	
	/**
	 * 是否已经爬取
	 */
	@Column("is_crawl")
	private boolean isCrawl; 
	
	/*以下是对抓取数据的保存字段*/
	
	/**
	 * 来源
	 */
	private String source; 
	
	/**
	 * 评分
	 */
	private int score;
	
	/**
	 * 是否是具体的链接（具体到文章的链接）
	 */
	@Column("is_link")
	private boolean isLink;
	
	
	//@Type(type="text")
	//@Column(name="url",nullable=false, length=800)
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	//@Column(name="is_crawl")
	public boolean isCrawl() {
		return isCrawl;
	}
	public void setCrawl(boolean isCrawl) {
		this.isCrawl = isCrawl;
	}

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	//@Column(name="is_link")
	public boolean isLink() {
		return isLink;
	}
	public void setLink(boolean isLink) {
		this.isLink = isLink;
	}
	
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

}
