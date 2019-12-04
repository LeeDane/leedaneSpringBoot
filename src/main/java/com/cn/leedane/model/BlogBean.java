package com.cn.leedane.model;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.mybatis.table.annotation.Column;


/**
 * Blog实体类
 * @author LeeDane
 * 2016年7月12日 上午10:37:17
 * Version 1.0
 */
//@Table(name="T_BLOG")
//@Cache(usage = CacheConcurrencyStrategy.READ_WRITE, region="javaClassName")  
public class BlogBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;

	//博客的状态,-1：草稿，1：正常，0:禁用，2、删除, 3:待审核
	
	
	/**
	 * 非必须，uuid,唯一(如在图像的时候关联filepath表)
	 */
	private String uuid;
	
	/**
	 * 博客的标题
	 */
	@Field
	private String title; 
	
	/**
	 * 博客内容
	 */
	@Field
	private String content;
	
	/**
	 * 博客摘要(建议一般不要超过50个字)
	 */
	@Field
	private String digest;
	
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
	@Column("has_img")
	private boolean hasImg;
	
	/**
	 * 图片的地址
	 */
	@Field
	@Column("img_url")
	private String imgUrl;
	
	/**
	 * 原文的链接
	 */
	@Field
	@Column("origin_link")
	private String originLink;
	
	/**
	 * 来源（指的是文章的来源，其他网站的话写的是其他网站的信息，原创的话直接写原创）
	 */
	@Field
	private String source; 
	
	 /**
     * 是否可以评论(默认可以评论)
     */
	@Field
	@Column("can_comment")
    private boolean canComment;
    
    /**
     * 是否可以转发(默认可以转发)
     */
	@Field
	@Column("can_transmit")
    private boolean canTransmit;
	
	/**
	 * 该条博客是否被索引了
	 */
	@Column("is_index")
	private boolean isIndex;
	
	/**
	 * 该条博客是否被solr索引了(冗余字段)
	 */
	@Column("is_solr_index")
	private boolean isSolrIndex;
	
	/**
	 * 是否立即发布
	 */
	@Column("is_publish_now")
	private boolean isPublishNow;
	
	/**
	 * 是否推荐
	 */
	@Field
	@Column("is_recommend")
	private boolean isRecommend;

	/**
	 * 是否被添加到es索引中
	 */
	@Field
	@Column("es_index")
	private boolean esIndex;
	
	/**
	 * 分类
	 */
	@Field
	private String category;

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
	public String getTitle() {
		return title;
	}
	//@Field
	public void setTitle(String title) {
		this.title = title;
	}
	
	//@Type(type="text")
	//@Column(name="content",nullable=false)
	public String getContent() {
		return content;
	}
	
	//@Field
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getDigest() {
		return digest;
	}
	
	//@Field
	public void setDigest(String digest) {
		this.digest = digest;
	}
	public String getTag() {
		return tag;
	}
	
	//@Field
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
	
	//@Type(type="text")
	//@Column(name="img_url")
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	//@Type(type="text")
	//@Column(name="origin_link", length=800)
	public String getOriginLink() {
		return originLink;
	}
	public void setOriginLink(String originLink) {
		this.originLink = originLink;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
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
	
	//@Column(name="is_index", columnDefinition="bit(1) default 0")
	public boolean isIndex() {
		return isIndex;
	}
	public void setIndex(boolean isIndex) {
		this.isIndex = isIndex;
	}
	
	//@Column(name="is_solr_index", columnDefinition="bit(1) default 0")
	public boolean isSolrIndex() {
		return isSolrIndex;
	}
	public void setSolrIndex(boolean isSolrIndex) {
		this.isSolrIndex = isSolrIndex;
	}	
	
	//@Column(name="can_comment", nullable=false, columnDefinition="bit(1) default 1")
	public boolean isCanComment() {
		return canComment;
	}
	public void setCanComment(boolean canComment) {
		this.canComment = canComment;
	}
	
	//@Column(name="can_transmit", nullable=false, columnDefinition="bit(1) default 1")
	public boolean isCanTransmit() {
		return canTransmit;
	}
	public void setCanTransmit(boolean canTransmit) {
		this.canTransmit = canTransmit;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public boolean isRecommend() {
		return isRecommend;
	}
	public void setRecommend(boolean isRecommend) {
		this.isRecommend = isRecommend;
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
