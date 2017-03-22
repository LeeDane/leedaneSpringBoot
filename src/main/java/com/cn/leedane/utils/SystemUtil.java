package com.cn.leedane.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 系统工具类
 * @author LeeDane
 * 2016年7月12日 上午10:32:32
 * Version 1.0
 */
public class SystemUtil {

	
	/**
	 * 获取安装环境的信息
	 * @return
	 */
	public static List<String> getSystemEnvironmentInfo(){
		List<String> list = new ArrayList<String>();
		
		list.add("Java运行时环境版本:"+System.getProperty("java.version"));
		list.add("Java 运行时环境供应商:"+System.getProperty("java.vendor"));
		list.add("Java 供应商的URL:"+System.getProperty("java.vendor.url"));
		list.add("Java安装目录:"+System.getProperty("java.home"));
		list.add("Java 虚拟机规范版本:"+System.getProperty("java.vm.specification.version"));
		list.add("Java 类格式版本号:"+System.getProperty("java.class.version"));
		list.add("Java类路径:"+System.getProperty("java.class.path"));
		list.add("操作系统的名称:"+System.getProperty("os.name"));
		list.add("操作系统的架构:"+System.getProperty("os.arch"));
		list.add("操作系统的版本:"+System.getProperty("os.version"));
		list.add("用户的主目录:"+System.getProperty("user.home"));
		list.add("用户的当前工作目录:"+System.getProperty("user.dir"));
		list.add("自定义变量getProperty CONF_LOCATION:"+System.getProperty("conf.location"));
		
		return list;
	}
	
	public static void main(String[] args) {
		List<String>  list = getSystemEnvironmentInfo();
		for(String s: list){
			System.out.println(s);
		}
	}
}
