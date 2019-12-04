package com.cn.leedane.utils;

import com.cn.leedane.model.UserBean;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * 金钱工具类的工具类
 * @author LeeDane
 * 2019年11月20日 下午19:40:09
 * Version 1.0
 */
public class MoneyUtil {
	private static Logger logger = Logger.getLogger(MoneyUtil.class);

	private MoneyUtil(){
		
	}

	/**
	 * 取值，四舍五入保留小数点后两位小数
	 * @param money
	 * @return
	 */
	public static Double twoDecimalPlaces(Double money){
		//整数的话不需要保留小数点
		if(money == 0.0d || isNumeric(money))
			return money;
		return (double) Math.round(money*100)/100; //保留两位小数 四舍五入型
	}

	/**\
	 * 判断是否是整数
	 * @param db
	 * @return
	 */
	public static boolean isNumeric(double db){
		String str = String.valueOf(db);
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

}
