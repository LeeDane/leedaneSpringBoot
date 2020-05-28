package com.cn.leedane.notice.send.sms;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.SMS;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 未来短信通知
 * @author LeeDane
 * 2020年05月24日 下午18:44：10
 * Version 1.0
 */
public class FutureLetterSmsNotice extends BaseSmsNotice {
    private Logger logger = Logger.getLogger(getClass());

    public FutureLetterSmsNotice(SMS sms) throws NoticeException {
        super(sms, "LeeDane", "SMS_124400087");
    }

    @Override
    public void checkRequency() throws NoticeException {
        super.checkRequency();
    }

    @Override
    public String getKey() throws NoticeException {
        return FUTURE_LETTER_KEY_PREFIX + getMobilePhone();
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
        logger.info(sendAliDaYu("{\"name\":\""+ mSms.getParams().get("content")+"\", \"link\": \""+ mSms.getParams().get("link") +"\"}"));
        return true;
    }
}
