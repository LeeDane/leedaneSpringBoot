package com.cn.leedane.notice.send;

import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.utils.EnumUtil;

/**
 * 通知工厂类接口
 * @author LeeDane
 * 2019年12月24日 09:59
 * Version 1.0
 */
public interface INoticeFactory<T> {

    /**
     * 通知的发送
     * @param noticeType
     */
    public INotice create(EnumUtil.NoticeType noticeType);
}
