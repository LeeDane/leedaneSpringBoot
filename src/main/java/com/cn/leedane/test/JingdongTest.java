package com.cn.leedane.test;

import com.cn.leedane.mall.jingdong.CommUtil;
import com.cn.leedane.mall.jingdong.api.SearchProductApi;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.internal.util.CodecUtil;
import com.jd.open.api.sdk.internal.util.HttpUtil;
import com.jd.open.api.sdk.internal.util.StringUtil;
import com.jd.open.api.sdk.request.JdRequest;
import org.junit.Test;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.fail;

/**
 * 京东相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:10:18
 * Version 1.0
 */
public class JingdongTest {

	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@Test
	public void searchProduct(){
//		GoodsReq GoodsReq = new GoodsReq();

		try {
			SearchProductApi.searchProduct(null);
		} catch (JdException e) {
			e.printStackTrace();
		}


	}

	public static void searchgoods () {
		/*KplOpenXuanpinSearchgoodsRequest request=new KplOpenXuanpinSearchgoodsRequest();
		QueryParam queryParam=new QueryParam();
		queryParam.setKeywords("https://item.jd.com/100005270557.html");
		request.setQueryParam(queryParam);
		PageParam pageParam=new PageParam();
		pageParam.setPageSize(10);
		request.setPageParam(pageParam);
		request.setOrderField(0);
		Connection.Response res = null;
		try {
			String url = buildUrl(request);
			res = Jsoup.connect(url)
					.ignoreContentType(true)
					.userAgent("Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; WOW64; Trident/6.0; BIDUBrowser 2.x)")
					.timeout(8000)
					.method(Connection.Method.GET)
					.execute();
			JSONObject object = JSONObject.fromObject(res.body());
			Parser parser = ParserFactory.getJsonParser();
			//KplOpenXuanpinSearchgoodsResponse response = parser.parse(object.toString(), KplOpenXuanpinSearchgoodsResponse.class);
			throw new JdAccessTokenException(object.optString("msg"));
		} catch (Exception e) {
			e.printStackTrace();
			throw new JdAccessTokenException("京东授权网络连接异常");
		}*/
	}

	private static String buildUrl(JdRequest request) throws Exception {
		Map<String, String> sysParams = request.getSysParams();
		Map<String, String> pmap = new TreeMap();
		pmap.put("360buy_param_json", request.getAppJsonParams());
		sysParams.put("method", request.getApiMethod());
		sysParams.put("access_token", CommUtil.getAccessToken());
		sysParams.put("app_key", CommUtil.appkey);
		sysParams.put("v", "1.0");
		pmap.putAll(sysParams);
		String sign = sign(pmap, CommUtil.secret);
		sysParams.put("v", "1.0");
		sysParams.put("sign", sign);
//		sysParams.put("timestamp", DateUtil.DateToString(new Date()));
		StringBuilder sb = new StringBuilder(CommUtil.url);
		sb.append("?");
		sb.append(HttpUtil.buildQuery(sysParams, "UTF-8"));
		sb.append("&360buy_param_json="+ pmap.get("360buy_param_json"));
		return sb.toString();
	}

	private static String sign(Map<String, String> pmap, String appSecret) throws Exception {
		StringBuilder sb = new StringBuilder(appSecret);
		Iterator i$ = pmap.entrySet().iterator();

		while(i$.hasNext()) {
			Map.Entry<String, String> entry = (Map.Entry)i$.next();
			String name = (String)entry.getKey();
			String value = (String)entry.getValue();
			if (StringUtil.areNotEmpty(new String[]{name, value})) {
				sb.append(name).append(value);
			}
		}

		sb.append(appSecret);
		String result = CodecUtil.md5(sb.toString());
		return result;
	}

	public static void main(String[] args) {
		searchgoods();
	}
}
