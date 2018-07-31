package com.cn.leedane.stock;

/**
 * 运算结果
 * @author LeeDane
 * 2018年7月11日 上午9:59:27
 * version 1.0
 */
/**
 * @author LeeDane
 * 2018年7月11日 上午10:15:03
 * version 1.0
 */
public class ComputeResult {

	public ComputeResult(int type, float result, float sourceResult) {
		super();
		this.type = type;
		this.result = result;
		this.sourceResult = sourceResult;
	}

	/**
	 * 算法的类型
	 * 0：加法， 1减法： 2乘法 3、除法
	 */
	private int type;
	
	/**
	 * 返回的计算结果
	 */
	private float result;
	
	/**
	 * 原始的结果，弥补在只比较最后一位的时候result的不足
	 */
	private float sourceResult;
	
	//private

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public float getResult() {
		return result;
	}

	public void setResult(float result) {
		this.result = result;
	}

	public float getSourceResult() {
		return sourceResult;
	}

	public void setSourceResult(float sourceResult) {
		this.sourceResult = sourceResult;
	}
	
	
}
