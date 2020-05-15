package com.cn.leedane.notice.send;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.handler.OptionHandler;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.SMS;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发送通知的实现类
 * @author LeeDane
 * 2016年7月12日 下午2:23:24
 * Version 1.0
 */
@Component
public class SmsNotice extends BaseNotice<SMS>{
	private Logger logger = Logger.getLogger(getClass());
	public static String REGISTER_KEY_PREFIX = "regi_";
	public static String LOGIN_KEY_PREFIX = "login_";
	public static String UPDATE_PSW_KEY_PREFIX = "upps_";
	public static String UPDATE_INFO_KEY_PREFIX = "upif_";
	public static String LOGIN_ERROR_KEY_PREFIX = "loge_";
	public static String IDENTITY_KEY_PREFIX = "ide_";
	public static String ACTIVITY_KEY_PREFIX = "act_";
	public static String BIND_PHONE_KEY_PREFIX = "bdph_";
	public static String REMIND_TAKE_MEDICINE_KEY_PREFIX = "rtm_";
	public static String AGAIN_REMIND_TAKE_MEDICINE_KEY_PREFIX = "artm_";

	@Autowired
	private OptionHandler optionHandler;

	private SMS sms;
	@Override
	public boolean send(SMS sms) throws NoticeException {
		this.sms = sms;
		String type = sms.getType();
		ParameterUnspecificationUtil.checkNullString(type, "没有短信业务类型");

		//校验是否太快发送验证码
		String phone = sms.getToUser().getMobilePhone();
		ParameterUnspecificationUtil.checkPhone(phone, "phone not incorrect");

		int seconds = checkRequency(phone, type);
		if(seconds > 0)
			throw new NoticeException("您操作太频繁啦！距离下次操作剩余："+ seconds +"秒");
		
		if(EnumUtil.NoticeSMSType.ADD_FRIEND.value.equals(type)
				|| EnumUtil.NoticeSMSType.AGREE_FRIEND.value.equals(type)){
			//FeedbackNotification feedbackSms = (FeedbackNotification) sms;
		}else if(EnumUtil.NoticeSMSType.REGISTER_VALIDATION.value.equals(type)){//用户注册验证码
			sendRegisterValidation(sms);
		}else if(EnumUtil.NoticeSMSType.LOGIN_VALIDATION.value.equals(type)){//用户登录验证码
			sendLoginValidation(sms);
		}else if(EnumUtil.NoticeSMSType.UPDATE_PSW_VALIDATION.value.equals(type)){//修改密码验证码
			sendUpdatePswValidation(sms);
		}else if(EnumUtil.NoticeSMSType.UPDATE_INFO_VALIDATION.value.equals(type)){//信息变更验证码
			sendUpdateInfoValidation(sms);
		}else if(EnumUtil.NoticeSMSType.LOGIN_ERROR_VALIDATION.value.equals(type)){//登录异常验证码
			sendLoginErrorValidation(sms);
		}else if(EnumUtil.NoticeSMSType.IDENTITY_VALIDATION.value.equals(type)){//身份验证验证码
			sendIdentityValidation(sms);
		}else if(EnumUtil.NoticeSMSType.ACTIVITY_VALIDATION.value.equals(type)){//活动确认验证码
			sendActivityValidation(sms);
		}else if(EnumUtil.NoticeSMSType.BIND_PHONE_VALIDATION.value.equals(type)){//手机号码绑定验证码
			sendBindPhoneValidation(sms);
		}else if(EnumUtil.NoticeSMSType.REMIND_TAKE_MEDICINE.value.equals(type)){//提醒吃药
			sendRemindTakeMedicine(sms);
		}else if(EnumUtil.NoticeSMSType.AGAIN_REMIND_TAKE_MEDICINE.value.equals(type)){//再次提醒吃药
			sendAgainRemindTakeMedicine(sms);
		}
		return true;
	}

