package com.cn.leedane.shiro;

import com.cn.leedane.utils.SessionManagerUtil;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

public class ShiroSessionListener implements SessionListener {

    @Override
    public void onStart(Session session) {
        System.out.println("会话创建。。。。。");
    }

    @Override
    public void onStop(Session session) {
        System.out.println("会话停止。。。。。");
//        SessionManagerUtil.getInstance().removeSession(session);
    }

    @Override
    public void onExpiration(Session session) {
        System.out.println("会话onExpiration。。。。。");
        SessionManagerUtil.getInstance().removeSession(session, false);
    }
}
