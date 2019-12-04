package com.cn.leedane.model;


/**
 * 选项配置类
 * @author LeeDane
 * 2016年7月12日 上午10:48:49
 * Version 1.0
 */
//@Table(name="T_OPTION")
public class OptionBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	//状态：-1：草稿 0：禁止  1：正常 
	/**
	 * 选项的键名
	 */
	private String optionKey;
	
	/**
	 * 选择的键值
	 */
	private String optionValue; 
	
	/**
	 * 选项的版本
	 */
	private int version;
	
	private String optionDesc;
	
	
	//@Column(name="option_key")
	public String getOptionKey() {
		return optionKey;
	}
	public void setOptionKey(String optionKey) {
		this.optionKey = optionKey;
	}
	
	//@Column(name="option_value")
	public String getOptionValue() {
		return optionValue;
	}
	public void setOptionValue(String optionValue) {
		this.optionValue = optionValue;
	}
	
	//@Column(nullable=false)
	public int getVersion() {
		return version;
	}
	public void setVersion(int version) {
		this.version = version;
	}
	
	//@Column(name="option_desc")
	public String getOptionDesc() {
		return optionDesc;
	}
	public void setOptionDesc(String optionDesc) {
		this.optionDesc = optionDesc;
	}
	
}