	/**
	 * 用户注册验证码(1个小时过期)
	 * @param sms
	 */
	private void sendRegisterValidation(SMS sms) throws NoticeException{
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
		logger.info(sendAliDaYu("注册验证", "{\"code\":\""+validationCode+"\",\"product\":\"\"}", mobilePhone, "SMS_4961018"));
	}
	
	/**
	 * 用户登录验证码(1个小时过期)
	 * @param sms
	 */
	private void sendLoginValidation(SMS sms) throws NoticeException{
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
		logger.info(sendAliDaYu("登录验证", "{\"code\":\""+validationCode+"\",\"product\":\"\"}", mobilePhone, "SMS_4961020"));
	}
	/**
	 * 修改密码验证码(1个小时过期)
	 * @param sms
	 */
	private void sendUpdatePswValidation(SMS sms) throws NoticeException{
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
		logger.info(sendAliDaYu("变更验证", "{\"code\":\""+validationCode+"\",\"product\":\"\"}", mobilePhone, "SMS_4961016"));
	}
	
	/**
	 * 信息变更验证码(1个小时过期)
	 * @param sms
	 */
	private void sendUpdateInfoValidation(SMS sms) throws NoticeException{
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
		logger.info(sendAliDaYu("变更验证", "{\"code\":\""+validationCode+"\",\"product\":\"\"}", mobilePhone, "SMS_4961015"));
	}
	/**
	 * 登录异常验证码(1个小时过期)
	 * @param sms
	 */
	private void sendLoginErrorValidation(SMS sms) throws NoticeException{
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
		logger.info(sendAliDaYu("登录验证", "{\"code\":\""+validationCode+"\",\"product\":\"\"}", mobilePhone, "SMS_4961019"));
	}
	
