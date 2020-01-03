package com.cn.leedane.mall.taobao.other;

import com.cn.leedane.task.spring.scheduling.AlimamaStayCookie;
import com.cn.leedane.utils.EnumUtil;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;

import java.util.HashMap;
import java.util.Map;

/**
 * 获取阿里妈妈的共享链接
 * @author LeeDane
 * 2017年12月24日 下午8:43:06
 * version 1.0
 */
public class AlimamaShareLink {
	
	//保存当前登录的cookie
	public static String cookie2 = "136bf80df755debc3ada9d716b3e023a";
	public static boolean success = false; //标记是否成功
	/**
	 * 执行解析阿里妈妈的平台获取分享的地址、二维码等信息
	 * @param taobaoId
	 * @throws Exception 
	 */
	public JSONObject doParse(String taobaoId) throws Exception{
		Connection.Response res;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("success", false);
		
		Map<String, String> cookieMap = new HashMap<String, String>();
		//cookieMap.put("_user_behavior_", "df6ef2e4-7814-4368-b377-fe76678007650");
		//cookieMap.put("_reg_key_", "XJhUE9X5ETJM7AXNUMIM");
		//cookieMap.put("Hm_lvt_a411c4d1664dd70048ee98afe7b28f0b", "1470896037,1470903530,1470964808");
		//cookieMap.put("Hm_lpvt_a411c4d1664dd70048ee98afe7b28f0b", "1470965061");
		cookieMap.put("t", "4de2ae532792353daf595fb087f29209");//经过测试，这是必须项，可以通过浏览器的调试模式找到cookie中的oscid
		cookieMap.put("UM_distinctid", "15f0adafb9b16f-08368b03745b3b-256e6f6f-1fa400-15f0adafb9c3ec");
		cookieMap.put("undefined_yxjh-filter-1", "true");
		cookieMap.put("29901824_yxjh-filter-1", "true");
		cookieMap.put("account-path-guide-s1", "true");
		cookieMap.put("cna", "8UQZEtrzfGoCAduJjuhFEi2Q");
		cookieMap.put("cookie2", cookie2);
		cookieMap.put("v", "0");
		cookieMap.put("_tb_token_", "35e7eb659eee3");
		cookieMap.put("pub-message-center", "1");
		cookieMap.put("alimamapwag", "TW96aWxsYS81LjAgKFdpbmRvd3MgTlQgMTAuMDsgV09XNjQpIEFwcGxlV2ViS2l0LzUzNy4zNiAoS0hUTUwsIGxpa2UgR2Vja28pIENocm9tZS80OS4wLjI2MjMuNzUgU2FmYXJpLzUzNy4zNiBMQkJST1dTRVI%3D");
		cookieMap.put("cookie32", "5b7f66becae9322652e5a693f64a2a16");
		cookieMap.put("alimamapw", "REYUdVZGIXMWI1MdICAQdVIScHYUcyJGIAc6UVZcVVNUBlYHVwIGCFMAAgZQUlQLUgcEBlBRAQZV%0AVAM%3D");
		cookieMap.put("cookie31", "Mjk5MDE4MjQscXElRTYlOUMlODAlRTclODglQjElRTklOUIlQTglRTQlQkQlQjMsODI1NzExNDI0QHFxLmNvbSxUQg%3D%3D");
		cookieMap.put("login", "Vq8l%2BKCLz3%2F65A%3D%3D");
		cookieMap.put("CNZZDATA1256793290", "2038901709-1507711542-%7C1511860157");
		cookieMap.put("apush3d145e0686c29887d6bdbebcd221cfd6", "%7B%22ts%22%3A1511864139009%2C%22heir%22%3A1511864080883%2C%22parentId%22%3A1511854434584%7D");
		cookieMap.put("isg", "AqamC64xwCzbd5dIKoD_QVRh5RzoL-bOV2r3lJBNp0mxE0Ut-hROUBKlHzB4");
		//cookieMap.put("login", "Vq8l%2BKCLz3%2F65A%3D%3D");
		//cookieMap.put("CNZZDATA1256793290", "254272153-1469153905-https%253A%252F%252Fwww.baidu.com%252F%7C1470962466");
		
		Map<String, String> dataMap = new HashMap<String, String>();
		dataMap.put("auctionid", taobaoId);
		dataMap.put("adzoneid", "116132925");
		dataMap.put("siteid", "31996610");
		dataMap.put("scenes", "1");
		dataMap.put("t", "1511864140734");
		dataMap.put("_tb_token_", cookieMap.get("_tb_token_"));//动弹的内容，必须
		dataMap.put("pvid", "10_219.137.141.16_530_1511864148300");//未知，可以不填
		
		res = Jsoup.connect("http://pub.alimama.com/common/code/getAuctionCode.json")
			    .data(dataMap)
			    .userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.75 Safari/537.36 LBBROWSER")
			    .timeout(8000)//网络链接超时的时间
			    .method(Method.GET)
			    .cookies(cookieMap)
			    .ignoreContentType(true)
			    .execute();	
		JSONObject resultJson;
		try{
			resultJson = JSONObject.fromObject(res.body());
			success = true;
			//System.out.println(res.body().toString());
			AlimamaStayCookie.LAST_REQUEST_TIME = System.currentTimeMillis();
		}catch(JSONException e){
			success = false;
			resultMap.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先登录.value));
			resultMap.put("responseCode", EnumUtil.ResponseCode.请先登录.value);
			resultJson = JSONObject.fromObject(resultMap);
		}
		return resultJson;
	}
}
