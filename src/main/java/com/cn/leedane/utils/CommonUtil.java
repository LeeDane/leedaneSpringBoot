package com.cn.leedane.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.security.sm4.SM4Utils;
import com.cn.leedane.springboot.controller.Oauth2HtmlController;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import net.sf.json.JSONObject;
import org.apache.http.HttpHeaders;
import org.apache.log4j.Logger;

import com.cn.leedane.model.UserBean;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

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
	public static boolean isPageRequest(HttpServletRequest request){
		
		if("XMLHttpRequest".equals(request.getHeader("X-Requested-With")))
			return false;

		return true;
	}

	/**
	 * 判断请求是否是页面，是页面请求才返回true
	 * 注意：非page请求需要添加request的header X-Requested-With值为XMLHttpRequest
	 * @param request
	 * @return
	 */
	public static boolean isAndroidRequest(HttpServletRequest request){
		if("android".equals(request.getHeader("platform")))
			return true;
		return false;
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

	/**
	 * 执行多行文本命令
	 * @param commandStrs
	 * @return
	 * @throws InterruptedException
	 */
	public static String execMult(String commandStrs) throws InterruptedException {
		if(StringUtil.isNull(commandStrs))
			return "空的命令无法执行";

		String[] commands = commandStrs.split(";");
		if(commands.length > 0){
			StringBuffer buffer = new StringBuffer();
			for(int i = 0; i < commands.length; i++){
				String command = commands[i];
				buffer.append("执行："+ command);
				buffer.append("\n");
				buffer.append("结果：");
				buffer.append(exec(command));
				buffer.append("\n");
			}

			return buffer.toString();
		}

		return "无法执行命令，请检查后重试。";
	}
	/**
	 * 执行command命令
	 * @param command
	 * @return
	 * @throws InterruptedException
	 */
	public static String exec(String command) throws InterruptedException {
		if(StringUtil.isNull(command))
			return "空的命令无法执行";

		String returnString = "";
		Process pro = null;
		Runtime runTime = Runtime.getRuntime();
		if (runTime == null) {
			System.err.println("Create runtime false!");
		}
		try {
			pro = runTime.exec(command);
			BufferedReader input = new BufferedReader(new InputStreamReader(pro.getInputStream()));
			PrintWriter output = new PrintWriter(new OutputStreamWriter(pro.getOutputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				returnString = returnString + line + "\n";
			}
			input.close();
			output.close();
			pro.destroy();
		} catch (IOException e) {
			logger.error("执行command命令出现异常", e);
		}
		return returnString;
	}

	/**
	 * 从链接的参数中解析id=
	 * 这里为了防止地址栏有多个，只解析第一个即可,如有：https://item.jd.com/100003661795.html?url=https://item.jd.com/1000519284.html
	 * @param url
	 * @return
	 */
	public static String parseLinkId(String url){
		String pattern = "(?<=(\\.com/))\\d+[^\\.htm]";
		// 创建 Pattern 对象
		Pattern r = Pattern.compile(pattern);
		// 现在创建 matcher 对象
		Matcher m = r.matcher(url);
		while(m.find()){
			//这里为了防止地址栏有多个，只解析第一个即可
			return m.group(0);
		}
		return null;
	}

	/**
	 * 从连接中解析ID
	 * 这里为了防止地址栏有多个，只解析第一个即可,如有：https://detail.tmall.com/item.htm?id=45114&spm=a220m.1000858.1000725.17.157424feoZKynL&id=591568958088&areaId=440100&user_id=2300221997&cat_id=2&is_b=1&rn=fc4725b55bc45642fb2117c08cc140ae&id=1234&pp=1
	 * 宽断言
	 * 			(?=exp)		匹配exp前面的位置
	 * 			(?<=exp)	匹配exp后面的位置
	 * 			(?!exp)		匹配后面跟的不是exp的位置
	 *			(?<!exp)	匹配前面不是exp的位置
	 * @param url
	 * @return
	 */
	/**
	 * 从连接中解析ID
	 * 这里为了防止地址栏有多个，只解析第一个即可,如有：https://detail.tmall.com/item.htm?id=45114&spm=a220m.1000858.1000725.17.157424feoZKynL&id=591568958088&areaId=440100&user_id=2300221997&cat_id=2&is_b=1&rn=fc4725b55bc45642fb2117c08cc140ae&id=1234&pp=1
	 * 宽断言
	 * 			(?=exp)		匹配exp前面的位置
	 * 			(?<=exp)	匹配exp后面的位置
	 * 			(?!exp)		匹配后面跟的不是exp的位置
	 *			(?<!exp)	匹配前面不是exp的位置
	 * @param url 地址
	 * @param params 可以支持多个匹配，或运算
	 * @return
	 */
	public static String parseLinkParams(String url, String ...params){
		if(params == null || params.length == 0)
			throw new ParameterUnspecificationException("params must not null.");
		StringBuffer paramsStr = new StringBuffer();
		for(String param: params){
			paramsStr.append(param);
			paramsStr.append("|");
		}

		String rp = "(" + StringUtil.deleteLastStr(paramsStr.toString()) +")";
//		String params = "(id|goods_id)";
		String pattern =  "(?<=(^|&|\\?)"+ rp +"=)\\d+([^&]*)";
		// 创建 Pattern 对象
		Pattern r = Pattern.compile(pattern);
		// 现在创建 matcher 对象
		Matcher m = r.matcher(url);
		while(m.find()){
			//这里为了防止地址栏有多个，只解析第一个即可
			return m.group(0);
		}
		return null;
	}

	/**
	 * 解析文本中带有淘口令的文本，带$xxxxxxx$返回
	 * @param text 带有淘口令的文本
	 * @return
	 */
	public static String parseTaokouling(String text){
		if(StringUtil.isNotNull(text)){
			//\p{xx}：a character with the xx property - 表示一个拥有 xx 属性的字符
			//Sc：Currency symbol - 货币字符属性
			String pattern =  "([\\p{Sc}])\\w{8,12}([\\p{Sc}])";
			// 创建 Pattern 对象
			Pattern r = Pattern.compile(pattern);
			// 现在创建 matcher 对象
			Matcher m = r.matcher(text);
			while(m.find()){
				//这里为了防止地址栏有多个，只解析第一个即可
				return m.group(0);
			}
			return text;
		}
		return text;
	}

	/**
	 * 解析淘口令并获得其中的长连接
	 * @param taokouling 带有淘口令的文本
	 * @return 没有解析到结果返回null
	 */
	public static String getUrlByTaokouling(String taokouling){
		if(StringUtil.isNotNull(taokouling)){
			Connection conn = null;
			try {
				Map<String, String> dataMap = new HashMap<String, String>();
				dataMap.put("text", taokouling);

//				Connection.Request request = new Connection.Request();

				conn = Jsoup.connect("http://www.taokouling.com/index/taobao_tkljm");

				conn.ignoreContentType(true)
						.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
						.header("Accept", "application/json, text/javascript, */*; q=0.01")
						.header("Accept-Encoding", "gzip, deflate")
						.header("Accept-Language", "zh-CN,zh;q=0.9")
						.header("Connection", "keep-alive")
						.header("Content-Type", "application/x-www-form-urlencoded")
						.header("Host", "www.taokouling.com")
						.header("Origin", "http://www.taokouling.com")
						.header("Referer", "http://www.taokouling.com/index/taobao_tkljm")
						.header("X-Requested-With", "XMLHttpRequest")
						.header("Content-Length", "22")
						.timeout(8000)
						.data(dataMap)
						.method(Connection.Method.POST);
				conn.execute();
				Connection.Response res = conn.response();
				JSONObject object = JSONObject.fromObject(res.body());
				if(object != null && object.optInt("code") == 1){
					JSONObject data = object.optJSONObject("data");
					String link = data.optString("url");
					return getDetailUrlByTaobaoUrl(link);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}

	/**
	 * 在解析淘口令得到的商品链接需要再次请求才能获取真正商品详情的链接
	 * @param link 通过淘口令获取的商品链接
	 * @return 没有解析到结果返回null
	 */
	public static String getDetailUrlByTaobaoUrl(String link){
		if(StringUtil.isNotNull(link)){
//			Connection conn = null;
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(link).openConnection();
				conn.setInstanceFollowRedirects(false);
				conn.setConnectTimeout(8000);
				conn.setRequestProperty(HttpHeaders.REFERER, "");
				return getRedirectUrl(conn.getHeaderField("Location"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}

	/**
	 * 通过获取重定向地址获取淘宝长链接
	 * @param needRedirectUrl
	 * @return
	 * @throws Exception
	 */
	private static String getRedirectUrl(String needRedirectUrl) throws Exception {
		HttpURLConnection conn = (HttpURLConnection) new URL(needRedirectUrl).openConnection();
		conn.setInstanceFollowRedirects(false);
		conn.setConnectTimeout(8000);
		/**
		 * 递归找到最终的url(包含location)
		 */
		if (conn.getHeaderField("location") == null)
			return needRedirectUrl;
		else
			return getRedirectUrl(conn.getHeaderField("location"));
	}

	/**
	 * 对数据进行加密，其中+号用*jia*代替
	 * @param paramsMap
	 * @return
	 */
	public static String sm4Encrypt(Map<String, Object> paramsMap){
		//生成state
		String plainText = JSONObject.fromObject(paramsMap).toString();
		SM4Utils sm4 = new SM4Utils();
		sm4.secretKey = Oauth2HtmlController.secretKey;
		String cipherText = sm4.encryptData_ECB(plainText);
		return cipherText.replaceAll("\\+","*jia*");
	}

	/**
	 * 对数据进行解密，其中把*jia*还原成+号
	 * @param cipherText 密文
	 * @return
	 */
	public static JSONObject sm4Decrypt(String cipherText){
		cipherText = cipherText.replaceAll("\\*jia\\*", "+"); //在网络传输过程中，会把+替换成%20，这里需要还原回来
		SM4Utils sm4 = new SM4Utils();
		sm4.secretKey = Oauth2HtmlController.secretKey;
		try{
			return JSONObject.fromObject(sm4.decryptData_ECB(cipherText));
		}catch (Exception e){
		    e.printStackTrace();
		}finally {

		}
		return new JSONObject();
	}

	/**
	 * 从文本中解析到地址信息
	 * 测试：司机：其实我是个 #预言家  #这就是山东   #搞笑  #亮一亮山东新动能 https://v.douyin.com/KAve9m/ 复制此链接，打开【抖音短视频】，直接观看视频！
	 * @param text
	 * @return
	 */
	public static String parseLink(String text){
		Pattern pattern = Pattern.compile("https?://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			return matcher.group();
		}
		return null;
	}

	/*public static void main(String[] args) {
		String url = "https://mobile.yangkeduo.com/duo_coupon_landing.html?goods_id1=4946116872&pid=9503869_122542943&cpsSign=CC_191202_9503869_122542943_0a778a5cb045d934c818f82128872ce9&duoduo_type=2";
		System.out.println(parseLinkParams(url, "id", "goods_id", "pid"));
		System.out.println(getUrlByTaokouling("$A5OZYBTfgdn$"));
	}*/

}
