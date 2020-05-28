package com.cn.leedane.utils;

import com.cn.leedane.model.UserBean;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.Email;
import com.cn.leedane.notice.model.Notification;
import com.cn.leedane.notice.send.INoticeFactory;
import com.cn.leedane.notice.send.NoticeFactory;
import com.cn.leedane.redis.util.RedisUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 消息的工具类
 * @author LeeDane
 * 2020年05月16日 02:33
 * Version 1.0
 */
public class NoticeUtil {

    /**
     * 发送站内信
     * @param fromUserId
     * @param toUserId
     * @param content
     * @return
     */
    public static boolean notification(long fromUserId, long toUserId, String content){
        try{
            Notification notification = new Notification();
            notification.setContent(content);
            notification.setFromUserId(fromUserId);
            notification.setToUserId(toUserId);
            notification.setType(EnumUtil.NotificationType.通知);
            INoticeFactory factory = new NoticeFactory();
            return factory.create(EnumUtil.NoticeType.站内信).send(notification);
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }
    /**
     * 发送邮件
     * @param toUserEmail
     * @param subject
     * @param content
     * @return
     */
    public static boolean email(String toUserEmail, String subject, String content){
        return email(toUserEmail, subject, content, false, 0,null);
    }

    /**
     * 发送邮件
     * @param toUserEmail
     * @param subject
     * @param content
     * @param errorNotification 异常后是否发送站内信通知
     * @param toUserId
     * @param errorMsg
     * @return
     */

    public static boolean email(String toUserEmail, String subject, String content, boolean errorNotification, long toUserId,  String errorMsg){
        try{
            INoticeFactory factory = new NoticeFactory();
            Email emailBean = new Email();
            emailBean.setContent(content);
            //emailBean.setFrom(user);
            emailBean.setSubject(subject);
            UserBean toUser = new UserBean();
            toUser.setEmail(toUserEmail);
            Set<UserBean> replyTo = new HashSet<>();
            replyTo.add(toUser);
            emailBean.setReplyTo(replyTo);
            return factory.create(EnumUtil.NoticeType.邮件).send(emailBean);
        }catch (Exception e){
            if(errorNotification){
                notification(OptionUtil.adminUser.getId(), toUserId, errorMsg + "， 异常信息："+ e.getMessage());
            }
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 校验code是否合法（校验成功会自动删除key,即只允许校验一次）
     @param mobilePhone
      * @param code
     * @param type
     * @return
     * @throws NoticeException
     */
    public static boolean check(String mobilePhone, String code, String type) throws NoticeException {
        String key = null;
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
