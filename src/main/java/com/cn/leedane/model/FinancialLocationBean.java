package com.cn.leedane.model;

import com.alibaba.fastjson.annotation.JSONField;


/**
 * 记账常用地址实体类
 * @author LeeDane
 * 2016年11月22日 上午10:19:42
 * Version 1.0
 */
//@Table(name="T_FINANCIAL_LOCATION")
public class FinancialLocationBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//地址的状态,1：正常，0:禁用
	
	/**
	 * 位置信息
	 */
	private String location;
	
	/**
	 * 位置表示信息
	 */
	@JSONField(name="location_desc")
	private String locationDesc;

	//@Type(type="text")
	//@Column(name="location", nullable=false)
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocationDesc() {
		return locationDesc;
	}

	public void setLocationDesc(String locationDesc) {
		this.locationDesc = locationDesc;
	}
	
}
