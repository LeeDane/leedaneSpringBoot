package com.cn.leedane.model;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;
/**
 * 分类实体类
 * @author LeeDane
 * 2017年6月28日 上午11:20:52
 * version 1.0
 */
@Table(value="T_CATEGORY")
public class CategoryBean extends RecordTimeBean{

	private static final long serialVersionUID = 1L;
	//分类状态，-1：草稿，1：正常，0:禁用，2、删除, 3:待审核
	
	/**
	 * 父分类的id
	 */
	private long pid;
	/**
	 * 分类显示的文本信息
	 */
	private String text;
	
	/**
	 * 分类默认显示的icon，只支持boostrap自定义的icon
	 */
	@Column(value = "icon", required=false)
	@Field
	private String icon;
	
	/**
	 * 分类选中显示的icon，只支持boostrap自定义的icon
	 */
	@Column(value = "selected_icon", required=false)
	@Field
	private String selectedIcon;
	
	/**
	 * 分类选中显示的颜色， 支持RGB格式颜色, "red"等
	 */
	@Column(value = "color", required=false)
	@Field
	private String color;
	
	/**
	 * 分类背景的颜色， 支持RGB格式颜色, "red"等
	 */
	@Column(value = "back_color", required=false)
	@Field
	private String backColor;
	
	/**
	 * 分类链接，结合全局enableLinks选项为列表树节点指定URL
	 */
	@Column(value = "href", required=false)
	@Field
	private String href;
	
	/**
	 * 分类当前节点是否能选中，默认是true
	 */
	//private boolean selectable = true; 
	
	/**
	 * 标记是否是系统级别的
	 */
	@Column(value = "is_system")
	@Field
	private boolean isSystem;
	
	/**
	 * 分类当前的级别， 默认是1
	 */
	@Column(value = "level", required=false)
	@Field
	private int level = 1; 

	public long getPid() {
		return pid;
	}

	public void setPid(long pid) {
		this.pid = pid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getSelectedIcon() {
		return selectedIcon;
	}

	public void setSelectedIcon(String selectedIcon) {
		this.selectedIcon = selectedIcon;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getBackColor() {
		return backColor;
	}

	public void setBackColor(String backColor) {
		this.backColor = backColor;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public boolean isSystem() {
		return isSystem;
	}

	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}
	
}
