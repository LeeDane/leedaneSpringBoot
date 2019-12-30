package com.cn.leedane.notice.send;

import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.model.EmailBean;
import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.Notification;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.EmailUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

/**
 * 站内信息通知发送
 * @author LeeDane
 * 2019年12月24日 10:00
 * Version 1.0
 */
public class NotificationNotice extends BaseNotice<Notification> {
    /**
     * 目前支持MoodBean, BlogBean等，可以为空
     */
   // private Object objectBean;

    @Override
    public boolean send(Notification e) throws NoticeException {
        NotificationHandler handler = (NotificationHandler)SpringUtil.getBean("notificationHandler");
        Set<Long> ids = new HashSet<>();
        if(ids == null && e.getToUserId() == 0L)
            throw new NoticeException("通知没有接收者");

        ids = new HashSet<>();
        ids.add(e.getToUserId());
        handler.sendNotificationByIds(e.isNotifyMySelf(), e.getFromUserId(), ids, e.getContent(), e.getType(), e.getTableName(), e.getTableId(), e.getObjectBean());
        return true;
    }
}
