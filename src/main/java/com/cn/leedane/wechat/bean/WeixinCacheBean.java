package com.cn.leedane.wechat.bean;

/**
 * 微信用户缓存的实体类
 * @author LeeDane
 * 2015年6月30日 下午8:05:03
 * Version 1.0
 */
public class WeixinCacheBean{
	
	private String currentType; //当前的模式类型
	private int lastBlogId; //获取的博客集合中最后一条博客的ID
	private boolean bindLogin;//是否绑定登录
	
	public boolean isBindLogin() {
		return bindLogin;
	}

	public void setBindLogin(boolean bindLogin) {
		this.bindLogin = bindLogin;
	}

	public String getCurrentType() {
		return currentType;
	}

	public void setCurrentType(String currentType) {
		this.currentType = currentType;
	}

	public int getLastBlogId() {
		return lastBlogId;
	}

	public void setLastBlogId(int lastBlogId) {
		this.lastBlogId = lastBlogId;
	}

	
}
