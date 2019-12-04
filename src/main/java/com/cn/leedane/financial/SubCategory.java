package com.cn.leedane.financial;

/**
 * 二级分类
 * @author LeeDane
`* 2016年7月22日 上午9:41:29
 * Version 1.0
 */
public class SubCategory {
	private String value; //展示的小类名称
	private int id;  //小类对应的ID
	private int icon; //显示的图标
	
	public static int DEFAULT_SUB_CATEGORY_ICON = 0;//默认显示的图标
	
	/**
	 * 二级分类的预算
	 * 默认是0.00，没有预算限制。
	 * 需要设置预算时候，先判断是否设置一级分类，有设置一级分类，则该二级预算的最大限制值就是一级分类减去其他二级分类的总预算。
	 * 要是没有设置一级分类，则判断总预算是否设置，要是总预算也没有设置，则该二级预算没有最大设置。要是有总预算，则最大值限制就是
	 * 总预算减去其他一级预算的和。
	 */
	private float budget;
	
	public SubCategory(){}
	
	public SubCategory(String value, int id){
		this.value = value;
		this.id = id;
		this.icon = DEFAULT_SUB_CATEGORY_ICON;
	}
	
	public SubCategory(String value, int id, int icon){
		this.value = value;
		this.id = id;
		this.icon = icon;
	}
	
	@Override
	public String toString() {
		return "SubCategories [value=" + value + ", id=" + id + "]";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public float getBudget() {
		return budget;
	}

	public void setBudget(float budget) {
		this.budget = budget;
	}
	
	
}
