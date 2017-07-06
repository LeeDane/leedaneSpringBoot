package com.cn.leedane.model.circle;

import com.cn.leedane.model.IDBean;

/**
 * 帖子与用户关系实体bean（主要使用在redis中保存用户相关帖子的列表(序列化)）
 * @author LeeDane
 * 2017年6月24日 下午10:26:37
 * version 1.0
 */
public class CircleUserPostBean extends IDBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 父圈子的id(转发、引用时候才不为0),关联自身
	 */
	private int pid;
	
	/**
	 * 圈子的id,外键关联CircleBean
	 */
	private int circleId;

	/**
	 * 帖子的内容
	 */
	private String title;
	
	/**
	 * 帖子的内容
	 */
	private String content;
	
	/**
	 * 帖子的摘要
	 */
	private String digest;
	
	/**
	 * 为了性能，此处增加冗余字段标记是否有照片
	 */
	private boolean hasImg;
	
	/**
	 * 为了性能，此处增加冗余字段存储图片(考虑到实际图片存在此处更合适)
	 */
	private String imgs;
	
	/**
	 * 帖子的标签
	 */
	private String tag;
	
	/**
	 * 创建时间
	 */
	private String createTime;

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getCircleId() {
		return circleId;
	}

	public void setCircleId(int circleId) {
		this.circleId = circleId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
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

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}
}
