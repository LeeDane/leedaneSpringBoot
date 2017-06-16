package com.cn.leedane.model.circle;

import java.io.Serializable;
import java.util.List;

/**
 * 多个圈子实体bean
 * @author LeeDane
 * 2017年6月16日 下午2:30:24
 * version 1.0
 */
public class CirclesSerializeBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<CircleBean> circleBeans;
	public List<CircleBean> getCircleBeans() {
		return circleBeans;
	}
	public void setCircleBeans(List<CircleBean> circleBeans) {
		this.circleBeans = circleBeans;
	}

	
}
