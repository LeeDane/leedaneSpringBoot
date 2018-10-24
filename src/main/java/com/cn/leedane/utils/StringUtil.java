package com.cn.leedane.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.cn.leedane.exception.ErrorException;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 字符串相关工具类
 * @author LeeDane
 * 2016年7月12日 上午10:31:37
 * Version 1.0
 */
public class StringUtil {
	private static Logger logger = Logger.getLogger(StringUtil.class);
	/**
	 * 要是null，就将null转成""
	 * @param origin
	 * @return
	 */
	public static String changeNotNull(Object origin){
		return origin == null ? "" : String.valueOf(origin);
	}
	
	/**
	 * 将null转成"",并转化成utf-8格式的字符串
	 * @param origin
	 * @return
	 */
	public static String changeNotNullAndUtf8(Object origin){		
		return getUTF8StringFromGBKString(changeNotNull(origin));
	}
	
	/***
	 * 获取免登陆验证码(7天过期)
	 * @param userid
	 * @param secretCode
	 * @return
	 * @throws ErrorException
	 */
	public static String getUserToken(String userid, String secretCode) throws ErrorException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userid);
		//map.put("pwd", pwd);
		Date currentDate = DateUtil.getCurrentTime();
		map.put("time", DateUtil.DateToString(currentDate, "yyyyMMddHHmmss"));
		map.put("overdue", DateUtil.DateToString(DateUtil.getOverdueTime(currentDate, "7天")));
		JSONObject json = JSONObject.fromObject(map);
		String value = json.toString();
		return new String(Base64Util.encode(value.getBytes()));
		//return new String(Des3Utils.EncryptBy3DES(value, secretCode));
	}
	
	/***
	 * 获取免登陆验证码
	 * @param account
	 * @param pwd
	 * @param overdue
	 * @return
	 * @throws ErrorException
	 */
	public static String getUserToken(String userid, String secretCode, Date overdue) throws ErrorException{
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("userid", userid);
		//map.put("pwd", pwd);
		Date currentDate = DateUtil.getCurrentTime();
		map.put("time", DateUtil.DateToString(currentDate, "yyyyMMddHHmmss"));
		map.put("overdue", DateUtil.DateToString(overdue, "yyyyMMddHHmmss"));
		JSONObject json = JSONObject.fromObject(map);
		String value = json.toString();
		return new String(Base64Util.encode(value.getBytes()));
		//return new String(Des3Utils.EncryptBy3DES(value, secretCode));
	}
	
	/**
	 * 校验免登陆验证码
	 * @param noLoginCode
	 * @param account
	 * @param format 目前支持格式：如"x年","x个月","x天","x小时","x分钟","x秒钟"
	 * @return
	 */
	public static boolean checkNoLoginCode(String noLoginCode, String account, String format){
		boolean result = false;
		if(StringUtil.isNull(noLoginCode) || noLoginCode.length() < 15)
			return result;
		try {
			long time = Long.parseLong(noLoginCode.substring(0, 13));
			Date date = new Date(time);
			//获取过期时间
			Date outDate = DateUtil.getOverdueTime(date, format);
			
			//判断如果没有过期
			if(!DateUtil.isOverdue(new Date(), outDate)){
				String str = String.valueOf(time) + MD5Util.compute(account);
				result = noLoginCode.startsWith(str);
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 获取邮箱的账号如：825711424@qq.com,返回825711424
	 * @param origin
	 * @return
	 */
	public static String getAccountByEmail(String origin){
		String [] strs = null;
		if(origin != null && origin != "" && "".equals(origin)){
			strs = origin.split("@");
		}
		return strs.length >0 ? strs[0] : null;
	}
	
	/**
	 * 产生邮箱注册码
	 * @param registerTime 注册时间如：20140506021212
	 * @param accountMd5 账号的MD5加密码
	 */
	public static String produceRegisterCode(String registerTime,String accountMd5) {	
		return registerTime + MD5Util.compute(accountMd5);
	}
	
	/**
	 * 产生找密码凭证码
	 * @param accountMd5 账号的MD5加密码
	 * @param validTime 有效时间（秒）
	 * @return
	 */
	public static String produceFindPasswordCode(String accountMd5,long validTime){
		return MD5Util.compute(accountMd5) + System.currentTimeMillis() + validTime;
	}
	
	
	/**
	 * 检查找回密码凭证是否有效
	 * @param findPasswordCode
	 * @param accountMd5 账号的MD5加密码
	 * @return
	 */
	public static boolean checkoutFindPasswordCode(String findPasswordCode,String accountMd5) {
		if(findPasswordCode == null || accountMd5 == null || findPasswordCode.equals("") || accountMd5.equals(""))
			return false;
		boolean isCheck = false;
		String account = MD5Util.compute(accountMd5);
		account.length();
		//账号信息不对
		if(!findPasswordCode.startsWith(account)) return isCheck;
		
		//
		String currentTime = findPasswordCode.substring(account.length(), findPasswordCode.length()-1);
		if(currentTime.length() < 1) return isCheck;
		
		
		return isCheck;
	}
	
	/**
	 * 判断字符串是否为空，为空返回true
	 * @param origin 源字符串
	 * @return
	 */
	public static boolean isNull(String origin) {
		
		return origin == null || origin.trim() == "" || origin.trim().equals("") ? true : false;
	}
	
	/**
	 * 判断字符串是否不为空，不为空返回true
	 * @param origin 源字符串
	 * @return
	 */
	public static boolean isNotNull(String origin) {
		
		return !isNull(origin);
	}
	
	/**
	 * 判断字符串是否全部都是整型数字
	 * 注意：浮点数在此方法返回为false
	 * @param origin  源字符串
	 * @return
	 */
	public static boolean isIntNumeric(String origin){ 
	   Pattern pattern = Pattern.compile("^-?[0-9]+"); 
	   Matcher isNum = pattern.matcher(origin);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}
	
	/**
	 * 判断字符串是否全部都是数字(整型和浮点型)
	 * @param origin  源字符串
	 * @return
	 */
	public static boolean isNumeric(String origin){ 
	   Pattern pattern = Pattern.compile("-?[0-9]+.?[0-9]*"); 
	   Matcher isNum = pattern.matcher(origin);
	   if( !isNum.matches() ){
	       return false; 
	   } 
	   return true; 
	}
	
	/**
	 * 将字符串转成int类型
	 * @param origin
	 * @return
	 */
	public static int stringToInt(String origin){
		int v = 0;
		if(StringUtil.isIntNumeric(origin)){
			v = Integer.parseInt(String.valueOf(origin));
		}
		return v;
	}
	
	/**
	 * 截取字符串中指定的值后获取剩下的部分
	 * @param origin  原始字符串
	 * @param key  要去掉的值
	 * @return
	 */
	public static String getExtraValue(String origin, String key){
		if(StringUtil.isNull(origin)) return null;
		
		return origin.replaceAll(key, "");
	}
	
	/**
	 * 检验字符串是否是链接
	 * @param origin
	 * @return
	 */
	public static boolean isLink(String origin){
		
		if(!StringUtil.isNull(origin)){
			Pattern p = Pattern.compile("(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr|top|wang)[^\u4e00-\u9fa5\\s]*");
	        String group;
	        Matcher m=p.matcher(origin);
	        while(m.find()){
	            group = m.group().trim();
	            if(StringUtil.isNotNull(group)){
	                return true;
	            }
	        }
		}
		return false;
	}

	
	public static String getUTF8StringFromGBKString(String gbkStr) {  
		try {  
			return new String(getUTF8BytesFromGBKString(gbkStr), "UTF-8");  
		} catch (UnsupportedEncodingException e) {  
			throw new InternalError();  
		}  
	}  
		
	public static byte[] getUTF8BytesFromGBKString(String gbkStr) {  
		int n = gbkStr.length();  
		byte[] utfBytes = new byte[3 * n];  
		int k = 0;  
		for (int i = 0; i < n; i++) {  
			int m = gbkStr.charAt(i);  
			if (m < 128 && m >= 0) {  
				utfBytes[k++] = (byte) m;  
				continue;  
			}  
			utfBytes[k++] = (byte) (0xe0 | (m >> 12));  
			utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));  
			utfBytes[k++] = (byte) (0x80 | (m & 0x3f));  
		}  
		if (k < utfBytes.length) {  
			byte[] tmp = new byte[k];  
			System.arraycopy(utfBytes, 0, tmp, 0, k);  
			return tmp;  
		}  
		return utfBytes;  
	} 
	
	/**
	 * 将对象转化成int类型
	 * @param obj  整形的对象
	 * @return
	 */
	public static int changeObjectToInt(Object obj) {
		try {
			return Integer.parseInt(String.valueOf(obj));
		} catch (Exception e) {
			logger.error(obj +"转换成int失败");
			return 0;
		}
	}
	
	/**
	 * 将对象转化成float类型
	 * @param obj  整形的对象
	 * @return
	 */
	public static float changeObjectToFloat(Object obj) {
		try {
			return Float.parseFloat(String.valueOf(obj));
		} catch (Exception e) {
			logger.error(obj +"转换成long失败");
			return 0f;
		}
	}
	
	/**
	 * 将对象转化成Double类型
	 * @param obj  整形的对象
	 * @return
	 */
	public static double changeObjectToDouble(Object obj) {
		try {
			return Double.parseDouble(String.valueOf(obj));
		} catch (Exception e) {
			logger.error(obj +"转换成Double失败");
			return 0d;
		}
	}
	
	/**
	 * 将boolean转成1或者0
	 * @param result
	 * @return
	 */
	public static int changeBooleanToInt(boolean result) {
		return result? ConstantsUtil.STATUS_NORMAL: ConstantsUtil.STATUS_DISABLE;
	}
	
	 /**
     * 将对象转化成long类型
     * @param obj  长整形的对象
     * @return
     */
    public static long changeObjectToLong(Object obj) {
        try {
            return Long.parseLong(String.valueOf(obj));
        } catch (Exception e) {
        	logger.error(obj +"转换成long失败");
            return 0;
        }
    }
	/**
	 * 将对象转化成boolean类型,出错为false
	 * @param obj  boolean型的对象
	 * @return
	 */
	public static boolean changeObjectToBoolean(Object obj) {
		try {
			return Boolean.parseBoolean(String.valueOf(obj));
		} catch (Exception e) {
			logger.error(obj +"转换成boolean失败");
			return false;
		}
	}
	
	/**
	 * 获取字符串后缀(这里只获取最后是.后的字符串)
	 * @param name  原始名称
	 * @return
	 */
	public static String getSuffixs(String name){
		if(StringUtil.isNull(name)){
			return null;
		}	
		return name.substring(name.lastIndexOf(".") + 1, name.length());
	}
	
	/**
	 * 获取文件名(包括后缀)
	 * @param name  原始名称
	 * @return
	 */
	public static String getFileName(String name){
		if(StringUtil.isNull(name)){
			return null;
		}	
		return name.substring(name.lastIndexOf("/") + 1, name.length());
	}
	
	/**
	 * 拼接字符串
	 * @param objs
	 * @return
	 */
	public static String getStringBufferStr(Object ... objs){
		StringBuffer buffer = new StringBuffer();
		for(Object o: objs){
			buffer.append(o);
		}
		return buffer.toString();
	}
	
	/**
	 * 将字符串转成数字，其他非数字直接返回-1
	 * @param origin
	 * @return
	 */
	public static int parseInt(String origin){
		int result = -1;
		try {
			result = Integer.parseInt(origin);
		} catch (Exception e) {
			logger.error("字符串转化整形数字失败，返回-1");
		}
		return result;
	}
	
	/**
	 * 将字符串转成数字，其他非数字直接返回默认值
	 * @param origin
	 * @param defaultValue
	 * @return
	 */
	public static int parseInt(String origin, int defaultValue){
		if(isNull(origin))
			return defaultValue;
		try {
			defaultValue = Integer.parseInt(origin);
		} catch (Exception e) {
			logger.error(origin+"字符串转化整形数字失败，返回默认值"+ defaultValue);
		}
		return defaultValue;
	}
	
	/**
	 * 根据昨天记录的连续签到的天数获取今天的分数
	 * 计算评分的分数(1+2+2+3+3+4+4+5+5.......)
	 * @param continuous 根据昨天记录的连续签到的天数
	 * @param score 昨天记录的分数
	 * @return
	 */
	public static int getScoreBySignin(int continuous, int score){
		
		//没有昨天签到记录的情况下
		if(continuous == 0) {
			return score + 1;
		}
		switch (continuous) {
			case 1:
				score = score + 2;
			break;
			case 2:
				score = score + 2;		
				break;
			case 3:
				score = score + 3;
				break;
			case 4:
				score = score + 3;
				break;
			case 5:
				score = score + 4;
				break;
			case 6:
				score = score + 4;
				break;
			default:
				
				//大于6天就每天加
				score = score + 5;
				break;
		}
		return score;
	}
	
	/**
	 * 根据昨天记录的连续签到的天数获取今天应该的分数
	 * 计算评分的分数(1+2+2+3+3+4+4+5+5.......)
	 * @param continuous 根据昨天记录的连续签到的天数
	 * @return
	 */
	public static int getScoreBySignin(int continuous){
		int score;
		//没有昨天签到记录的情况下
		if(continuous == 0) {
			return 1;
		}
		switch (continuous) {
			case 1:
				score = 2;
			break;
			case 2:
				score = 2;		
				break;
			case 3:
				score = 3;
				break;
			case 4:
				score = 3;
				break;
			case 5:
				score = 4;
				break;
			case 6:
				score = 4;
				break;
			default:
				//大于6天就每天加
				score = 5;
				break;
		}
		return score;
	}
	
	/**
	 * 构建uuid
	 * @param tablename
	 * @return
	 */
	public static String buildUUID(String tablename){
		return UUID.randomUUID().toString() + tablename + System.currentTimeMillis();
	}
	
	/**
	 * 生成下载码
	 * @param uid
	 * @param fileName
	 * @param uuid
	 * @return
	 */
	public static String produceDownloadCode(int uid, String fileName, String uuid){
		StringBuffer buffer = new StringBuffer();
		buffer.append(uid);
		buffer.append(fileName);
		buffer.append(uuid);
		buffer.append(System.currentTimeMillis());
		return new String(Base64Util.encode(buffer.toString().getBytes()));
	}
	
	
	/**
	 * 将boolean类型的成功与否对象转成通用的字符串
	 * @param success
	 * @return
	 */
	public static String getSuccessOrNoStr(boolean success){
		if(success){
			return "成功";
		}else{
			return "失败";
		}
	}
	
	/**
	 * 生产6位验证码
	 * @return
	 */
	public static String build6ValidationCode(){
		Random random = new Random();
		StringBuffer code = new StringBuffer(); 
		code.append(random.nextInt(10));
		code.append(random.nextInt(10));
		code.append(random.nextInt(10));
		code.append(random.nextInt(10));
		code.append(random.nextInt(10));
		code.append(random.nextInt(10));
		if(code.toString().length() != 6)
			return build6ValidationCode();
		else
			return code.toString();
	}
	
	/**
	 * 通过本地系统的文件的路径解析获取mime
	 * @param filePath
	 * @return
	 */
	public static String getMime(String filePath){
		// getMagicMatch accepts Files or byte[],    
		// which is nice if you want to test streams    
		MagicMatch match = null;
		try {
			match = Magic.getMagicMatch(new File(filePath), true);
		} catch (MagicParseException e) {
			//e.printStackTrace();
		}catch (MagicMatchNotFoundException e) {
			//e.printStackTrace();
		} catch (MagicException e) {
			//e.printStackTrace();
		}   
	    if(match == null)
	    	return "text/plain";
	    return match.getMimeType();
	}
	
	/**
	 * 从文本内容中提取@的用户账号信息(返回没有@)
	 * @param content
	 * @return
	 */
	public static Set<String> getAtUserName(String content){
		Set<String> usernames = new HashSet<String>();
		if(isNotNull(content)){
			Pattern p=Pattern.compile("\\s*\\@[\\S]+\\s*");  
			Matcher m=p.matcher(content);  
			String group = null;
			while(m.find()){  
				group = m.group().trim();
				if(isNotNull(group) && group.startsWith("@")){
					group = group.substring(1, group.length());
					usernames.add(group);
				}
			} 
		}
		return usernames;
	}
	
	public static Set<String> getSearchHighlight(String content){
		Set<String> topics = new HashSet<String>();
		if(isNotNull(content)){
			Pattern p = Pattern.compile("(<[^>]*>)");
			Matcher m = p.matcher(content);
			while(m.find()){
				String group = m.group();
				topics.add(group.substring(1, group.length() - 2));
			}
		}
		return topics;
	}
	
	/**
	 * 获取文本的[]里面的ID(int)作为列表输出
	 * @param content
	 * @return
	 */
	public static Set<Integer> getImgIdList(String content){
		Set<Integer> imgIds = new HashSet<Integer>();
		if(isNotNull(content)){
			Pattern p=Pattern.compile("\\[([^\\[\\]]+)\\]");  
			Matcher m=p.matcher(content);  
			String group = null;
			while(m.find()){  
				group = m.group().trim();
				if(isNotNull(group) && group.startsWith("[") && group.endsWith("]")){
					group = group.substring(1, group.length() -1);
					if(changeObjectToInt(group) > 0)
						imgIds.add(changeObjectToInt(group));
				}
			} 
		}
		return imgIds;
	}
	
	/**
	 * 从有道翻译返回的json数据中获得翻译内容
	 * @param returnMsg
	 * @return
	 */
	public static String getYoudaoFanyiContent(String returnMsg) {
		logger.info("有道翻译返回的信息:"+returnMsg);
		StringBuffer buffer = new StringBuffer();
		if(isNotNull(returnMsg)){
			JSONObject json = JSONObject.fromObject(returnMsg);
			if(json.has("translation")){
				JSONArray array = json.getJSONArray("translation");
				if(array.size() > 0){
					for(int i = 0 ; i < array.size(); i++){
						buffer.append(array.get(i).toString()+"  \n");
					}
				}
			}else{
				buffer.append("抱歉！无法翻译" + (json.has("errorCode")? "，错误码是:"+json.getInt("errorCode") : ""));
			}
		}else{
			buffer.append("无法进行翻译，请稍后重试");
		}
		
		return buffer.toString();
	}
	
	/**
	 * 用正则表达式解析获取()里面的内容
	 * @param source
	 * @return
	 */
	public static List<String> getBracket(String source){
		List<String> r = new ArrayList<String>();
		String regex = "\\((.*?)\\)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(source);
		while (m.find()) {
			r.add(m.group(1));
		}
		return r;
	}
	
	/**
	 * 将头尾有"的字符串去掉"
	 * @param source
	 * @return
	 */
	public static String parseJSONToString(String source){
		StringBuffer b = new StringBuffer(source);
		if(source.startsWith("\"")){
			b.deleteCharAt(0);
		}
		if(source.endsWith("\"")){
			b.deleteCharAt(b.length() -1);
		}
		return b.toString();
	}
	
	/**
	 * 获取统一状态的名称
	 * @param status
	 * @return
	 */
	public static String getStatusText(int status){
		switch (status) {
			case ConstantsUtil.STATUS_AUDIT:
				return "等待审核";
			case ConstantsUtil.STATUS_DELETE:
				return "删除";
			case ConstantsUtil.STATUS_DISABLE:
				return "禁用";
			case ConstantsUtil.STATUS_DRAFT:
				return "草稿";
			case ConstantsUtil.STATUS_INFORMATION:
				return "等待完善资料";
			case ConstantsUtil.STATUS_NO_ACTIVATION:
				return "注册未激活";
			case ConstantsUtil.STATUS_NO_TALK:
				return "被禁言";
			case ConstantsUtil.STATUS_NO_VALIDATION_EMAIL:
				return "未验证邮箱";
			case ConstantsUtil.STATUS_NORMAL:
				return "正常";
			case ConstantsUtil.STATUS_SELF:
				return "私有";
			default:
				return null;
		}
	}
	
	/**
	 * 生成shareId
	 * @return
	 */
	public static String getShareId(){
		return UUID.randomUUID().toString().replace("-", "");
	}
	
	/*public static void main(String[] args) {
		//logger.info(StringUtil.changeNotNullAndUtf8("赵本山代表作被指\"丑化\"农民 丢弃农村传统底蕴"));
		//logger.info(StringUtil.isNumeric("-5.00"));
		//logger.info(StringUtisl.isIntNumeric("5"));
		for(int i=0; i < 1000; i++){
			logger.info(build6ValidationCode());
		}
		//logger.info(getMime("G://新建 Microsoft Office Excel 2007 工作簿.xls"));
		String text = ",哈哈";
		if(StringUtil.isNotNull(text)){
			while(text.length()> 0){
				if(text.charAt(0) == '.' || text.charAt(0) == '/' || text.charAt(0) == ',' ||text.charAt(0) == '，'){
					text = text.substring(1, text.length());
				}else{
					break;
				}
			}
			
		}
		
		logger.info("结果：" + text);
		String content = "空格空格<和都会好的/>奶粉<sskfkf /><dj />回复返回回复<的集合附加费/>ff";
		Set<String> ss = getTopic(content);
		System.out.println(ss);
	}*/
}
