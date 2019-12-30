package com.cn.leedane.notice.send;

import com.cn.leedane.model.EmailBean;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.EnumUtil;

/**
 * 通知工厂类接口
 * @author LeeDane
 * 2019年12月24日 09:59
 * Version 1.0
 */
public class NoticeFactory implements INoticeFactory{


    @Override
    public INotice create(EnumUtil.NoticeType noticeType) {
        if(noticeType == EnumUtil.NoticeType.邮件)
            return (EmailNotice)SpringUtil.getBean("emailNotice");
        if(noticeType == EnumUtil.NoticeType.站内信)
            return new NotificationNotice();
        if(noticeType == EnumUtil.NoticeType.短信)
            return (SmsNotice)SpringUtil.getBean("smsNotice");
        return null;
    }
}
