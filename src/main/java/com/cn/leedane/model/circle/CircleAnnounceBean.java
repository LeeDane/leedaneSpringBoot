package com.cn.leedane.model.circle;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 公告实体bean
 * @author LeeDane
 * 2017年6月24日 下午9:04:18
 * version 1.0
 */
public class CircleAnnounceBean extends RecordTimeBean{

	//只有管理员和圈主才能使用
	/**
	 * 
	 */
	private static final long serialVersionUID = 3896570329003234212L;

	/**
	 * 公告的标题
	 */
	@Column("title")
	@Field
	private String title;
	
	/**
	 * 公告的内容
	 */
	@Column("content")
	@Field
	private String content;
	
	/**
	 * 公告摘要
	 */
	@Column("digest")
	@Field
	private String digest;
	
	/**
	 * 公告主图片
	 */
	@Column("path")
	@Field
	private String path;
	
	/**
	 * 公告开始展示的时间
	 */
	@Column("start_time")
	@Field
	private Date startTime;
	
	/**
	 * 公告结束展示的时间
	 */
	@Column("end_time")
	@Field
	private Date endTime;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
