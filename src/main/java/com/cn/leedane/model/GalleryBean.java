package com.cn.leedane.model;


/**
 * 图库实体类
 * @author LeeDane
 * 2016年7月12日 上午10:45:35
 * Version 1.0
 */
//@Table(name="T_GALLERY")
public class GalleryBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	
	//图库的状态,1：正常，0:禁用，2、删除
	
	/**
	 * 路径
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

	private String galleryDesc;

	//@Column(length=255, nullable=false)
	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	//@Column(name="gallery_desc", nullable=true)
	public String getGalleryDesc() {
		return galleryDesc;
	}

	public void setGalleryDesc(String galleryDesc) {
		this.galleryDesc = galleryDesc;
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
	
}
