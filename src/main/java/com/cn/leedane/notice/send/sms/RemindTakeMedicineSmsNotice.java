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
 * 提醒吃药
 * @author LeeDane
 * 2020年05月17日 21:08
 * Version 1.0
 */
public class RemindTakeMedicineSmsNotice extends BaseSmsNotice {
    private Logger logger = Logger.getLogger(getClass());

    public RemindTakeMedicineSmsNotice(SMS sms) throws NoticeException {
        super(sms, "LeeDane", "SMS_190281153");
    }

    @Override
    public void checkRequency() throws NoticeException {
        super.checkRequency();
    }

    @Override
    public String getKey() throws NoticeException {
        return REMIND_TAKE_MEDICINE_KEY_PREFIX + getMobilePhone();
    }

    @Override
    public boolean send() throws NoticeException {
        String validationCode = StringUtil.build6ValidationCode();
        Map<String, String> map = new HashMap<String, String>();
        map.put("validationCode", validationCode);
        Date createTime = new Date();
        map.put("createTime", DateUtil.DateToString(createTime));
        try {
            map.put("endTime", DateUtil.DateToString(DateUtil.getOverdueTime(createTime, "10分钟")));
        } catch (ErrorException e) {
            e.printStackTrace();
        }
        RedisUtil.getInstance().addMap(getKey(), map);
        RedisUtil.getInstance().expire(getKey(), mSms.getExpire());
//		logger.info(sendAliDaYu("LeeDane", "{\"name\":\""+ sms.getToUser().getChinaName()+"\",\"content\":\""+DateUtil.getChineseDateFormat() + "好"+"\"}", mobilePhone, "SMS_190266535"));
        logger.info(sendAliDaYu("{\"content\":\""+DateUtil.getChineseDateFormat() + "好"+"\"}"));
        return true;
    }
}
