package com.cn.leedane.utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import com.cn.leedane.exception.ErrorException;
/**
 * 日期工具类
 * 完整的日期格式：yyyy-MM-dd HH(hh):mm:ss S E D F w W a k K z
 * 完整的日期解释：yyyy年MM月dd日 HH(hh)时   mm分 ss秒 S毫秒   星期E 今年的第D天  这个月的第F星期   今年的第w个星期   这个月的第W个星期  今天的a k1~24制时间 K0-11小时制时间 z时区
 * @author LeeDane
 * 2016年7月12日 下午2:40:33
 * Version 1.0
 */
public class DateUtil {
	
	/**
	 * 默认的日期格式“yyyy-MM-dd HH:mm:ss”,其中的HH大小表示的是24小时制，hh是小写表示的是12小时制
	 */
	public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	//public static final String 

	/**
	 * 将Date格式的日期根据format进行格式化
	 * @param date  原始的日期参数
	 * @param format  格式：如"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss", 为空返回默认的格式
	 * @return
	 */
	public static String DateToString (Date date,String format){
		if(date == null)
			return DateToString(date);
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	/**
	 * 将日期字符串根据format转成日期,默认的格式是"yyyy-MM-dd HH:mm:ss"
	 * @param stringDate 字符串日期，格式要正确，不然会抛异常
	 * @param 
	 * @return
	 */
	public static Date stringToDate(String stringDate){
		return DateUtil.stringToDate(stringDate, DEFAULT_DATE_FORMAT);
	}
	
	/**
	 * 将日期字符串转成日期,没有日期格式，系统自动判断
	 * @param stringDate 字符串日期，格式要正确，不然会抛异常
	 * @param 
	 * @return
	 */
	public static Date stringToDateNoFormat(String stringDate){
		if(StringUtil.isNull(stringDate))
			return null;
		int len = stringDate.length();
		String format = DEFAULT_DATE_FORMAT;
		switch(len){
			case 19:
				format = "yyyy-MM-dd HH:mm:ss";
				break;
			case 14:
				format = "yyyyMMddHHmmss";
				break;
			case 10:
				format = "yyyy-MM-dd";
				break;
			case 8:
				format = "yyyyMMdd";
				break;
			
		}
		return DateUtil.stringToDate(stringDate, format);
	}
	
	/**
	 * 将日期字符串根据format转成日期
	 * @param stringDate 字符串日期，格式要正确，不然会抛异常
	 * @param 格式：如"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static Date stringToDate(String stringDate,String format){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
		Date date = null;	   
		try {
			date = simpleDateFormat.parse(stringDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	
	/**
	 * 将日期转化成字符串, 格式是"yyyy-MM-dd HH:mm:ss"
	 * @param origin 原始日期参数
	 * @return
	 */
	public static String DateToString(Date origin){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT);
		return simpleDateFormat.format(origin);
	}
	
	/**
	 * 将日期转化成一定的日期类型, 格式是"yyyy-MM-dd HH:mm:ss"
	 * @param origin 原始日期对象
	 * @param format
	 * @return
	 */
	public static Date DateToDate(Date origin, String format){
		return stringToDate(DateToString(origin), format);
	}
	
