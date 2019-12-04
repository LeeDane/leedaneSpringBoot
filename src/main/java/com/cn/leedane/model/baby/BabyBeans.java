package com.cn.leedane.model.baby;

import java.io.Serializable;
import java.util.List;

/**
 * 所有的宝宝实体bean
 * @author LeeDane
 * 2018年6月5日 下午4:37:29
 * version 1.0
 */
public class BabyBeans implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<BabyBean> babys;

	public List<BabyBean> getBabys() {
		return babys;
	}

	public void setBabys(List<BabyBean> babys) {
		this.babys = babys;
	}
	
}
