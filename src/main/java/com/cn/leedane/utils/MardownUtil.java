package com.cn.leedane.utils;

import java.io.IOException;

import org.markdown4j.Markdown4jProcessor;

/**
 * mardown语法的开源解析工具类
 * 1.Markdown4j maven地址：https://mvnrepository.com/artifact/org.commonjava.googlecode.markdown4j/markdown4j
 * 2.MDTool github地址：https://github.com/cevin15/MDTool
 * 3.pegdown github地址：https://github.com/sirthias/pegdown
 * @author LeeDane
 * 2017年6月27日 下午1:49:01
 * version 1.0
 */
public class MardownUtil {

	private MardownUtil(){}

	/**
	 * 将mardown语法的字符串解析成html输出
	 * @param mardownStr
	 * @return
	 */
	public static String parseHtml(String mardownStr){
		String re;
		String hasTag = JsoupUtil.getInstance().getContentNoTag(mardownStr);
		if(mardownStr.equals(hasTag))
			return mardownStr;
		try {
			re = new Markdown4jProcessor().process(mardownStr);
		} catch (IOException e) {
			re = mardownStr;
			e.printStackTrace();
		}
		return re;
	}
}
