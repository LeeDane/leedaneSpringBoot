package com.cn.leedane.notice.send;

import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.SMS;
import com.cn.leedane.notice.send.sms.*;
import org.springframework.stereotype.Component;

/**
 * @author LeeDane
 * 2020年05月17日 21:07
 * Version 1.0
 */
@Component
public class SmsNotice extends BaseNotice<SMS>{

    private BaseSmsNotice baseSmsNotice;


    /**
     * 所有短信信息的发送
     * @return
     * @throws NoticeException
     */
    @Override
    public boolean send(SMS sms) throws NoticeException {
        switch (sms.getType()){
            case ADD_FRIEND:
                break;
            case REGISTER_VALIDATION:
                baseSmsNotice = new RegisterValidationSmsNotice(sms);//用户注册验证码
                break;
            case LOGIN_VALIDATION:
                baseSmsNotice = new LoginValidationSmsNotice(sms);//用户登录验证码
                break;
            case UPDATE_PSW_VALIDATION:
                baseSmsNotice = new UpdatePswValidationSmsNotice(sms);//修改密码验证码
                break;
            case UPDATE_INFO_VALIDATION://信息变更验证码
                baseSmsNotice = new UpdateInfoValidationSmsNotice(sms);
                break;
            case LOGIN_ERROR_VALIDATION:
                baseSmsNotice = new LoginErrorValidationSmsNotice(sms);//登录异常验证码
                break;
            case IDENTITY_VALIDATION:
                baseSmsNotice = new IdentityValidationSmsNotice(sms);//身份验证验证码
                break;
            case ACTIVITY_VALIDATION:
                baseSmsNotice = new ActivityValidationSmsNotice(sms);//活动确认验证码
                break;
            case BIND_PHONE_VALIDATION:
                baseSmsNotice = new BindPhoneValidationSmsNotice(sms);//手机号码绑定验证码
                break;
            case REMIND_TAKE_MEDICINE:
                baseSmsNotice = new RemindTakeMedicineSmsNotice(sms);//提醒吃药
                break;
            case AGAIN_REMIND_TAKE_MEDICINE:
                baseSmsNotice = new AgainRemindTakeMedicineSmsNotice(sms);//再次提醒吃药
                break;
            case MY_ATTENTION:
                baseSmsNotice = new MyAttentionSmsNotice(sms);//我的关注
                break;
            case FUTURE_LETTER:
                baseSmsNotice = new FutureLetterSmsNotice(sms);//未来信件
                break;
        }

        return baseSmsNotice.send();
    }

    /*public boolean check(String phone, String code, String type) throws NoticeException {
        //return NoticeUtil.check(baseSmsNotice.getKey(), code);
        return true;
    }*/
}
