package com.cn.leedane.model.mall;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页展示的商店实体bean
 * @author LeeDane
 * 2018年1月11日 下午2:48:01
 * version 1.0
 */
public class S_HomeShopBeans implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 总数
	 */
	private int total;
	
	/**
	 * 需要展示的轮播列表列表
	 */
	private List<S_HomeShopBean> homeShopBeans = new ArrayList<S_HomeShopBean>();

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<S_HomeShopBean> getHomeShopBeans() {
		return homeShopBeans;
	}

	public void setHomeShopBeans(List<S_HomeShopBean> homeShopBeans) {
		this.homeShopBeans = homeShopBeans;
	}
}
