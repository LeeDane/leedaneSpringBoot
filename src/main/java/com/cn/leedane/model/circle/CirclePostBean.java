package com.cn.leedane.model.circle;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 帖子实体bean
 * @author LeeDane
 * 2017年5月30日 下午6:57:36
 * version 1.0
 */
@Table(value = "T_CIRCLE_POST")
public class CirclePostBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1857404566150610725L;
	
	//帖子状态：正常、禁用、待审核、已删除等等
	
	/**
	 * 父圈子的id(转发、引用时候才不为0),关联自身
	 */
	@Column("pid")
	@Field
	private int pid;
	
	/**
	 * 圈子的id,外键关联CircleBean
	 */
	@Column("circle_id")
	@Field
	private int circleId;

	/**
	 * 帖子的内容
	 */
	@Column("title")
	@Field
	private String title;
	
	
	/**
	 * 帖子的内容
	 */
	@Column("content")
	@Field
	private String content;
	
	/**
	 * 为了性能，此处增加冗余字段标记是否有照片
	 */
	@Column("has_img")
	@Field
	private boolean hasImg;
	
	/**
	 * 为了性能，此处增加冗余字段存储图片(考虑到实际图片存在此处更合适)
	 */
	@Column("imgs")
	@Field
	private String imgs;
	
	/**
	 * 帖子的标签
	 */
	@Column("tag")
	@Field
	private String tag;
	
	/**
     * 是否可以评论(默认可以评论)
     */
	@Column("can_comment")
	@Field
    private boolean canComment;
    
    /**
     * 是否可以转发(默认可以转发)
     */
	@Column("can_transmit")
	@Field
    private boolean canTransmit;

	public int getCircleId() {
		return circleId;
	}

	public void setCircleId(int circleId) {
		this.circleId = circleId;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isHasImg() {
		return hasImg;
	}

	public void setHasImg(boolean hasImg) {
		this.hasImg = hasImg;
	}

	public String getImgs() {
		return imgs;
	}

	public void setImgs(String imgs) {
		this.imgs = imgs;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public boolean isCanComment() {
		return canComment;
	}

	public void setCanComment(boolean canComment) {
		this.canComment = canComment;
	}

	public boolean isCanTransmit() {
		return canTransmit;
	}

	public void setCanTransmit(boolean canTransmit) {
		this.canTransmit = canTransmit;
	}
	
}
