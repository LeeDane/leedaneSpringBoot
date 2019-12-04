package com.cn.leedane.model.mall;

import java.util.ArrayList;
import java.util.List;

import com.cn.leedane.model.IDBean;

/**
 * 商品首页分类集合实体
 * @author LeeDane
 * 2017年12月28日 上午10:36:34
 * version 1.0
 */
public class S_HomeItemBeans extends IDBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private List<S_HomeItemBean> homeItemBeans = new ArrayList<S_HomeItemBean>();

	public List<S_HomeItemBean> getHomeItemBeans() {
		return homeItemBeans;
	}

	public void setHomeItemBeans(List<S_HomeItemBean> homeItemBeans) {
		this.homeItemBeans = homeItemBeans;
	}
	
}
