package com.cn.leedane.message;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.enums.NotificationType;
import com.cn.leedane.message.notification.Notification;
import com.cn.leedane.redis.util.RedisUtil;
import com.taobao.api.ApiException;
import com.taobao.api.DefaultTaobaoClient;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFcSmsNumSendRequest;
import com.taobao.api.response.AlibabaAliqinFcSmsNumSendResponse;

/**
 * 发送通知的实现类
 * @author LeeDane
 * 2016年7月12日 下午2:23:24
 * Version 1.0
 */
public class SendNotificationImpl implements ISendNotification{
	
	protected static final String URL = "http://gw.api.taobao.com/router/rest";
    //正式环境需要设置为:http://gw.api.taobao.com/router/rest
    protected static final String APPKEY = "23304394";
    protected static final String SECRET = "2b1f44d4947c82b95b156358aa685972";

	@Override
	public boolean Send(Notification notification) {
		String type = notification.getType();
		if(StringUtil.isNull(type)) return false;
		
		if(NotificationType.ADD_FRIEND.value.equals(type)
				|| NotificationType.AGREE_FRIEND.value.equals(type)){
			//FeedbackNotification feedbackNotification = (FeedbackNotification) notification;
		}else if(NotificationType.REGISTER_VALIDATION.value.equals(type)){//用户注册验证码
			sendRegisterValidation(notification);
		}else if(NotificationType.LOGIN_VALIDATION.value.equals(type)){//用户登录验证码
			sendLoginValidation(notification);
		}else if(NotificationType.UPDATE_PSW_VALIDATION.value.equals(type)){//修改密码验证码
			sendUpdatePswValidation(notification);
		}else if(NotificationType.UPDATE_INFO_VALIDATION.value.equals(type)){//信息变更验证码
			sendUpdateInfoValidation(notification);
		}else if(NotificationType.LOGIN_ERROR_VALIDATION.value.equals(type)){//登录异常验证码
			sendLoginErrorValidation(notification);
		}else if(NotificationType.IDENTITY_VALIDATION.value.equals(type)){//身份验证验证码
			sendIdentityValidation(notification);
		}else if(NotificationType.ACTIVITY_VALIDATION.value.equals(type)){//活动确认验证码
			sendActivityValidation(notification);
		}
		return true;
	}
	
	/**
	 * 用户注册验证码(1个小时过期)
	 * @param notification
	 */
	private void sendRegisterValidation(Notification notification) {
		String mobilePhone = notification.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap("register_"+mobilePhone, map);
		System.out.println(sendAliDaYu("注册验证", "{\"code\":\""+validationCode+"\",\"product\":\""+notification.getToUser().getAccount()+"\"}", mobilePhone, "SMS_4961018"));
	}
	
	/**
	 * 用户登录验证码(1个小时过期)
	 * @param notification
	 */
	private void sendLoginValidation(Notification notification) {
		String mobilePhone = notification.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap("login_"+mobilePhone, map);
		System.out.println(sendAliDaYu("登录验证", "{\"code\":\""+validationCode+"\",\"product\":\"\"}", mobilePhone, "SMS_4961020"));
	}
	/**
	 * 修改密码验证码(1个小时过期)
	 * @param notification
	 */
	private void sendUpdatePswValidation(Notification notification) {
		String mobilePhone = notification.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap("updatepsw_"+mobilePhone, map);
		System.out.println(sendAliDaYu("变更验证", "{\"code\":\""+validationCode+"\",\"product\":\""+notification.getToUser().getAccount()+"\"}", mobilePhone, "SMS_4961016"));
	}
	
	/**
	 * 信息变更验证码(1个小时过期)
	 * @param notification
	 */
	private void sendUpdateInfoValidation(Notification notification) {
		String mobilePhone = notification.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap("updateinfo_"+mobilePhone, map);
		System.out.println(sendAliDaYu("变更验证", "{\"code\":\""+validationCode+"\",\"product\":\""+notification.getToUser().getAccount()+"\"}", mobilePhone, "SMS_4961015"));
	}
	/**
	 * 登录异常验证码(1个小时过期)
	 * @param notification
	 */
	private void sendLoginErrorValidation(Notification notification) {
		String mobilePhone = notification.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap("loginerror_"+mobilePhone, map);
		System.out.println(sendAliDaYu("登录验证", "{\"code\":\""+validationCode+"\",\"product\":\""+notification.getToUser().getAccount()+"\"}", mobilePhone, "SMS_4961019"));
	}
	
	/**
	 * 身份验证验证码(1个小时过期)
	 * @param notification
	 */
	private void sendIdentityValidation(Notification notification) {
		String mobilePhone = notification.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap("identity_"+mobilePhone, map);
		System.out.println(sendAliDaYu("身份验证", "{\"code\":\""+validationCode+"\",\"product\":\""+notification.getToUser().getAccount()+"\"}", mobilePhone, "SMS_4961021"));
	}
	/**
	 * 活动确认验证码(1个小时过期)
	 * @param notification
	 */
	private void sendActivityValidation(Notification notification) {
		String mobilePhone = notification.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap("activity_"+mobilePhone, map);
		String item = "测试活动";
		System.out.println(sendAliDaYu("活动验证", "{\"code\":\""+validationCode+"\",\"product\":\""+notification.getToUser().getAccount()+"\",\"item\":\""+item+"\"}", mobilePhone, "SMS_4961017"));
	}
	/**
	 * 发送到阿里大鱼的请求
	 * @param signName 短信签名
	 * @param tempParams 模板参数(json字符串)
	 * @param phoneNum 对方手机号码
	 * @param tempCode 模板的编码
	 * @return
	 */
	public String sendAliDaYu(String signName, String tempParams, String phoneNum, String tempCode){
		TaobaoClient client = new DefaultTaobaoClient(URL, APPKEY, SECRET);
	   	 AlibabaAliqinFcSmsNumSendRequest req = new AlibabaAliqinFcSmsNumSendRequest();
	   	 //req.setExtend("123456");
	   	 req.setSmsType("normal");
	   	 req.setSmsFreeSignName(signName); //"注册验证"
	   	 req.setSmsParamString(tempParams);//"{\"code\":\"1234\",\"product\":\"1\"}"
	   	 req.setRecNum(phoneNum);//
	   	 req.setSmsTemplateCode(tempCode);
	   	 AlibabaAliqinFcSmsNumSendResponse rsp;
	   	 
	   	 String result = "";
		try {
			rsp = client.execute(req);
			result = rsp.getBody();
		} catch (ApiException e) {
			e.printStackTrace();
		}
		return result;
	}
   	 
}
