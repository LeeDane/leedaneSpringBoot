package com.cn.leedane.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

/**
 * 自定义Json转化工具类
 * @author LeeDane
 * 2016年7月12日 上午10:29:22
 * Version 1.0
 */
public class JsonUtil {
	
	private static JsonUtil mJsonUtil = null;
	
	private JsonUtil(){
	}
	
	public static synchronized JsonUtil getInstance(){
		if(mJsonUtil == null){
			mJsonUtil = new JsonUtil();
		}
		
		return mJsonUtil;
	}

	/**
	 * 将Android客户端的请求输入流转变成json对象
	 * @param inS 输入流
	 * @return
	 * @throws Exception
	 */
	public JSONObject toJsonObject (InputStream inS)throws Exception {
		BufferedReader in2 = null;
		String value="";
		JSONObject json=null;
		try{
			in2 =new BufferedReader(new InputStreamReader(inS));		
			while((value=in2.readLine())!=null){//一行一行读
				value = URLDecoder.decode(value, "UTF-8");
				System.out.println(value);
				json = JSONObject.fromObject(value);
			}
		}catch(Exception e){
			e.printStackTrace();
		}		 
		inS.close();
		in2.close();
		return json;
	}
	
	/**
	 * 判断一个json对象是否有key存在
	 * @param object json对象
	 * @param key 键名称
	 * @return
	 */
	public static boolean hasKey(JSONObject object, String key){
		
		//对象为空/对象是数组对象/对象是空对象等直接返回false
		if(object.isEmpty() || object.isArray() || object.isNullObject())
			return false;
		
		return object.containsKey(key);
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回null
	 * @param object json对象
	 * @param key  键名称
	 * @return
	 */
	public static Object getValue(JSONObject object, String key){
		if(JsonUtil.hasKey(object, key)){
			return object.get(key);
		}
		return null;
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回""
	 * @param object json对象
	 * @param key  键名称
	 * @return
	 */
	public static String getStringValue(JSONObject object, String key){
		if(JsonUtil.hasKey(object, key)){
			return String.valueOf(object.get(key));
		}
		return "";
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回defaultValue
	 * @param object json对象
	 * @param key  键名称
	 * @param defaultValue 默认值
	 * @return
	 */
	public static String getStringValue(JSONObject object, String key, String defaultValue){
		String v = getStringValue(object, key);
		return StringUtil.isNull(v) ? defaultValue : v;
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回false
	 * @param object json对象
	 * @param key  键名称
	 * @return
	 */
	public static boolean getBooleanValue(JSONObject object, String key){
		if(JsonUtil.hasKey(object, key)){
			if("true".equals(String.valueOf(object.get(key)))){
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回defaultValue
	 * @param object json对象
	 * @param key  键名称
	 * @return
	 */
	public static boolean getBooleanValue(JSONObject object, String key, boolean defaultValue){
		if(JsonUtil.hasKey(object, key)){
			if("true".equals(String.valueOf(object.get(key)))){
				return true;
			}
			return false;
		}
		return defaultValue;
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回0
	 * @param object json对象
	 * @param key  键名称
	 * @return
	 */
	public static int getIntValue(JSONObject object, String key){
		if(JsonUtil.hasKey(object, key)){
			String s = String.valueOf(object.get(key));
			if(StringUtil.isIntNumeric(s)){
				return Integer.parseInt(s);
			}
			return -1;
		}
		return -1;
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回0
	 * @param object json对象
	 * @param key  键名称
	 * @return
	 */
	public static double getDoubleValue(JSONObject object, String key){
		if(JsonUtil.hasKey(object, key)){
			String s = String.valueOf(object.get(key));
			if(StringUtil.isIntNumeric(s)){
				return Double.parseDouble(s);
			}
			return 0;
		}
		return 0;
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回0
	 * @param object json对象
	 * @param key  键名称
	 * @return
	 */
	public static long getLongValue(JSONObject object, String key){
		if(JsonUtil.hasKey(object, key)){
			String s = String.valueOf(object.get(key));
			if(StringUtil.isIntNumeric(s)){
				return Long.parseLong(s);
			}
			return 0;
		}
		return 0;
	}
	
	
	/**
	 * 将json转成实体对象
	 * @param jsonObject
	 * @param t
	 * @return
	 */
	public static Object jsonToObject(JSONObject jsonObject){
		return JSONObject.toBean(jsonObject);
	}
	
	/**
	 * 将json字符串转成实体对象
	 * @param jsonObject
	 * @param t
	 * @return
	 */
	public static Object jsonToObject(String jsonObject){
		return JSONObject.toBean(JSONObject.fromObject(jsonObject));
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回defaultValue
	 * @param object json对象
	 * @param key 键名称
	 * @param defaultValue 默认值
	 * @return
	 */
	public static int getIntValue(JSONObject object, String key, int defaultValue){
		int v = getIntValue(object, key);
		return v == -1 ? defaultValue: v;
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回defaultValue
	 * @param object json对象
	 * @param key 键名称
	 * @param defaultValue 默认值
	 * @return
	 */
	public static long getLongValue(JSONObject object, String key, long defaultValue){
		long v = getLongValue(object, key);
		return v == 0 ? defaultValue: v;
	}
	public static void main(String[] args) {
		
		try {
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public class Message1{
		public Message1(){
			
		}
		private boolean success;
		private List<Map<String, Object>> message;
		public boolean isSuccess() {
			return success;
		}
		public void setSuccess(boolean success) {
			this.success = success;
		}
		public List<Map<String, Object>> getMessage() {
			return message;
		}
		public void setMessage(List<Map<String, Object>> message) {
			this.message = message;
		}	
	}
}
