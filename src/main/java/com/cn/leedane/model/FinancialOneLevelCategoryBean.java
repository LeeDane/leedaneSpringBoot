package com.cn.leedane.model;

import java.util.List;

/**
 * 记账一级类bean实体类
 * @author LeeDane
 * 2016年12月8日 下午8:59:22
 * Version 1.0
 */
public class FinancialOneLevelCategoryBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int DEFAULT_PARENT_CATEGORY_ICON = 0;
	/**
	 * 展示的大类名称
	 */
	private String categoryValue;

	/**
	 * 显示的图标
	 */
	private String iconName;

	private int model; //1表示收入, 2表示支出
	
	/**
	 * 一级分类的预算
	 * 默认是0.00，没有预算限制。
	 * 需要设置预算时候，需要判断其所有二级分类的预算总和，这时设置的值必须大于/等于
	 * 该总和，并且判断是否有设置总预算，没有设置总预算，最高可以设置大于等于二级预算总和即可。
	 * 要是设置了总预算，则将总预算的值减去其他一级分类预算得到该一级预算的最大范围，最小范围还是其所有二级预算的总和。
	 */
	private float budget;

	/**
	 * 排序的位置
	 */
	private int categoryOrder;

	/**
	 * 是否是默认的分类
	 */
	private boolean isDefault;
	
	/**
	 * 是否是系统默认的(禁止删除，修改)
	 */
	private boolean isSystem;
	
	/**
	 * 所有一级预算的列表
	 */
	private List<FinancialTwoLevelCategoryBean> twoLevelCategories;
	
	public FinancialOneLevelCategoryBean() {
	}
	
	
	@Override
	public String toString() {
		return "OneLevelCategory [categoryValue=" + categoryValue + ", id=" + id + "]";
	}

	public String getCategoryValue() {
		return categoryValue;
	}


	public void setCategoryValue(String categoryValue) {
		this.categoryValue = categoryValue;
	}

	public String getIconName() {
		return iconName;
	}


	public void setIconName(String iconName) {
		this.iconName = iconName;
	}


	public float getBudget() {
		return budget;
	}

	public void setBudget(float budget) {
		this.budget = budget;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public void setIsDefault(boolean isDefault) {
		this.isDefault = isDefault;
	}

	public int getCategoryOrder() {
		return categoryOrder;
	}


	public void setCategoryOrder(int categoryOrder) {
		this.categoryOrder = categoryOrder;
	}


	public int getModel() {
		return model;
	}

	public void setModel(int model) {
		this.model = model;
	}

	public List<FinancialTwoLevelCategoryBean> getTwoLevelCategories() {
		return twoLevelCategories;
	}

	public void setTwoLevelCategories(
			List<FinancialTwoLevelCategoryBean> twoLevelCategories) {
		this.twoLevelCategories = twoLevelCategories;
	}


	public boolean isSystem() {
		return isSystem;
	}


	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	
	
}
