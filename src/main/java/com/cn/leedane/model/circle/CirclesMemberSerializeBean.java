package com.cn.leedane.model.circle;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 多个圈子成员实体bean
 * @author LeeDane
 * 2017年6月20日 上午10:52:44
 * version 1.0
 */
public class CirclesMemberSerializeBean implements Serializable{

	private static final long serialVersionUID = 1L;
	/**
	 * 成员列表
	 */
	private List<Map<String, Object>> circleMemberBeans;
	public List<Map<String, Object>> getCircleMemberBeans() {
		return circleMemberBeans;
	}
	public void setCircleMemberBeans(List<Map<String, Object>> circleMemberBeans) {
		this.circleMemberBeans = circleMemberBeans;
	}
	
	
}
