package com.cn.leedane.notice.send;

import com.cn.leedane.notice.NoticeException;

/**
 * @author LeeDane
 * 2019年12月24日 09:59
 * Version 1.0
 */
public interface INotice<T> {

    /**
     * 通知的发送
     * @param t
     */
    public boolean send(T t) throws NoticeException;
}
