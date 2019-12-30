package com.cn.leedane.notice.send;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * @author LeeDane
 * 2019年12月24日 10:00
 * Version 1.0
 */
public abstract class BaseNotice<T extends IDBean> implements INotice<T> {
    /*protected UserBean fromUser; //发送者
    protected UserBean toUser; //接收者
    protected String content;//消息的内容

    public UserBean getFromUser() {
        return fromUser;
    }

    public void setFromUser(UserBean fromUser) {
        this.fromUser = fromUser;
    }

    public UserBean getToUser() {
        return toUser;
    }

    public void setToUser(UserBean toUser) {
        this.toUser = toUser;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }*/
}
