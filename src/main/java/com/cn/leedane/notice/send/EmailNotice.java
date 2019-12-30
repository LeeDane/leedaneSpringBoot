package com.cn.leedane.notice.send;

import com.cn.leedane.model.EmailBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.Email;
import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.utils.EmailUtil;
import com.cn.leedane.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

/**
 * 邮件通知发送
 * @author LeeDane
 * 2019年12月24日 10:00
 * Version 1.0
 */
@Component
public class EmailNotice extends BaseNotice<Email> {
    @Autowired
    private EmailUtil emailUtil;
    @Override
    public boolean send(Email e) throws NoticeException {
        if(e.getFrom() != null && StringUtil.isNull(e.getFrom().getEmail()))
            throw new NoticeException("发送者还没有绑定邮箱");

        for(UserBean toUser: e.getReplyTo()){
            if(StringUtil.isNull(toUser.getEmail()))
                throw new NoticeException("有接收者还没有绑定邮箱");
        }

        if(StringUtil.isNull(e.getContent()))
            throw new NoticeException("content must not null.");
        emailUtil.initData(e.getFrom(), e.getReplyTo(), e.getContent(), e.getSubject());
        try {
            emailUtil.sendMore();
        } catch (MessagingException e1) {
            e1.printStackTrace();
            throw new NoticeException("发送邮箱 MessagingException");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            throw new NoticeException("发送邮箱 UnsupportedEncodingException");
        }
        return true;
    }
}
