package com.cn.leedane.enums;

/**
 * 文件的类型
 * @author LeeDane
 * 2016年7月12日 上午10:23:26
 * Version 1.0
 */
public enum FileType {
	IMAGE("image"), 
	BASE64_60X60("base64_60X60"),
	TEMPORARY("temporary"),  //临时
	UEDITOR("ueditor"),  //ueditor
	FILE("file"); //文件

	public String value;
	private FileType(String value){
		this.value = value;
	}
}
