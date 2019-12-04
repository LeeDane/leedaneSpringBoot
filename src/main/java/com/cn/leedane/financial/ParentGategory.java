package com.cn.leedane.financial;

import java.util.List;

/**
 * 一级分类
 * @author LeeDane
`* 2016年7月22日 上午9:41:20
 * Version 1.0
 */
public class ParentGategory {
	private String value; //展示的大类名称
	private int id;  //大类对应的ID
	private int icon; //显示的图标
	public static int DEFAULT_PARENT_CATEGORY_ICON = 0;
	
	/**
	 * 一级分类的预算
	 * 默认是0.00，没有预算限制。
	 * 需要设置预算时候，需要判断其所有二级分类的预算总和，这时设置的值必须大于/等于
	 * 该总和，并且判断是否有设置总预算，没有设置总预算，最高可以设置大于等于二级预算总和即可。
	 * 要是设置了总预算，则将总预算的值减去其他一级分类预算得到该一级预算的最大范围，最小范围还是其所有二级预算的总和。
	 */
	private float budget;
	
	/**
	 * 所有一级预算的列表
	 */
	private List<SubCategory> subCategories;
	
	public ParentGategory(){}
	
	public ParentGategory(String value, int id){
		this.value = value;
		this.id = id;
		this.icon = DEFAULT_PARENT_CATEGORY_ICON;
	}
	
	public ParentGategory(String value, int id, int icon){
		this.value = value;
		this.id = id;
		this.icon = icon;
	}
	
	@Override
	public String toString() {
		return "ParentGategory [value=" + value + ", id=" + id + "]";
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

	public List<SubCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<SubCategory> subCategories) {
		this.subCategories = subCategories;
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