	/**
	 * 身份验证验证码(1个小时过期)
	 * @param sms
	 */
	private void sendIdentityValidation(SMS sms) throws NoticeException{
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
		logger.info(sendAliDaYu("身份验证", "{\"code\":\""+validationCode+"\",\"product\":\"\"}", mobilePhone, "SMS_4961021"));
	}
	/**
	 * 活动确认验证码(1个小时过期)
	 * @param sms
	 */
	private void sendActivityValidation(SMS sms) throws NoticeException {
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "1小时")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
		String item = "测试活动";
		logger.info(sendAliDaYu("活动验证", "{\"code\":\""+validationCode+"\",\"product\":\"\",\"item\":\""+item+"\"}", mobilePhone, "SMS_4961017"));
	}

	/**
	 * 手机号码绑定验证码(10分钟)
	 * @param sms
	 */
	private void sendBindPhoneValidation(SMS sms) throws NoticeException {
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "10分钟")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
		logger.info(sendAliDaYu("LeeDane", "{\"code\":\""+validationCode+"\"}", mobilePhone, "SMS_181195996"));
	}

	/**
	 * 提醒吃药(10分钟)
	 * @param sms
	 */
	private void sendRemindTakeMedicine(SMS sms) throws NoticeException {
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "10分钟")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
//		logger.info(sendAliDaYu("LeeDane", "{\"name\":\""+ sms.getToUser().getChinaName()+"\",\"content\":\""+DateUtil.getChineseDateFormat() + "好"+"\"}", mobilePhone, "SMS_190266535"));
		logger.info(sendAliDaYu("LeeDane", "{\"content\":\""+DateUtil.getChineseDateFormat() + "好"+"\"}", mobilePhone, "SMS_190281153"));

	}

	/**
	 * 再次提醒客户吃药(10分钟)
	 * @param sms
	 */
	private void sendAgainRemindTakeMedicine(SMS sms) throws NoticeException {
		String mobilePhone = sms.getToUser().getMobilePhone();
		String validationCode = StringUtil.build6ValidationCode();
		//获取redis缓存的key
		String key = getKey(mobilePhone, sms.getType());
		Map<String, String> map = new HashMap<String, String>();
		map.put("validationCode", validationCode);
		Date createTime = new Date();
		map.put("createTime", DateUtil.DateToString(createTime));
		try {
			map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "10分钟")));
		} catch (ErrorException e) {
			e.printStackTrace();
		}
		RedisUtil.getInstance().addMap(key, map);
		RedisUtil.getInstance().expire(key, sms.getExpire());
		logger.info(sendAliDaYu("LeeDane", "{\"name\":\""+ sms.getToUser().getChinaName()+"\",\"content\":\""+DateUtil.getChineseDateFormat()+"\"}", mobilePhone, "SMS_190276381"));
	}

	/**
	 * 发送到阿里大鱼的请求
	 * @param signName 短信签名
	 * @param tempParams 模板参数(json字符串)
	 * @param mobilePhone 对方手机号码
	 * @param tempCode 模板的编码
	 * @return
	 */
	public boolean sendAliDaYu(String signName, String tempParams, String mobilePhone, String tempCode) throws NoticeException {
		//设置超时时间-可自行调整
		System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
		System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		//初始化ascClient需要的几个参数
		final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
		final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
		//替换成你的AK
		//初始化ascClient,暂时不支持多region（请勿修改）
		IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", StringUtil.changeNotNull(optionHandler.getData("aliyunsmsKeyId")),
				StringUtil.changeNotNull(optionHandler.getData("aliyunsmsKeySecret")));
		try {
			DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
		} catch (ClientException e) {
			e.printStackTrace();
			throw new NoticeException("aliyun sms DefaultProfile.addEndpoint() error");
		}
		IAcsClient acsClient = new DefaultAcsClient(profile);
		//组装请求对象
		SendSmsRequest request = new SendSmsRequest();
		//使用post提交
		request.setMethod(MethodType.POST);
		//必填:待发送手机号。支持以逗号分隔的形式进行批量调用，批量上限为1000个手机号码,批量调用相对于单条调用及时性稍有延迟,验证码类型的短信推荐使用单条调用的方式；发送国际/港澳台消息时，接收号码格式为国际区号+号码，如“85200000000”
		request.setPhoneNumbers(mobilePhone);
		//必填:短信签名-可在短信控制台中找到
		request.setSignName(signName);
		//必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
		request.setTemplateCode(tempCode);
		//可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
		//友情提示:如果JSON中需要带换行符,请参照标准的JSON协议对换行符的要求,比如短信内容中包含\r\n的情况在JSON中需要表示成\\r\\n,否则会导致JSON在服务端解析失败
		if(StringUtil.isNotNull(tempParams))
			request.setTemplateParam(tempParams);
		//可选-上行短信扩展码(扩展码字段控制在7位或以下，无特殊需求用户请忽略此字段)
		//request.setSmsUpExtendCode("90997");
		//可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
