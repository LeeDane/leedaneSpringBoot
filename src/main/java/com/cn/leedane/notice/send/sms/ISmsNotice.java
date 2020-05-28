package com.cn.leedane.notice.send.sms;

import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.SMS;

/**
 * @author LeeDane
 * 2020年05月17日 21:09
 * Version 1.0
 */
public interface ISmsNotice {

    /**
     * 获取每个短信模板的key
     */
    public String getKey() throws NoticeException;

    /**
     * 公共发送短信方法之前的初始化参数
     */
//    public void beforeSend() throws NoticeException;

    /**
     * 公共发送短信方法
     */
    public boolean send() throws NoticeException;
}
