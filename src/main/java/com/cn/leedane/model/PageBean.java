package com.cn.leedane.model;

/**
 * 抽象分页实体类
 * @author LeeDane
 * 2016年7月12日 上午10:51:18
 * Version 1.0
 */
public abstract class PageBean{

	/**
	 * 总数
	 */
	private int count = 0;
	
	/**
	 * 每页显示的记录数
	 */
	private int pageSize = 10;
	
	/**
	 * 总分页的页数
	 */
	private int pageCount = 0; 
	
	/**
	 * 当前的页数
	 */
	private int pageCurrent = 1; 
	
	/**
	 * 是否分页
	 */
	private boolean isPagging = true;
	
	public PageBean(int count, int pageSize, int pageCount, int pageCurrent,
			boolean isPagging) {
		this.count = count;
		this.pageSize = pageSize;
		this.pageCount = pageCount;
		this.pageCurrent = pageCurrent;
		this.isPagging = isPagging;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public int getPageCurrent() {
		return pageCurrent;
	}
	public void setPageCurrent(int pageCurrent) {
		this.pageCurrent = pageCurrent;
	}
	public boolean isPagging() {
		return isPagging;
	}
	public void setPagging(boolean isPagging) {
		this.isPagging = isPagging;
	}

}
