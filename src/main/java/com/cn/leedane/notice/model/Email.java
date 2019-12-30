package com.cn.leedane.notice.model;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

import java.util.Set;

/**
 * @author LeeDane
 * 2019年12月24日 12:25
 * Version 1.0
 */
public class Email extends IDBean {
    /**
     * 邮件来自(发件人)
     */
    private UserBean from;

    /**
     * 邮件标题
     */
    private String subject;

    /**
     * 收件人的列表,一对多的关系
     */
    private Set<UserBean> replyTo;

    /**
     * 邮件内容
     */
    private String content;

    public UserBean getFrom() {
        return from;
    }

    public void setFrom(UserBean from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Set<UserBean> getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Set<UserBean> replyTo) {
        this.replyTo = replyTo;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
