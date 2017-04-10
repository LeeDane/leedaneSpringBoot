package com.cn.leedane.model;

/**
 * 权限实体bean
 * @author LeeDane
 * 2017年3月23日 下午12:54:50
 * version 1.0
 */
public class PermissionBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;

	private String name;

    private int order;//排序
    
    private String desc; //描述信息
    
    private String code;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
}
