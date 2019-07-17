package com.cn.leedane.utils;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import org.apache.log4j.Logger;

import com.cn.leedane.model.UserBean;

/**
 * 系统中相同部分的工具类
 * @author LeeDane
 * 2016年7月12日 下午2:40:09
 * Version 1.0
 */
public class CommonUtil {
	private static Logger logger = Logger.getLogger(CommonUtil.class);
	
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
			logger.error("不能读取属性文件. " + "请确保" + propertiesPath +"的文件存在");
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
			logger.error("不能读取属性文件. " + "请确保" + propertiesName +"的.properties文件存在");
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
            
            /*if (LOCAL_IP.equals(ip) || LOCAL_IP1.equals(ip)) {
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                    ip = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }*/
        }  
        return StringUtil.isNull(ip)? "无法解析客户端IP": ip;  
		//return request.getRemoteAddr();
	}
	
	public static String remoteAddr(HttpServletRequest request) {
		try {
			// 使用了代理服务器
//			String ip = request.getHeader("X-Cluster-Client-Ip");
//			if (ip != null && ip.length() > 0) {
//				return ip;
//			} else {
				/*InetAddress myip = InetAddress.getLocalHost();
				String hostAddress = myip.getHostAddress(); // ip地址
				// String HostName=myip.getHostName(); //主机名
				return hostAddress;*/

			String ipAddress = request.getHeader("x-forwarded-for");
			if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("Proxy-Client-IP");
			}
			if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getHeader("WL-Proxy-Client-IP");
			}
			if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
				ipAddress = request.getRemoteAddr();
				if(ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")){
					//根据网卡取本机配置的IP
					InetAddress inet = null;
					try {
						inet = InetAddress.getLocalHost();
					} catch (UnknownHostException e) {
						e.printStackTrace();
					}
					ipAddress= inet.getHostAddress();
				}
			}
			//对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
			if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
				if(ipAddress.indexOf(",")>0){
					ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
				}
			}
			return ipAddress;
//}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析请求中的浏览器信息
	 * @param request
	 * @return
	 */
	public static String getBroswerInfo(HttpServletRequest request){
		UserAgent userAgent = UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
		Browser browser = userAgent.getBrowser();
		OperatingSystem os = userAgent.getOperatingSystem();
		String message = "浏览器名称："+ browser.getName() +
				", 版本："+ browser.getVersion(request.getHeader("User-Agent")) +
				", 类型："+ browser.getBrowserType() +
				", 制造商："+ browser.getManufacturer() +
				", 渲染引擎：" + browser.getRenderingEngine() +
				", 操作系统： "+ os.getName();
		logger.info(message);
		return  message;
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
	 * 判断请求是否是页面，是页面请求才返回true
	 * 注意：非page请求需要添加request的header X-Requested-With值为XMLHttpRequest
	 * @param request
	 * @return
	 */
	public static boolean isPageRequest(HttpServletRequest request, HttpServletResponse response){
		
		if("XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
			return false;
		
		return true;
	}
	
	/**
	 * 判断某个类是否实现了该接口
	 * @param clazz
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
	 * 获得格式化的总数
	 * @param total
	 * @return
	 */
	public static String getFormatTotal(int total){
		if(total < 100)
			return total + "";
		
		if(total >= 100 && total < 1000)
			return ((int)total/100) +"00+";
		
		if(total >= 1000 && total < 10000)
			return ((int)total/1000) +"000+";
		
		if(total >= 10000 && total < 100000)
			return ((int)total/10000) +"0000+";
		
		if(total >= 100000 && total < 1000000)
			return ((int)total/100000) +"00000+";
		
		return total +"";
	}

	/**
	 * 从生日中获取年龄
	 * @param user
	 * @return
	 */
	public static int getAgeByUser(UserBean user) {
		int age = 0;
		if(user != null){
			if(user.getBirthDay() != null)
				return DateUtil.getAgeByBirthDay(user.getBirthDay());
			return user.getAge();
		}
		return age;
	}
}
