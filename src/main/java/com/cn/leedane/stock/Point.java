package com.cn.leedane.stock;

/**
 * 每一个点数
 * @author LeeDane
 * 2018年7月9日 下午4:21:51
 * version 1.0
 */
public class Point {

	public Point(int nper, int index, int value) {
		super();
		this.nper = nper;
		this.index = index;
		this.value = value;
	}

	/**
	 * 期数
	 */
	private int nper;
	
	/**
	 * 该期中的索引，即位数
	 */
	private int index;
	
	/**
	 * 值
	 */
	private int value;

	public int getNper() {
		return nper;
	}

	public void setNper(int nper) {
		this.nper = nper;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return "Point [nper=" + nper + ", index=" + index + ", value=" + value
				+ "]";
	}
	
	
}