//		request.setOutId("yourOutId");
		//请求失败这里会抛ClientException异常
		SendSmsResponse sendSmsResponse = null;
		try {
			sendSmsResponse = acsClient.getAcsResponse(request);
		} catch (ClientException e) {
			e.printStackTrace();
			throw new NoticeException("aliyun sms connect error");
		}
		if(sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK")) {
			//请求成功
			logger.info("给"+ mobilePhone +"发送"+ tempCode +"短信返回成功");
			return true;
		}else{
			//给管理员发送异常信息
			if(OptionUtil.adminUser != null && OptionUtil.adminUser.getId() > 0)
				NoticeUtil.notification(OptionUtil.adminUser.getId(), OptionUtil.adminUser.getId(), "发送短信异常, 异常信息："+ sendSmsResponse.getMessage()+", 发送数据="+ sms.toString());
		}
		throw new NoticeException("aliyun sms send result error");
	}

	/**
	 * 校验手机发送验证码的次数
	 * @param mobilePhone
	 * @param type
	 * @return int 表示剩余的秒数，如果秒数大于0将无法操作
	 * @throws ClientException
	 */
	public int checkRequency(String mobilePhone, String type) throws NoticeException {
		String key = getKey(mobilePhone, type);
		List<String> list = RedisUtil.getInstance().getMap(key, "validationCode", "createTime", "endTime");
		if(list.size() > 0){
			//校验验证码是否正确
			if(StringUtil.isNotNull(list.get(0))){
				String startTime = list.get(1);
				//校验验证码是否过期
				if(StringUtil.isNotNull(startTime)){
					Date start = DateUtil.stringToDate(startTime);
					//2分钟之内的话，将禁止再次发送验证码
					if(DateUtil.isInMinutes(start, new Date(), 2)){
						return DateUtil.leftSeconds(start, new Date());
					}
				}
			}
		}
		return 0;
	}

	/**
	 * 校验code是否合法（校验成功会自动删除key,即只允许校验一次）
	 @param mobilePhone
	 * @param code
	 * @param type
	 * @return
	 * @throws ClientException
	 */
	public boolean check(String mobilePhone, String code, String type) throws NoticeException {
		String key = getKey(mobilePhone, type);
		List<String> list = RedisUtil.getInstance().getMap(key, "validationCode", "createTime", "endTime");
		if(list.size() > 0){
			//校验验证码是否正确
			if(StringUtil.isNotNull(list.get(0)) && list.get(0).equalsIgnoreCase(code)){
				String endTime = list.get(2);
				//校验验证码是否过期
				if(StringUtil.isNotNull(endTime)){
					Date create = new Date();
					Date end = DateUtil.stringToDate(endTime);
					//没有过期
					if(create.before(end)){
						RedisUtil.getInstance().delete(key);
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 公共提取方法获取key
	 * @param mobilePhone
	 * @param type
	 * @return
	 */
	private String getKey(String mobilePhone, String type) throws NoticeException {
		String key = null;
		if(EnumUtil.NoticeSMSType.ADD_FRIEND.value.equals(type)
				|| EnumUtil.NoticeSMSType.AGREE_FRIEND.value.equals(type)){

		}else if(EnumUtil.NoticeSMSType.REGISTER_VALIDATION.value.equals(type)){//用户注册验证码
			key = REGISTER_KEY_PREFIX;
		}else if(EnumUtil.NoticeSMSType.LOGIN_VALIDATION.value.equals(type)){//用户登录验证码
			key = LOGIN_KEY_PREFIX;
		}else if(EnumUtil.NoticeSMSType.UPDATE_PSW_VALIDATION.value.equals(type)){//修改密码验证码
			key = UPDATE_PSW_KEY_PREFIX;
		}else if(EnumUtil.NoticeSMSType.UPDATE_INFO_VALIDATION.value.equals(type)){//信息变更验证码
			key = UPDATE_INFO_KEY_PREFIX;
		}else if(EnumUtil.NoticeSMSType.LOGIN_ERROR_VALIDATION.value.equals(type)){//登录异常验证码
			key = LOGIN_ERROR_KEY_PREFIX;
		}else if(EnumUtil.NoticeSMSType.IDENTITY_VALIDATION.value.equals(type)){//身份验证验证码
			key = IDENTITY_KEY_PREFIX;
		}else if(EnumUtil.NoticeSMSType.ACTIVITY_VALIDATION.value.equals(type)){//活动确认验证码
			key = ACTIVITY_KEY_PREFIX;
		}else if(EnumUtil.NoticeSMSType.BIND_PHONE_VALIDATION.value.equals(type)){//活手机号码绑定验证码
			key = BIND_PHONE_KEY_PREFIX;
		}else if(EnumUtil.NoticeSMSType.REMIND_TAKE_MEDICINE.value.equals(type)){//提醒吃药
			key = REMIND_TAKE_MEDICINE_KEY_PREFIX;
		}else if(EnumUtil.NoticeSMSType.AGAIN_REMIND_TAKE_MEDICINE.value.equals(type)){//再次提醒吃药
			key = AGAIN_REMIND_TAKE_MEDICINE_KEY_PREFIX;
		}else{
			throw new NoticeException("未知的短信业务类型");
		}
		return key + mobilePhone;
	}

	/*public static void main(String[] args) throws NoticeException {

		//登录确认验证码
		sendAliDaYu("LeeDane", "{\"code\":\""+125452+"\",\"product\":\""+"系统"+"\",\"item\":\""+1+"\"}", "13763059195", "SMS_4961020");
	}*/
}
