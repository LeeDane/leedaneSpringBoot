package com.cn.leedane.utils;

import org.apache.log4j.Logger;

import java.util.regex.Pattern;

/**
 * 商城相关的工具类
 * @author LeeDane
 * 2019年11月20日 下午19:40:09
 * Version 1.0
 */
public class MallUtil {
	private static Logger logger = Logger.getLogger(MallUtil.class);
	private MallUtil(){
		
	}

	/**
	 * 判断平台是否在系统所支持的平台里面
	 * @param platform
	 * @return
	 */
	public static boolean inPlatform(String  platform){
		if(StringUtil.isNull(platform))
			return false;
		EnumUtil.ProductPlatformType[] types = EnumUtil.ProductPlatformType.values();
		if(types.length > 0){
			for(EnumUtil.ProductPlatformType type: types){
				if(type.value.equalsIgnoreCase(platform))
					return true;
			}
		}
		return false;
	}
}
