package com.cn.leedane.chat_room;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.model.ChatSquareBean;
import com.cn.leedane.model.ScoreBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.ChatSquareService;
import com.cn.leedane.service.ScoreService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.FilterUtil;
import com.cn.leedane.utils.JsoupUtil;
import com.cn.leedane.utils.StringUtil;

@ServerEndpoint(value = "/websocket")
@Component
public class ChatWebSocket {
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的ChatWebSocket对象。
    private static CopyOnWriteArraySet<ChatWebSocket> webSocketSet = new CopyOnWriteArraySet<ChatWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        webSocketSet.add(this);     //加入set中
        addOnlineCount();           //在线数加1
        System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
        /*try {
            sendMessage("hello");
        } catch (IOException e) {
            System.out.println("IO异常");
        }*/
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);  //从set中删除
        subOnlineCount();           //在线数减1
        System.out.println("有一连接关闭！当前在线人数为" + getOnlineCount());
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("来自客户端的消息:" + message);
        try {
            JSONObject jsonObject = JSONObject.fromObject(message);
        	jsonObject.put("time", DateUtil.DateToString(new Date()));
        	UserHandler userHandler = (UserHandler) SpringUtil.getBean("userHandler");
        	String keyString = jsonObject.getString("id");
        	final int userId = StringUtil.changeObjectToInt(keyString.split("UNION")[0]);
        	Map<String, Object> userInfo = new HashMap<String, Object>();
        	
        	//非访客身份将不进行进一步的操作
        	if(userId == Integer.parseInt(Util.VISITORS_ID))
        		return;
        	
    		userInfo = userHandler.getBaseUserInfo(userId);
    		jsonObject.put("user_pic_path", userInfo.get("user_pic_path"));
        	jsonObject.put("account", userInfo.get("account"));
        	
        	jsonObject.put("id", userId);
        	String type = null;
        	if(jsonObject.has("type")){
        		type = jsonObject.getString("type");
        	}
        	
        	Set<String> sensitiveWords = FilterUtil.getFilter(jsonObject.getString("content"));
        	//进行敏感词过滤和emoji过滤
    		if(sensitiveWords != null && sensitiveWords.size() > 0){
    			type = "error";
            	jsonObject.put("type", type);
            	StringBuffer words = new StringBuffer();
            	words.append("您发送的内容有【");
            	for(String sensitiveWord: sensitiveWords){
            		words.append(sensitiveWord + "、");
            	}
            	words.deleteCharAt(words.length() -1);
            	words.append("】等敏感词，请核实！");
            	jsonObject.put("content", words.toString());
            	sendMessageToSelf(jsonObject, userId);
            	return;
    		}
        	
        	//type = "error";
        	//jsonObject.put("type", type);
        	
        	//弹屏类型需要扣积分
        	if(StringUtil.isNotNull(type) && "PlayScreen".equals(type)){
        		try {
					dealPlayScreen(type, jsonObject, userId);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
        	}else{
        		sendMessageToAll(type, jsonObject, userId);
        	}
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    
    /**
     * 给自己发信息
     * @param message
     */
    public void sendMessageToSelf(JSONObject jsonObject, int userId){
    	this.session.getAsyncRemote().sendText(jsonObject.toString());
    }
    
    /**
     * 给所有人发信息
     * @param message
     */
    public void sendMessageToAll(String type, JSONObject jsonObject, int userId){
    	boolean welcome = StringUtil.isNotNull(type) && "welcome".equals(type);
    	
    	if(!welcome){
    		saveRecore(userId, jsonObject);
    	}
    	//群发消息
        for (ChatWebSocket item : webSocketSet) {
            try {
                
              //如果是欢迎并且是自己就不发送了
//        		if(welcome && userId == StringUtil.changeObjectToInt(inbound.getId().split("UNION")[0])){
//            		continue;
//            	}
            	
                item.sendMessage(jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 处理弹屏
     * @param jsonObject
     * @param userId
     * @throws IOException 
     * @throws ClassNotFoundException 
     */
    @SuppressWarnings("unchecked")
	private void dealPlayScreen(String type, JSONObject jsonObject, int userId) throws IOException, ClassNotFoundException{
    	//获取文字
		String screenText = JsoupUtil.getInstance().getContentNoTag(jsonObject.getString("content"));
		if(StringUtil.isNotNull(screenText) && screenText.length() > 30){
			jsonObject.put("content", "抱歉，超过30个文字，无法使用弹屏功能！");
			jsonObject.put("type", "error");
			sendMessageToSelf(jsonObject, userId);
			return;
		}
		
    	ScoreService<ScoreBean> scoreService = (ScoreService<ScoreBean>) SpringUtil.getBean("scoreService");
    	UserService<UserBean> userService = (UserService<UserBean>) SpringUtil.getBean("userService");
		int score = scoreService.getTotalScore(userId);
		if(score < 1){
			jsonObject.put("content", "积分不足，无法发送弹屏！");
			jsonObject.put("type", "error");
			sendMessageToSelf(jsonObject, userId);
			return;
		}else{
			//扣除积分
			Map<String, Object> map= scoreService.reduceScore(1, "发弹屏扣积分", "", 0, userService.findById(userId));
			if(StringUtil.changeObjectToBoolean(map.get("isSuccess"))){
				
				jsonObject.put("screenText", screenText); //弹屏内容，纯文本
				sendMessageToAll(type, jsonObject, userId);
				return;
			}else{
				jsonObject.put("content", "扣除积分失败");
				jsonObject.put("type", "error");
				sendMessageToSelf(jsonObject, userId);
				return;
			}
			
			
		}
    }
    
    /**
     * 保存记录
     * @param userId
     * @param jsonObject
     */
    private void saveRecore(final int userId, final JSONObject jsonObject){
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				@SuppressWarnings("unchecked")
				ChatSquareService<ChatSquareBean> chatSquareService = (ChatSquareService<ChatSquareBean>) SpringUtil.getBean("chatSquareService");
				chatSquareService.addChatSquare(userId, jsonObject.toString());
			}
		}).start();
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


    public void sendMessage(String message) throws IOException {
    	//这个是同步，线程阻塞
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
    }


    /**
     * 群发自定义消息
     * */
    public static void sendInfo(String message) throws IOException {
        for (ChatWebSocket item : webSocketSet) {
            try {
                item.sendMessage(message);
            } catch (IOException e) {
                continue;
            }
        }
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        ChatWebSocket.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        ChatWebSocket.onlineCount--;
    }
}