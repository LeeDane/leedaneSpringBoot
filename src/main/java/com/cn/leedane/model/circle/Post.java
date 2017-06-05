package com.cn.leedane.model.circle;

import com.cn.leedane.model.RecordTimeBean;

/**
 * 帖子实体bean
 * @author LeeDane
 * 2017年5月30日 下午6:57:36
 * version 1.0
 */
public class Post extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1857404566150610725L;
	
	//帖子状态：正常、禁用、待审核、已删除等等
	
	/**
	 * 圈子的id,外键关联CircleBean
	 */
	private int circleId;

	/**
	 * 帖子的内容
	 */
	private String content;
	
	/**
	 * 为了性能，此处增加冗余字段标记是否有照片，为true将去PhotoBean查找相关的照片
	 */
	private boolean hasPhoto;

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

	public boolean isHasPhoto() {
		return hasPhoto;
	}

	public void setHasPhoto(boolean hasPhoto) {
		this.hasPhoto = hasPhoto;
	}
}
