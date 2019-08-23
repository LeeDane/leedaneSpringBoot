package com.cn.leedane.wechat.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import net.sf.json.JSONObject;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.JsonUtil;

public class HttpRequestUtil {

	public static final int REQUEST_TIME_OUT = 15000; // 请求时间
	public static final int RESPONSE_TIME_OUT = 15000; // 响应时间

	/**
	 * 用HttpURLConnection，通过get方法发送请求
	 * 
	 * @param urlString
	 *            url地址
	 * @return
	 * @throws Exception
	 */
	public static InputStream HttpURLConnectionByGetMethod(String urlString)
			throws Exception {

		// OutputStream out = new ;
		HttpURLConnection urlConnection = null;
		URL url = new URL(urlString);
		InputStream in = null;
		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(REQUEST_TIME_OUT); // 设置请求响应时间
			urlConnection.setReadTimeout(RESPONSE_TIME_OUT); // 设置服务器响应时间
			urlConnection.setRequestMethod("GET"); // 设置请求方法

			urlConnection.connect();

			int code = urlConnection.getResponseCode(); // 获得响应请求码

			if (code != HttpURLConnection.HTTP_OK) {// 请求码不是200
				throw new Exception("服务器连接异常");
			}

			in = urlConnection.getInputStream();

			/** 最不好的方式，因为如果输入流过大，定义的字节数组过大，影响其他的网络资源 **/
			/*
			 * int len = in.available(); //获取输入流的长度 byte[] byt0 = new byte[len];
			 * out.write(in.read(byt0));
			 */

			/*** 不是很友好的代码,因为最后的字节流的长度不一定刚好装1024k，造成浪费资源 **/
			/*
			 * byte[] byt1 = new byte[1024]; while(in.read(byt1) != -1){
			 * out.write(byt1); }
			 */

			/*** 以下是最好的方法 ***/
			/*
			 * int count = 0; byte[] byt = new byte[1024]; //定义1k大小的自己数组
			 * 
			 * while((count = in.read(byt))!= -1){ out.write(byt, 0, count); }*
			 */
		} finally {
			/*
			 * if(in != null){ //关闭输入流 in.close(); }
			 */
			/*
			 * if(urlConnection != null){ //关闭HttpURLConnection连接
			 * urlConnection.disconnect(); }
			 */
			// 以上都不能关闭
		}

		return in;

	}

	/**
	 * 用HttpURLConnection，通过post方法发送请求
	 * 
	 * @param urlString
	 *            服务器url地址
	 * @param params
	 *            传递的参数
	 * @return
	 */
	public static InputStream HttpURLConnectionByPostMethod(String urlString,
			Map<String, Object> params) throws Exception {
		InputStream in = null;
		HttpURLConnection urlConnection = null;
		URL url = new URL(urlString);
		DataOutputStream out = null;

		try {
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(REQUEST_TIME_OUT);
			urlConnection.setReadTimeout(RESPONSE_TIME_OUT);
			urlConnection.setDoInput(true);
			urlConnection.setDoOutput(true);
			urlConnection.setRequestMethod("POST");
			// Post 请求不能使用缓存
			urlConnection.setUseCaches(false);

			StringBuffer sb = new StringBuffer();
			for (Map.Entry<String, Object> m : params.entrySet()) {
				sb.append(m.getKey())
						.append("=")
						.append(URLEncoder.encode(String.valueOf(m.getValue()),
								"UTF-8")).append("&");
			}

			sb.deleteCharAt(sb.length() - 1);
			out = new DataOutputStream(urlConnection.getOutputStream());
			out.writeBytes(sb.toString());
			out.flush();

			url.getContent();

			int code = urlConnection.getResponseCode(); // 获得响应请求码

			if (code != HttpURLConnection.HTTP_OK) {// 请求码不是200
				throw new Exception("服务器连接异常");
			}

			in = urlConnection.getInputStream();

		} finally {
			/*
			 * if(in != null){ //关闭输入流 in.close(); } if(out != null){ //关闭输入流
			 * out.close(); } if(urlConnection != null){ //关闭HttpURLConnection连接
			 * urlConnection.disconnect(); }
			 */
		}

		return in;
	}
	
	public static String sendAndRecieveFromYoudao(String Content) throws IOException{
		String keyfrom = "1232224qq123"; 
		String key = "734370526";
		String type = "data";
		String doctype = "json";
		String version = "1.1";
	    String q = URLEncoder.encode(Content, "utf-8"); 
	    String getURL = "http://fanyi.youdao.com/openapi.do?keyfrom="+ keyfrom +"&key="+ key +"&type="+ type +"&doctype="+ doctype +"&version="+ version +"&q="+q; 
	    URL getUrl = new URL(getURL); 
	    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection(); 
	    connection.connect(); 

	     // 取得输入流，并使用Reader读取 
	    BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream(), "utf-8")); 
	    StringBuffer sb = new StringBuffer(); 
	    String line = ""; 
	    while ((line = reader.readLine()) != null) { 
	        sb.append(line); 
	    } 
	    reader.close();    
	     // 断开连接 
	    connection.disconnect(); 
	    return sb.toString(); 
	}
	
	/**
	 * 图灵机器人的调用API
	 * @param Content
	 * @return
	 * @throws IOException
	 */
	public static synchronized String sendAndRecieveFromTuLing(String Content) throws IOException {
		String APIKEY = "b044a69b6cf049daab15f17a900b5b40";
	    String INFO = URLEncoder.encode(Content, "utf-8"); 
	    String getURL = "http://www.tuling123.com/openapi/api?key=" + APIKEY + "&info=" + INFO; 
	    URL getUrl = new URL(getURL); 
	    HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection(); 
	    connection.connect(); 

	     // 取得输入流，并使用Reader读取 
	    BufferedReader reader = new BufferedReader(new InputStreamReader( connection.getInputStream(), "utf-8")); 
	    StringBuffer sb = new StringBuffer(); 
	    String line = ""; 
	    while ((line = reader.readLine()) != null) { 
	        sb.append(line); 
	    } 
	    reader.close();    
	     // 断开连接 
	    connection.disconnect(); 
	    return sb.toString(); 
	}

	/**
	 * 从微信服务器端获取access_token数据
	 */
	public static String getAccessTokenFromWeixin() {
		String result = "";
		String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+WeixinUtil.APP_ID+"&secret="+ WeixinUtil.APP_SECRET +"";
		
		InputStream in;
		try {
			in = HttpRequestUtil.HttpURLConnectionByGetMethod(url);
			if(in != null){
				JSONObject jsonObject = JsonUtil.getInstance().toJsonObject(in);
				if(jsonObject != null){
					result = jsonObject.getString("access_token");
				}
			}				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//把得到的access_token放在缓存中
		SystemCache systemCache = (SystemCache) SpringUtil.getBean("systemCache");
		systemCache.addCache("access_token", result);
		return result;
	}
	
	/*public static void main(String[] args) {
		HttpRequestUtil.getAccessTokenFromWeixin();
	}*/
}