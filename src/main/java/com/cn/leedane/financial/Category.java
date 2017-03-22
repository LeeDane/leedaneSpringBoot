package com.cn.leedane.financial;

import java.util.List;

import com.cn.leedane.model.IDBean;

/**
 * 分类
 * @author LeeDane
`* 2016年7月22日 上午9:41:13
 * Version 1.0
 */
public class Category extends IDBean{
	/**
	 * 
	 */
	private static final long serialVersionUID = 995264068175438380L;

	/**
	 * 支出分类的总预算
	 * 默认是0.00，没有预算限制。
	 * 需要设置预算时候，需要判断所有的一级分类和二级分类的预算总和，这时设置的值必须大于/等于
	 * 该总和。
	 */
	private float budget;
	
	/**
	 * 所有的一级分类列表
	 * @return
	 */
	private List<ParentGategory> parentGategories;

	public List<ParentGategory> getParentGategories() {
		return parentGategories;
	}

	public void setParentGategories(List<ParentGategory> parentGategories) {
		this.parentGategories = parentGategories;
	}

	public float getBudget() {
		return budget;
	}

	public void setBudget(float budget) {
		this.budget = budget;
	}
	
	
}
