package com.cn.leedane.stock;

import java.util.List;

/**
 * 七星彩
 * @author LeeDane
 * 2018年7月3日 上午10:00:19
 * version 1.0
 */
public class QXStock {
	public QXStock(int nper, List<Integer> numbers) {
		super();
		this.nper = nper;
		this.numbers = numbers;
	}

	/**
	 * 期数，不设置默认是列表的后面一个加1
	 */
	private int nper;
	
	/**
	 * 号码排列数，按照顺序排列，目前已知是7位数
	 */
	private List<Integer> numbers;

	public int getNper() {
		return nper;
	}

	public void setNper(int nper) {
		this.nper = nper;
	}

	public List<Integer> getNumbers() {
		return numbers;
	}

	public void setNumbers(List<Integer> numbers) {
		this.numbers = numbers;
	}
}
