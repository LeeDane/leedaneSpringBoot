package com.cn.leedane.chat_room;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONObject;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Component;

import com.cn.leedane.controller.UserController;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.StringUtil;

@ServerEndpoint(value = "/scanLogin")
@Component
public class ScanLoginWebSocket {
    //concurrent包的线程安全Set，用来存放每个客户端对应的scanLoginWebSocket对象。
    public static ConcurrentHashMap<String, ScanLoginWebSocket> scanLoginSocket = new ConcurrentHashMap<String, ScanLoginWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
    
    private String cnid;
    
    public Session getSession() {
		return session;
	}

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        cnid = StringUtil.buildUUID("scan");
        scanLoginSocket.put(cnid, this);
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("message", cnid);
		map.put("isSuccess", true);
		sendMessage(JSONObject.fromObject(map).toString());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session) {
    	scanLoginSocket.remove(this);  //从set中删除
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
    }

    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("发生错误");
        error.printStackTrace();
    }

    public void sendMessage(String message){
    	//这个是同步，线程阻塞
        //this.session.getBasicRemote().sendText(message);
        this.session.getAsyncRemote().sendText(message);
    }
}