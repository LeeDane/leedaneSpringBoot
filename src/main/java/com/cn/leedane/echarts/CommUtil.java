package com.cn.leedane.echarts;

import com.cn.leedane.redis.config.LeedanePropertiesConfig;

/**
 * 淘宝相关的公共工具
 * @author LeeDane
 * 2017年12月24日 下午8:46:17
 * version 1.0
 */
public class CommUtil {

	//淘宝api服务器的url
	public static String url = "http://gw.api.taobao.com/router/rest";

	//淘宝应用的appkey
	public static String appkey = "28014076";

	//淘宝应用的secret
	public static String secret = "ae9afa59b55711f42daf833650fa1768";

	//设置淘宝客的推广空间ID
	public static Long adzoneId = 116132925L;

	//授权成功后的回调地址
	public static String getRedirectUrl(){
		if(LeedanePropertiesConfig.newInstance().isDebug()){
			return "http://127.0.0.1:8089/oauth2/taobao/verify";
		}
		return "http://onlyloveu.top/oauth2/taobao/verify";
	}
}
