package com.cn.leedane.wechat.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import net.sf.json.JSONObject;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.springboot.SpringUtil;
public class WeixinUtil {
	
	/**
	 * 微信的请求或者响应类型：text
	 */
	public static final String TYPE_TEXT = "text";
	
	/**
	 * 微信的请求或者响应类型：image
	 */
	public static final String TYPE_IMAGE = "image";
		
	/**
	 * 微信的请求或者响应类型：news
	 */
	public static final String TYPE_NEWS = "news";
	
	/**
	 * 选择的模式类型：主菜单
	 */
	public static final String MODEL_MAIN_MENU  = "mainmenu";
	
	/**
	 * 选择的模式类型：翻译
	 */
	public static final String MODEL_TRANSLATION  = "translation";
	
	/**
	 * 选择的模式类型：聊天
	 */
	public static final String MODEL_CHAT  = "chat";
	
	/**
	 * 选择的模式类型：博客查询
	 */
	public static final String MODEL_BLOG_SEARCH  = "blogsearch";
	
	/**
	 * 选择的模式类型：博客查询
	 */
	public static final String MODEL_NEWEST_APP  = "newestapp";
	
	/**
	 * 选择的模式类型：发布心情
	 */
	public static final String MODEL_SEND_MOOD  = "sendmood";
	
	/**
	 * 选择的模式类型：最新博客
	 */
	public static final String MODEL_NEWEST_BLOG  = "newestblog";
	
	/**
	 * 选择的模式类型：退出
	 */
	public static final String MODEL_OUT  = "outbind";

	//开发者订阅号的AppID(应用ID)
	public static final String APP_ID = "wxe1f49c8f15333c14";
	
	//开发者订阅号的AppSecret(应用密钥)
	public static final String APP_SECRET = "e9df02b207eb8b9cb45d916bddd12290";
	
	private static final String UPLOAD_URL = "https://api.weixin.qq.com/cgi-bin/media/upload?access_token=ACCESS_TOKEN&type=TYPE";

	/**
	 * 默认查询博客的数量是5条
	 */
	public static final int DEFAULT_SEARCH_NUMBER = 5;

	
	/**
	 * 获取访问的token,(每两个小时更新一次)
	 */
	public static String getAccessToken() {
		SystemCache systemCache = (SystemCache) SpringUtil.getBean("systemCache");
		if(systemCache.getCache("access_token") != null){
			return systemCache.getCache("access_token").toString();
		}else{
			//从微信服务器重新获取access_token
			return HttpRequestUtil.getAccessTokenFromWeixin();
		}
	}
	
	/**
	 * 文件上传
	 * @param filePath
	 * @param accessToken
	 * @param type
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws KeyManagementException
	 */
	public static String upload(String filePath, String accessToken,String type) throws IOException, NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
		File file = new File(filePath);
		if (!file.exists() || !file.isFile()) {
			throw new IOException("文件不存在");
		}

		String url = UPLOAD_URL.replace("ACCESS_TOKEN", accessToken).replace("TYPE",type);
		
		URL urlObj = new URL(url);
		//连接
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();

		con.setRequestMethod("POST"); 
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false); 

		//设置请求头信息
		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");

		//设置边界
		String BOUNDARY = "----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");

		byte[] head = sb.toString().getBytes("utf-8");

		//获得输出流
		OutputStream out = new DataOutputStream(con.getOutputStream());
		//输出表头
		out.write(head);

		//文件正文部分
		//把文件已流文件的方式 推入到url中
		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte[1024];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();

		//结尾部分
		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");//定义最后数据分隔线

		out.write(foot);

		out.flush();
		out.close();

		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		String result = null;
		try {
			//定义BufferedReader输入流来读取URL的响应
			reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
			if (result == null) {
				result = buffer.toString();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

		JSONObject jsonObj = JSONObject.fromObject(result);
		System.out.println(jsonObj);
		String typeName = "media_id";
		if(!WeixinUtil.TYPE_IMAGE.equals(type)){
			typeName = type + "_media_id";
		}
		String mediaId = jsonObj.getString(typeName);
		return mediaId;
	}
}
