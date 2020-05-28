package com.cn.leedane.notice.send.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.cn.leedane.handler.OptionHandler;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.SMS;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author LeeDane
 * 2020年05月17日 21:15
 * Version 1.0
 */
@Component
public abstract class BaseSmsNotice implements ISmsNotice {
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
    public static String MY_ATTENTION_KEY_PREFIX = "mya_";
    public static String FUTURE_LETTER_KEY_PREFIX = "ftl_";

    protected BaseSmsNotice(){

    }
    protected SMS mSms;
    protected String mSignName; //短信签名
    protected String mTempCode; //模板的编码

    /**
     *
     * @param sms
     * @param signName 短信签名
     * @param tempCode 模板的编码
     * @throws NoticeException
     */
    protected BaseSmsNotice(SMS sms, String signName, String tempCode) throws NoticeException{
        this.mSms = sms;
        this.mSignName = signName;
        this.mTempCode = tempCode;

        //校验必须参数
        String type = EnumUtil.getNoticeSMSTypeValue(sms.getType());
        ParameterUnspecificationUtil.checkNullString(type, "没有短信业务类型");

        //校验是否太快发送验证码
        String phone = sms.getToUser().getMobilePhone();
        ParameterUnspecificationUtil.checkPhone(phone, "phone not incorrect");
        //做校验频繁次数的操作
        checkRequency();
    }

    /**
     * 获取手机号码
     * @return
     */
    protected String getMobilePhone(){
        return mSms.getToUser().getMobilePhone();
    }

    /**
     * 校验接口刷新的频率, 子类可以重写该方法做自己的校验
     * @return
     */
    protected void checkRequency() throws NoticeException {
        String key = getKey();
        List<String> list = RedisUtil.getInstance().getMap(key, "validationCode", "createTime", "endTime");
        int leftSeconds = 0;
        if(list.size() > 0){
            //校验验证码是否正确
            if(StringUtil.isNotNull(list.get(0))){
                String startTime = list.get(1);
                //校验验证码是否过期
                if(StringUtil.isNotNull(startTime)){
                    Date start = DateUtil.stringToDate(startTime);
                    //2分钟之内的话，将禁止再次发送验证码
                    if(DateUtil.isInMinutes(start, new Date(), 2)){
                        leftSeconds = DateUtil.leftSeconds(start, new Date());
                    }
                }
            }
        }
        if(leftSeconds > 0)
            throw new NoticeException("您操作太频繁啦！距离下次操作剩余："+ leftSeconds +"秒");
    }

    /**
     * 发送到阿里大鱼的请求
     * @param tempParams 模板参数(json字符串)
     * @return
     */
    protected boolean sendAliDaYu(String tempParams) throws NoticeException {
        //设置超时时间-可自行调整
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
        //初始化ascClient需要的几个参数
        final String product = "Dysmsapi";//短信API产品名称（短信产品名固定，无需修改）
        final String domain = "dysmsapi.aliyuncs.com";//短信API产品域名（接口地址固定，无需修改）
        //替换成你的AK
        //初始化ascClient,暂时不支持多region（请勿修改）
        OptionHandler optionHandler = (OptionHandler) SpringUtil.getBean("optionHandler");
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
        request.setPhoneNumbers(getMobilePhone());
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(mSignName);
        //必填:短信模板-可在短信控制台中找到，发送国际/港澳台消息时，请使用国际/港澳台短信模版
        request.setTemplateCode(mTempCode);
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
            logger.info("给"+ getMobilePhone() +"发送"+ mTempCode +"短信返回成功");
            return true;
        }else{
            //给管理员发送异常信息
            if(OptionUtil.adminUser != null && OptionUtil.adminUser.getId() > 0)
                NoticeUtil.notification(OptionUtil.adminUser.getId(), OptionUtil.adminUser.getId(), "发送短信异常, 异常信息："+ sendSmsResponse.getMessage()+", 发送数据="+ mSms.toString());
        }
        throw new NoticeException("aliyun sms send result error");
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
        String key = getKey();
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
}
