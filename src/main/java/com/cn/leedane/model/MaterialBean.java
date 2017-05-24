package com.cn.leedane.model;

/**
 * 素材实体bean
 * @author LeeDane
 * 2017年5月22日 上午10:05:20
 * version 1.0
 */
public class MaterialBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 9107789817208431543L;
	/**
	 * 本地路径
	 */
	private String path;
	
	/**
	 * 七牛云路径
	 */
	private String qiniuPath;
	
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
	private String materialDesc;
	
	/**
	 * 素材类型。 1是文件, 2是图像
	 */
	private int type;

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

	public String getMaterialDesc() {
		return materialDesc;
	}

	public void setMaterialDesc(String materialDesc) {
		this.materialDesc = materialDesc;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getQiniuPath() {
		return qiniuPath;
	}

	public void setQiniuPath(String qiniuPath) {
		this.qiniuPath = qiniuPath;
	}
	
	
}