	/**
	 * 将日期转化成字符串
	 * @param origin 原始日期参数
	 * @return
	 */
	public static String LongToString(long origin){
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateUtil.DEFAULT_DATE_FORMAT);
		return simpleDateFormat.format(origin);
	}
	
	/**
	 * 将日期对象转换成日期
	 * @param obj 日期对象
	 * @return
	 */
	public static Date objectToDate(Object obj){
		if(obj == null ) return null;
		try{
			return (Date)obj;
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * 获得系统昨天的时间
	 * @return
	 */
	public static Date getYestoday(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}
	
	/**
	 * 获得指定日期的前一天的时间
	 * @return
	 */
	public static Date getYestoday(Date date){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.DATE, -1);
		return c.getTime();
	}
	
	/**
	 * 获得系统的指定天数的时间
	 * @param contains 是否包含当天,true 表示包含,如5-7,包含就是5-1,不包含就是4-30
	 * @param amount 指定的天数，可以为任意整数，负数表示过去的时间，整数表示今天或未来的时间
	 * @return
	 */
	public static Date getDayBeforeOrAfter(int amount, boolean contains){
		Calendar c = Calendar.getInstance();
		if(contains)
			c.add(Calendar.DATE, amount + 1);
		else
			c.add(Calendar.DATE, amount);
		return c.getTime();
	}
	
	/**
	 * 获得指定日期的指定天数的时间
	 * @param date
	 * @param amount 指定的天数，可以为任意整数，负数表示过去的时间，整数表示今天或未来的时间
	 * @param contains 是否包含当天,true 表示包含,如5-7,包含就是5-1,不包含就是4-30
	 * @return
	 */
	public static Date getDayBeforeOrAfter(Date date, int amount, boolean contains){
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if(contains)
			c.add(Calendar.DATE, amount + 1);
		else
			c.add(Calendar.DATE, amount);
		return c.getTime();
	}
	

	/**
	 * 获取系统当前时间
	 * @return
	 */
	public static Long getSystemCurrentTime(){
		return System.currentTimeMillis();
	}
	
	/**
	 * 获取过期时间
	 * 目前支持格式：如"1年","1个月","1天","1小时","1分钟","1秒钟"
	 * @param date
	 * @param overdueFormat
	 * @return
	 * @throws ErrorException 
	 */
	public static Date getOverdueTime(Date date,String overdueFormat) throws ErrorException{
		if(StringUtil.isNull(overdueFormat)) return null;
		int value = -1;
		if(date == null) {
			date = new Date();
		}
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if(overdueFormat.contains("年") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "年"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "年"));
			c.add(Calendar.YEAR, value);
		}else if(overdueFormat.contains("个月") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "个月"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "个月"));
			c.add(Calendar.MONTH, value);
		}else if(overdueFormat.contains("天") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "天"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "天"));
			c.add(Calendar.DATE, value);
		}else if(overdueFormat.contains("小时") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "小时"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "小时"));
			//c.add(Calendar.HOUR, value);//12小时制
			c.add(Calendar.HOUR_OF_DAY, value);//24小时制
		}else if(overdueFormat.contains("分钟") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "分钟"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "分钟"));
			c.add(Calendar.MINUTE, value);
		}else if(overdueFormat.contains("秒钟") && StringUtil.isIntNumeric(StringUtil.getExtraValue(overdueFormat, "秒钟"))){
			value = StringUtil.stringToInt(StringUtil.getExtraValue(overdueFormat, "秒钟"));
			c.add(Calendar.SECOND, value);
		}else{
			throw new ErrorException("表达式的格式有误，格式如：'1年','1个月','1天','1小时','1分钟','1秒钟'");
		}
		return c.getTime();
	}

	/**
	 * 比较两个时间大小(判断日期是否过期)
	 * 注意：当原始时间或者过期时间都为空的情况下，返回的是false(没过期)
	 * @param origin  原始时间
	 * @param overdueTime  过期时间
	 * @return
	 */
	public static boolean isOverdue(Date origin, Date overdueTime){
		if(origin == null || overdueTime == null) 
			return false;
		return overdueTime.getTime() - origin.getTime() > 0 ? false : true;
	}
	
	/**
	 * 根据指定的格式获取系统当前的时间
	 * @param format 格式：如"yyyy-MM-dd","yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public static String getSystemCurrentTime(String format){
		return DateToString(new Date(getSystemCurrentTime()),format);
	}
	
	/**
	 * 获得当前的时间
	 * @return
	 */
	public static Date getCurrentTime(){
		return new Date();
	}
	
	/**
	 * 获取今天日期的开始时间
	 * @return
	 */
	public static Date getTodayStart(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 0); //小时
		c.set(Calendar.MINUTE, 0); //分钟
		c.set(Calendar.SECOND, 0); //秒钟
		return c.getTime();
	}
	
	/**
	 * 获取今天日期的结束时间
	 * @return
	 */
	public static Date getTodayEnd(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.HOUR_OF_DAY, 23); //小时
		c.set(Calendar.MINUTE, 59); //分钟
		c.set(Calendar.SECOND, 59); //秒钟
		return c.getTime();
	}
	
	/**
	 * 获取昨天日期的开始时间
	 * @return
	 */
	public static Date getYesTodayStart(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1); //日
		c.set(Calendar.HOUR_OF_DAY, 0); //小时
		c.set(Calendar.MINUTE, 0); //分钟
		c.set(Calendar.SECOND, 0); //秒钟
		return c.getTime();
	}
	
	/**
	 * 获取昨天日期的结束时间
	 * @return
	 */
	public static Date getYesTodayEnd(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, -1); //日
		c.set(Calendar.HOUR_OF_DAY, 23); //小时
		c.set(Calendar.MINUTE, 59); //分钟
		c.set(Calendar.SECOND, 59); //秒钟
		return c.getTime();
	}
	
	/**
	 * 获取本周日期的开始时间（周日凌晨零点开始）
	 * @return
	 */
	public static Date getThisWeekStart(){
		Calendar currentDate = Calendar.getInstance();
		currentDate.setFirstDayOfWeek(Calendar.MONDAY);
		currentDate.set(Calendar.HOUR_OF_DAY, 0); //小时
		currentDate.set(Calendar.MINUTE, 0); //分钟
		currentDate.set(Calendar.SECOND, 0); //秒钟
		currentDate.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		return currentDate.getTime();
	}
	
	/**
	 * 获得当前日期与本周日的偏移量  
	 * @return
	 */
    public static int getSundayPlus() {  
        Calendar cd = Calendar.getInstance();  
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......  
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);         //因为按中国礼拜一作为第一天所以这里减1  
        if (dayOfWeek == 1) {  
            return 0;  
        } else {  
            return 1 - dayOfWeek;  
        }  
    }  
	
	/**
	 * 获取本月日期的开始时间
	 * @return
	 */
	public static Date getThisMonthStart(){
		Calendar currentDate = Calendar.getInstance();  
		currentDate.set(Calendar.DATE,1);//设为当前月的1号  
		currentDate.set(Calendar.HOUR_OF_DAY, 0); //小时
        currentDate.set(Calendar.MINUTE, 0); //分钟
        currentDate.set(Calendar.SECOND, 0); //秒钟
		return currentDate.getTime();  
	}
	
	/**
	 * 获取上一个月日期的开始时间
	 * @return
	 */
	public static Date getLastMonthStart(){
		Calendar calendar = Calendar.getInstance();  
		calendar.add(Calendar.MONTH, -1);
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		//设置日期格式
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		return stringToDate(sf.format(calendar.getTime()) + " 00:00:00");
	}
	
	/**
	 * 获取上一个月日期的结束时间
	 * @return
	 */
	public static Date getLastMonthEnd(){
		Calendar calendar = Calendar.getInstance();  
		calendar.add(Calendar.MONTH, -1);
		//设置日期为本月最大日期
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
		//设置日期格式
		SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
		return stringToDate(sf.format(calendar.getTime()) + " 23:59:59");
	}
	
	/**
	 * 获取本年日期的开始时间
	 * @return
	 */
	public static Date getThisYearStart(){
		int yearPlus = getYearPlus();  
        GregorianCalendar currentDate = new GregorianCalendar();  
        currentDate.add(GregorianCalendar.DATE,yearPlus);  
        currentDate.set(Calendar.HOUR_OF_DAY, 0); //小时
        currentDate.set(Calendar.MINUTE, 0); //分钟
        currentDate.set(Calendar.SECOND, 0); //秒钟
        return currentDate.getTime();
	}
	
	/**
	 * 获取当前日期在本年的偏移量
	 * @return
	 */
	private static int getYearPlus(){  
        Calendar cd = Calendar.getInstance();  
        int yearOfNumber = cd.get(Calendar.DAY_OF_YEAR);//获得当天是一年中的第几天  
        cd.set(Calendar.DAY_OF_YEAR,1);//把日期设为当年第一天  
        cd.roll(Calendar.DAY_OF_YEAR,-1);//把日期回滚一天。  
        int MaxYear = cd.get(Calendar.DAY_OF_YEAR);  
        if(yearOfNumber == 1){  
            return -MaxYear;  
        }else{  
            return 1-yearOfNumber;  
        }  
    }  
	
	/**
	 * 获取开始时间
	 * @param scope
	 * @return
	 */
	public static Date getBeginTime(EnumUtil.TimeScope scope){
		
		Date beginTime = null;
		switch(scope){
			case 昨日: //昨日
				beginTime = DateUtil.getYesTodayStart();
				break;
			case 当日: //当日
				beginTime = DateUtil.getTodayStart();
				break;
			case 本周: //本周
				beginTime = DateUtil.getThisWeekStart();
				break;
			case 本月: //本月
				beginTime = DateUtil.getThisMonthStart();
				break;
			case 本年: //本年
				beginTime = DateUtil.getThisYearStart();
				break;
		}
		
		return beginTime;
	}
	/**
	 * 获取结束时间
	 * @param scope
	 * @return
	 */
	public static Date getEndTime(EnumUtil.TimeScope scope){
		Date endTime = null;
		switch(scope){
			case 昨日: //昨日
				endTime = DateUtil.getYesTodayEnd();
				break;
			case 当日: //当日
				endTime = DateUtil.getCurrentTime();
				break;
			case 本周: //本周
				endTime = DateUtil.getCurrentTime();
				break;
			case 本月: //本月
				endTime = DateUtil.getCurrentTime();
				break;
			case 本年: //本年
				endTime = DateUtil.getCurrentTime();
				break;
		}
		return endTime;
	}
	
	/**
	 * 获取指定时间段内的时间列表
	 * @param dBegin 开始时间
	 * @param dEnd 结束时间
	 * @return
	 */
	public static List<Date> findDates(Date dBegin, Date dEnd) {
		List<Date> lDate = new ArrayList<Date>();
		lDate.add(dBegin);
		Calendar calBegin = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calBegin.setTime(dBegin);
		Calendar calEnd = Calendar.getInstance();
		// 使用给定的 Date 设置此 Calendar 的时间
		calEnd.setTime(dEnd);
		// 测试此日期是否在指定日期之后
		while (dEnd.after(calBegin.getTime())) {
			// 根据日历的规则，为给定的日历字段添加或减去指定的时间量
			calBegin.add(Calendar.DAY_OF_MONTH, 1);
			lDate.add(calBegin.getTime());
		}
		return lDate;
	}
	
	
	/**
	 * 判断结束时间和开始时间是否在n分钟内(包括n分钟)
	 * @param createTime
	 * @param endTime
	 * @param n n表示分钟数
	 * @return
	 */
	public static boolean isInMinutes(Date createTime, Date endTime, int n){
		try{
		    long diff = endTime.getTime() - createTime.getTime();
		    if(diff < 0) 
		    	return false;
		    return (diff - (1000 * 60 * n)) <= 0 ;
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 计算开始时间离结束时间还有多少分钟
	 * @param createTime
	 * @param endTime
	 * @return
	 */
	public static int leftMinutes(Date createTime, Date endTime){
		try{
		    long diff = endTime.getTime() - createTime.getTime();
		    if(diff < 0) 
		    	return 1;
		    int i = StringUtil.changeObjectToInt(diff / (1000 * 60));
		    return  i == 0? 1 : i;
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		return 1;
	}
	
	/**
	 * 计算开始时间离结束时间还有多少秒
	 * @param createTime
	 * @param endTime
	 * @return
	 */
	public static int leftSeconds(Date createTime, Date endTime){
		try{
		    long diff = endTime.getTime() - createTime.getTime();
		    if(diff < 0) 
		    	return 1;
		    int i = StringUtil.changeObjectToInt(diff / 1000);
		    return  i == 0? 1 : i;
		    
		}catch(Exception e){
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 获取去年本月的开始时间(本月的同比时间)
	 * @return
	 */
	public static String getLastYearThisMonthStart(){
		Calendar calendar = Calendar.getInstance();  
		calendar.add(Calendar.YEAR, -1);
		return DateToString(calendar.getTime(), "yyyy-MM") +"-01 00:00:00";
	}
	
	/**
	 * 获取去年本月的开始时间(本月的同比时间)
	 * @return
	 */
	public static String getLastYearThisMonthEnd(){
		Calendar calendar = Calendar.getInstance();  
		calendar.add(Calendar.YEAR, -1);
		return DateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 获取去年的1月1号凌晨时间(本年的环比时间)
	 * @return
	 */
	public static String getLastYearStart(){
		Calendar calendar = Calendar.getInstance();  
		calendar.add(Calendar.YEAR, -1);
		return DateToString(calendar.getTime(), "yyyy") +"-01-01 00:00:00";
	}
	
	/**
	 * 获取去年的现在这个时间(本年的环比时间)
	 * @return
	 */
	public static String getLastYearEnd(){
		Calendar calendar = Calendar.getInstance();  
		calendar.add(Calendar.YEAR, -1);
		return DateToString(calendar.getTime(), "yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 将日期转化成Timestamp
	 * @param date
	 * @return
	 */
	public static Timestamp getTimestamp(Date date){
		Timestamp timestamp = new Timestamp(date.getTime());
		return timestamp;
	}
	
	/**
	 * 对字符串进行格式化，格式如Thu Sep 03 23:51:25 CST 2015
	 * @param str
	 * @param format
	 * @return 转化不了返回""
	 */
	public static String formatLocaleTime(String str, String format){
		try{
			SimpleDateFormat sdf1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
	   	   	Date date = sdf1.parse(str);
	   	   	SimpleDateFormat sdf = new SimpleDateFormat(format);
	   	   	return sdf.format(date);
		}
		catch (ParseException e){
			e.printStackTrace();
		}
		return "";
	}
	
	/**
	 * 对字符串进行格式化，格式如Thu Sep 03 23:51:25 CST 2015
	 * @param str
	 * @param format
	 * @return 转化不了返回""
	 */
	public static Date formatLocaleTimeToDate(String str, String format){
		try{
			SimpleDateFormat sdf1 = new SimpleDateFormat ("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
	   	   	Date date = sdf1.parse(str);
	   	   	SimpleDateFormat sdf = new SimpleDateFormat(format);
	   	   	return stringToDate(sdf.format(date));
		}
		catch (ParseException e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 对字符串进行格式化，格式如Thu Sep 03 23:51:25 CST 2015
	 * @param str
	 * @param format
	 * @return 转化不了返回""
	 */
	public static String formatStringTime(String str){
		if(StringUtil.isNotNull(str) && str.trim().length() == 21){
			return str.trim().substring(0, 19);
		}
		return str;
	}
	
	/**
	 * 对字符串进行格式化，格式如Thu Sep 03 23:51:25 CST 2015
	 * @param str
	 * @param format
	 * @return 转化不了返回""
	 */
	public static String formatStringTime(String str, String format){
		str = formatStringTime(str);
		if(StringUtil.isNotNull(str)){
			return DateToString(stringToDate(str), format);
		}
		return str;
	}
	
	/**
	 * 判断两个时间是否在某个范围之内
	 * @param end
	 * @param start
	 * @param range
	 * @return
	 */
	public static boolean checkDateInRange(Date end, Date start, int range){
		return Math.abs((int) ((end.getTime() - start.getTime()) / (1000*60*60*24))) <= range;
	}
	
}
