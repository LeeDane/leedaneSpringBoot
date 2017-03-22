package com.cn.leedane.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

/**
 * 系统中相同部分的工具类
 * @author LeeDane
 * 2016年7月12日 下午2:40:09
 * Version 1.0
 */
public class CommonUtil {
	
	private CommonUtil(){
		
	}
	
	/**
	 * 获得指定系统资源文件的输入流
	 * @param filePath
	 * @return
	 */
	public static InputStream getResourceAsStream(String filePath){
		return CommonUtil.class.getClassLoader().getResourceAsStream(filePath);
	}
	
	/**
	 * 根据指定属性文件中的键获取对应的值(全部缓冲)
	 * @param propertiesPath  指定属性文件的路径
	 * @param key  键
	 * @return
	 */
	public static String getProperties(String propertiesPath, String key){
		return getProperties(propertiesPath,key,true);
	}
	
	/**
	 * 根据指定属性文件中的键获取对应的值
	 * @param propertiesPath  指定属性文件的路径
	 * @param key  键
	 * @param isCache 是否需要缓存
	 * @return
	 */
	public static String getProperties(String propertiesPath, String key, boolean isCache){
		// 获得类加载器，然后把文件作为一个流获取
        InputStream in = getResourceAsStream(propertiesPath);
       
		try {
			 // 创建Properties实例
	        Properties prop = new Properties();
	        // 将Properties和流关联
	        prop.load(in);
			return prop.getProperty(key);
		} catch (Exception e) {
			System.out.println("不能读取属性文件. " + "请确保" + propertiesPath +"的文件存在");
			System.exit(1);
			
		}
		return null;
	}
		
	/**
	 * 根据指定属性文件中的键获取对应的值(全部缓冲)
	 * @param propertiesName  指定属性文件的名称，注意文件必须放在classpath下面且不包括.properties后缀
	 * @param key  键
	 * @return
	 */
	public static String getPropertiesByName(String propertiesName, String key){
		return getPropertiesByName(propertiesName,key,true);
	}
	
	/**
	 * 根据指定属性文件中的键获取对应的值
	 * @param propertiesName  指定属性文件的名称，注意文件必须放在classpath下面且不包括.properties后缀
	 * @param key  键
	 * @param isCache 是否需要缓存
	 * @return
	 */
	public static String getPropertiesByName(String propertiesName, String key, boolean isCache){
		ResourceBundle rb=ResourceBundle.getBundle(propertiesName);  
		try {
			return rb.getString(key);
		} catch (Exception e) {
			System.out.println("不能读取属性文件. " + "请确保" + propertiesName +"的.properties文件存在");
			System.exit(1);
			
		}
		return null;
	}
	
	/**
	 * 解析请求中的IP地址
	 * @param request
	 * @return
	 */
	public static String getIPAddress(HttpServletRequest request){
		//使用了代理服务器
		//String ip = request.getHeader("x-forwarded-for");
		String ip = request.getHeader("x-forwarded-for");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("http_client_ip");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        return StringUtil.isNull(ip)? "无法解析客户端IP": ip;  
		//return request.getRemoteAddr();
	}

	/**
	 * 解析请求中的浏览器信息
	 * @param request
	 * @return
	 */
	public static String getBroswerInfo(HttpServletRequest request){
		return request.getHeader("User-Agent") == null || "".equals(request.getHeader("User-Agent")) ? "无法解析请求的浏览器信息" : request.getHeader("User-Agent");
	}
	
	/**
	 * 将数组转换成list集合对象
	 * @param objs
	 * @return
	 */
	public static List<Object> arrayToList(Object[] objs){
		
		if(objs == null || objs.length == 0) return null;
		List<Object> list = new ArrayList<Object>();
		
		for(Object obj: objs){
			list.add(obj);
		}
		return list;
	}
	
	/**
	 * 获取地址的完整路劲
	 * @param request
	 * @return
	 */
	public static String getFullPath(HttpServletRequest request){
		StringBuffer url = request.getRequestURL();
		if (request.getQueryString() != null) {
		  url.append("?");
		  url.append(request.getQueryString());
		}
		return url.toString();
	}
	
	/**
	 * 判断某个类是否实现了该接口
	 * @param class
	 * @param szInterface 接口的名称(全类名：包括包名)
	 * @return
	 */
	@SuppressWarnings({ "rawtypes" })
	public static boolean isInterface(Class clazz, String szInterface){
		Class[] face = clazz.getInterfaces();
		for(int i = 0, j = face.length; i <j; i++ ){
			if(face[i].getName().equals(szInterface)){
				return true;
			}else{
				Class[] face1 = face[i].getInterfaces();
				for(int x = 0; x < face1.length; x++){
					if(face1[x].getName().equals(szInterface)){
						return true;
					}else if(isInterface(face1[x], szInterface)){
						return true;
					}
				}
			}
		}
		if(null != clazz.getSuperclass()){
			return isInterface(clazz.getSuperclass(), szInterface);
		}
		return false;
	}
	
	/**
	 * 测试
	 * @param args
	 */
	public static void main(String[] args) {
		InputStream in = CommonUtil.getResourceAsStream("leedane.properties");  
		 // 创建Properties实例
       Properties prop = new Properties();
       // 将Properties和流关联
       try {
		prop.load(in);
	} catch (IOException e) {
		e.printStackTrace();
	}
     Set<Object> set = prop.keySet();
     Iterator<Object> it = set.iterator();
     while(it.hasNext()) {
    	 System.out.println(it.next());
     }
	}
}
