package com.cn.leedane.model.circle;

import java.util.List;

import com.cn.leedane.model.IDBean;

/**
 * 帖子与用户关系实体bean（主要使用在redis中保存用户相关帖子的列表(序列化)）
 * @author LeeDane
 * 2017年6月24日 下午10:26:37
 * version 1.0
 */
public class CircleUserPostsBean extends IDBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 总数(因为下面的列表只展示用户最新的几条，所以总数在这里展示)
	 */
	private int total;
	
	/**
	 * 需要展示的帖子列表
	 */
	private List<CircleUserPostBean> posts;

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<CircleUserPostBean> getPosts() {
		return posts;
	}

	public void setPosts(List<CircleUserPostBean> posts) {
		this.posts = posts;
	}
}
