package com.cn.leedane.comet4j;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.comet4j.core.CometConnection;
import org.comet4j.core.CometContext;
import org.comet4j.core.CometEngine;
import org.comet4j.core.event.ConnectEvent;
import org.comet4j.core.listener.ConnectListener;


public class Comet4jServer extends ConnectListener implements ServletContextListener{
	
	public static final String SCAN_LOGIN = "scan_login"; //扫码登录
	public static final String CHAT_ROOM = "chat_room"; //聊天室
	
    public void contextInitialized(ServletContextEvent event) {  
    	System.out.println("执行初始化comet4j");
        CometContext cc = CometContext.getInstance();  
        cc.registChannel(SCAN_LOGIN);//注册应用的channel
        cc.registChannel(CHAT_ROOM);//注册应用的channel
        
        //添加监听
        CometEngine engine = CometContext.getInstance().getEngine();  
        engine.addConnectListener(this);
        
        /*Thread helloAppModule = new Thread(new HelloAppModule(), "Sender App Module");  
        helloAppModule.setDaemon(true);  
        helloAppModule.start();  */

    } 

    public void contextDestroyed(ServletContextEvent event) {  
    	System.out.println("事件销毁：contextDestroyed");
    }

	@Override
	public boolean handleEvent(ConnectEvent event) {
		CometConnection conn = event.getConn();
		HttpServletRequest request = conn.getRequest();
		String channel = request.getParameter("channel");//获取频道
		
		String cid = conn.getId(); //唯一标记符号，由页面传递
		if(SCAN_LOGIN.equals(channel)){
			HttpSession session = request.getSession();
			AppStore.getInstance().putScanLogin(cid, session);
		}else if(CHAT_ROOM.equals(channel)){
			AppStore.getInstance().addChat(cid);
		}
		System.out.println("新的页面打开，调用handleEvent:"+event.getConn().getId()+"-链接数："+AppStore.getInstance().getScanLogin().size());
		return true;
	} 
}
