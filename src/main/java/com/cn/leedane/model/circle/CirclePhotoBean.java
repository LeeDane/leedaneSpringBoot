package com.cn.leedane.model.circle;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 帖子照片的实体bean
 * @author LeeDane
 * 2017年5月30日 下午7:01:58
 * version 1.0
 */
public class CirclePhotoBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6836326239601463850L;
	/**
	 * 图片的路径
	 */
	private String path;
	
	/**
	 * 图片的宽度
	 */
	private int width;
	
	/**
	 * 图片的高度
	 */
	private int height;
	
	/**
	 * 图片的大小
	 */
	private long length;
	
	/**
	 * 描述信息
	 */
	@Column("photo_desc")
	@Field
	private String photoDesc;

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public String getPhotoDesc() {
		return photoDesc;
	}

	public void setPhotoDesc(String photoDesc) {
		this.photoDesc = photoDesc;
	}
	
}
