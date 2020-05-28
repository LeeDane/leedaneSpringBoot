package com.cn.leedane.notice.send.sms;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.SMS;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 身份验证验证码
 * @author LeeDane
 * 2020年05月17日 21:08
 * Version 1.0
 */
public class IdentityValidationSmsNotice extends BaseSmsNotice {
    private Logger logger = Logger.getLogger(getClass());

    public IdentityValidationSmsNotice(SMS sms) throws NoticeException {
        super(sms, "身份验证", "SMS_4961021");
    }

    @Override
    public void checkRequency() throws NoticeException {
        super.checkRequency();
    }

    @Override
    public String getKey() throws NoticeException {
        return IDENTITY_KEY_PREFIX + getMobilePhone();
    }

    @Override
    public boolean send() throws NoticeException {
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
        RedisUtil.getInstance().addMap(getKey(), map);
        RedisUtil.getInstance().expire(getKey(), mSms.getExpire());
        logger.info(sendAliDaYu("{\"code\":\""+validationCode+"\",\"product\":\"\"}"));
        return true;
    }
}
