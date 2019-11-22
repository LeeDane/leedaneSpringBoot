package com.cn.leedane.model;

import java.util.Date;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.OptionUtil;

/**
 * 记账二级类bean实体类
 * @author LeeDane
 * 2016年12月8日 下午8:59:06
 * Version 1.0
 */
public class FinancialTwoLevelCategoryBean extends RecordTimeBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static int DEFAULT_SUB_CATEGORY_ICON = 0;//默认显示的图标

	/**
	 * 一级分类的ID
	 */
	private long oneLevelId;

	/**
	 * 展示的大类名称
	 */
	private String categoryValue;

	/**
	 * 显示的图标
	 */
	private String iconName;

	/**
	 * 二级分类的预算
	 * 默认是0.00，没有预算限制。
	 * 需要设置预算时候，先判断是否设置一级分类，有设置一级分类，则该二级预算的最大限制值就是一级分类减去其他二级分类的总预算。
	 * 要是没有设置一级分类，则判断总预算是否设置，要是总预算也没有设置，则该二级预算没有最大设置。要是有总预算，则最大值限制就是
	 * 总预算减去其他一级预算的和。
	 */
	private float budget;

	/**
	 * 是否是默认的分类
	 */
	private boolean isDefault;

	/**
	 * 排序的位置
	 */
	private int categoryOrder;
	
	/**
	 * 是否是系统默认的(禁止删除，修改)
	 */
	private boolean isSystem;
	
	public FinancialTwoLevelCategoryBean(){}
	
	public FinancialTwoLevelCategoryBean(long oneLevelId, String categoryValue, boolean isDefault, int categoryOrder, String iconName, boolean isSystem){
		this.oneLevelId = oneLevelId;
		this.categoryValue = categoryValue;
		this.isDefault = isDefault;
		this.categoryOrder = categoryOrder;
		this.createUserId = OptionUtil.adminUser.getId();
		this.createTime = new Date();
		this.status = ConstantsUtil.STATUS_NORMAL;
		this.iconName = iconName;
		this.isSystem = isSystem;
	}
	
	@Override
	public String toString() {
		return "TwoLevelCategory [categoryValue=" + categoryValue + ", id=" + id + "]";
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public long getOneLevelId() {
		return oneLevelId;
	}

	public void setOneLevelId(int oneLevelId) {
		this.oneLevelId = oneLevelId;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	
	
}
