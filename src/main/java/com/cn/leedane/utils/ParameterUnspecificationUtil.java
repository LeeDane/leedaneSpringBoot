package com.cn.leedane.utils;

import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.springboot.SpringUtil;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.QueryParser.Operator;
import org.apache.lucene.search.*;
import org.apache.lucene.store.SimpleFSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * 请求参数校验的工具类
 * @author LeeDane
 * 2019年11月29日 上午8:30:09
 * Version 1.0
 */
public class ParameterUnspecificationUtil {
	private static Logger logger = Logger.getLogger(MallUtil.class);
	private ParameterUnspecificationUtil(){

	}

	/**
	 * 校验空字符串
	 * @param str
	 * @return
	 */
	public static void checkNullString(String  str){
		if(StringUtil.isNull(str))
			throw new ParameterUnspecificationException("has null string parameter");
	}

	/**
	 * 校验空字符串
	 * @param str
	 * @param msg 字符串为空后的提示信息
	 */
	public static void checkNullString(String  str, String msg){
		if(StringUtil.isNull(str))
			throw new ParameterUnspecificationException(msg);
	}
}
