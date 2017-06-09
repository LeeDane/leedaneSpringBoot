package com.cn.leedane.model.circle;

import com.cn.leedane.model.RecordTimeBean;

/**
 * 圈子实体bean
 * @author LeeDane
 * 2017年5月30日 下午4:54:35
 * version 1.0
 */
public class CircleBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7320151621589479551L;
	
	//圈子的状态：0：禁用， 1：正常
	
	/**
	 * 圈子的名称
	 */
	private String name;
	
	/**
	 * 圈子的描述信息(介绍信息)
	 */
	private String describe;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
}
