package com.cn.leedane.model;


/**
 * 保存图片的类
 * @author LeeDane
 * 2016年7月12日 上午10:51:36
 * Version 1.0
 */
//@Table(name="T_PHOTO")
public class PhotoBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;
	
	/**
	 * 父id，如商品:pid就是商品的id,用户：pid就是用户
	 */
	private int pid;
	
	/**
	 * 父code,如商品：pCode就是商品的code (pid和pCode有唯一性约束)
	 */
	private String photoCode;
	
	/**
	 * 保存图片的路径
	 */
	private String path;
	
	/**
	 * 图片描述，如：正面，背面
	 */
	private String desc;
	
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	//@Column(name="photo_code")
	public String getPhotoCode() {
		return photoCode;
	}
	public void setPhotoCode(String photoCode) {
		this.photoCode = photoCode;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	//@Column(name="photo_desc")
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
