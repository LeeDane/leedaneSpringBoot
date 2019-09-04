package com.cn.leedane.model;

import com.cn.leedane.mybatis.table.annotation.Column;
import org.apache.solr.client.solrj.beans.Field;


/**
 * 心情实体类
 * @author LeeDane
 * 2016年7月12日 上午10:37:24
 * Version 1.0
 */
//@Table(name="T_MOOD")
public class MoodBean extends RecordTimeBean{
		
	private static final long serialVersionUID = -6784369167574852718L;

	//心情的状态,-1：草稿，1：正常，0:禁用，2、删除, 5、私有的
	
	/**
	 * 非必须，uuid,唯一(如在图像的时候一定必须，因为它是关联filepath表的唯一凭证)
	 */
	@Field
	private String uuid;
	
	/**
	 * 心情内容
	 */
	@Field
	private String content;
	
	/**
	 * 标签(多个用逗号隔开)
	 */
	@Field
	private String tag;   
	
	/**
	 * 来自(指的是来自发表的方式，如：Android客户端，iPhone客户端等)
	 */
	@Field
	private String froms;
	
	/**
	 * 是否有图片
	 */
	@Field
	private boolean hasImg;
	
	/**
	 * 位置的展示信息
	 */
	@Field
	private String location;
	
	/**
	 * 经度
	 */
	@Field
    private double longitude;
    
    /**
     * 纬度
     */
	@Field
    private double latitude;
    
    /**
     * 是否可以评论(默认可以评论)
     */
	@Field
    private boolean canComment;
    
    /**
     * 是否可以转发(默认可以转发)
     */
	@Field
    private boolean canTransmit;
	
	/**
	 * 是否立即发布
	 */
	private boolean isPublishNow;
	
	/**
	 * 是否被solr索引(冗余字段)
	 */
	private boolean isSolrIndex;

	/**
	 * 是否被添加到es索引中
	 */
	@Field
	@Column("es_index")
	private boolean esIndex;

	/**
	 * 排序字段，根据从大到小排序，大于0表示置顶字段
	 */
	private int stick;
	
	/**
	 * 扩展字段1
	 */
	private String str1;
	
	/**
	 * 扩展字段2
	 */
	private String str2;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	//@Type(type="text")
	//@Column(name="content",nullable=false)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
	//@Column(name="has_img", columnDefinition="bit(1) default 0")
	public boolean isHasImg() {
		return hasImg;
	}
	public void setHasImg(boolean hasImg) {
		this.hasImg = hasImg;
	}
	
	public String getFroms() {
		return froms;
	}
	public void setFroms(String froms) {
		this.froms = froms;
	}
	
	//@Column(name="is_publish_now", columnDefinition="bit(1) default 0")
	public boolean isPublishNow() {
		return isPublishNow;
	}
	public void setPublishNow(boolean isPublishNow) {
		this.isPublishNow = isPublishNow;
	}
	
	//@Column(name="is_solr_index", columnDefinition="bit(1) default 0")
	public boolean isSolrIndex() {
		return isSolrIndex;
	}
	public void setSolrIndex(boolean isSolrIndex) {
		this.isSolrIndex = isSolrIndex;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public double getLatitude() {
		return latitude;
	}
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	//@Column(name="can_comment", columnDefinition="bit(1) default 1")
	public boolean isCanComment() {
		return canComment;
	}
	public void setCanComment(boolean canComment) {
		this.canComment = canComment;
	}
	
	//@Column(name="can_transmit", columnDefinition="bit(1) default 1")
	public boolean isCanTransmit() {
		return canTransmit;
	}
	public void setCanTransmit(boolean canTransmit) {
		this.canTransmit = canTransmit;
	}

	public boolean isEsIndex() {
		return esIndex;
	}

	public void setEsIndex(boolean esIndex) {
		this.esIndex = esIndex;
	}

	public int getStick() {
		return stick;
	}

	public void setStick(int stick) {
		this.stick = stick;
	}
}
