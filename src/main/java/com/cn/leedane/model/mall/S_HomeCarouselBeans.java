package com.cn.leedane.model.mall;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 首页轮播商品实体bean 
 * @author LeeDane
 * 2017年12月26日 下午3:00:38
 * version 1.0
 */
public class S_HomeCarouselBeans implements Serializable{
	
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
	private List<S_HomeCarouselBean> homeCarouselBeans = new ArrayList<S_HomeCarouselBean>();

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<S_HomeCarouselBean> getHomeCarouselBeans() {
		return homeCarouselBeans;
	}

	public void setHomeCarouselBeans(List<S_HomeCarouselBean> homeCarouselBeans) {
		this.homeCarouselBeans = homeCarouselBeans;
	}

}
