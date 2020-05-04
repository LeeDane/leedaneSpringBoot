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

import com.cn.leedane.redis.config.LeedanePropertiesConfig;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.model.ChatSquareBean;
import com.cn.leedane.model.ScoreBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.ChatSquareService;
import com.cn.leedane.service.ScoreService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;

@ServerEndpoint(value = "/websocket")
@Component
public class ChatWebSocket {
	private Logger logger = Logger.getLogger(getClass());
	
    //静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的ChatWebSocket对象。
    private static CopyOnWriteArraySet<ChatWebSocket> chatSocketSet = new CopyOnWriteArraySet<ChatWebSocket>();
    
  //concurrent包的线程安全Set，用来存放每个客户端对应的scanLoginWebSocket对象。
    private static CopyOnWriteArraySet<ChatWebSocket> scanLoginSocketSet = new CopyOnWriteArraySet<ChatWebSocket>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        
        String queryStr = session.getQueryString();
        if(StringUtil.isNotNull(queryStr) && "scanLogin".equals(queryStr)){
        	scanLoginSocketSet.add(this);
        }else{
        	chatSocketSet.add(this); 
        	//加入set中
            addOnlineCount();           //在线数加1
            logger.info("有新连接加入！当前在线人数为" + getOnlineCount());
        }
        /*try {
            sendMessage("hello");
        } catch (IOException e) {
            logger.error("IO异常");
        }*/
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
    	if(chatSocketSet.remove(this)){//从set中删除
    		subOnlineCount();           //在线数减1
    		logger.info("有一连接关闭！当前在线人数为" + getOnlineCount());
    	}else{
    		scanLoginSocketSet.remove(this);  //从set中删除
    	}
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息*/
    @OnMessage
    public void onMessage(String message, Session session) {
    	logger.info("来自客户端的消息:" + message);
        try {
            JSONObject jsonObject = JSONObject.fromObject(message);
        	jsonObject.put("time", DateUtil.DateToString(new Date()));
        	UserHandler userHandler = (UserHandler) SpringUtil.getBean("userHandler");
        	final int userId = StringUtil.changeObjectToInt(jsonObject.getString("id"));
        	Map<String, Object> userInfo = new HashMap<String, Object>();
        	String content = jsonObject.optString("content");
        	//非访客身份将不进行进一步的操作
        	if(userId < 1){
        		jsonObject.put("account", "匿名用户");
        		jsonObject.put("create_user_id", userId);
        		jsonObject.put("user_pic_path", LeedanePropertiesConfig.newInstance().getString("constant.default.no.pic.path"));
        		Set<String> sensitiveWords = FilterUtil.getFilter(content);
            	//进行敏感词过滤和emoji过滤
        		if(sensitiveWords != null && sensitiveWords.size() > 0){
        			jsonObject.put("type", "error");
                	StringBuffer words = new StringBuffer();
                	words.append("您发送的内容\"");
                	words.append(content);
                	words.append("\"");
                	words.append("中【");
                	for(String sensitiveWord: sensitiveWords){
                		words.append(sensitiveWord + "、");
                	}
                	words.deleteCharAt(words.length() -1);
                	words.append("】等是敏感词，请核实！");
                	jsonObject.put("content", words.toString());
                	sendMessageToSelf(jsonObject, userId);
					//发送敏感信息日志
					FilterUtil.sendOperateLogByUser(null, content, sensitiveWords);
        		}else{
        			sendMessageToAll(false, jsonObject, userId);
        		}
        		return;
        	}	

        	//登录用户的统一处理
    		userInfo = userHandler.getBaseUserInfo(userId);
    		jsonObject.put("user_pic_path", userInfo.get("user_pic_path"));
        	jsonObject.put("account", userInfo.get("account"));
        	
        	jsonObject.put("create_user_id", userId);
        	String type = null;
        	if(jsonObject.has("type")){
        		type = jsonObject.getString("type");
        	}
        	
        	Set<String> sensitiveWords = FilterUtil.getFilter(content);
        	//进行敏感词过滤和emoji过滤
    		if(sensitiveWords != null && sensitiveWords.size() > 0){
    			type = "error";
            	jsonObject.put("type", type);
            	StringBuffer words = new StringBuffer();
            	words.append("您发送的内容\"");
            	words.append(content);
            	words.append("\"");
            	words.append("中【");
            	for(String sensitiveWord: sensitiveWords){
            		words.append(sensitiveWord + "、");
            	}
            	words.deleteCharAt(words.length() -1);
            	words.append("】等是敏感词，请核实！");
            	jsonObject.put("content", words.toString());
            	sendMessageToSelf(jsonObject, userId);
				//发送敏感信息日志
				FilterUtil.sendOperateLogByUser(userHandler.getUserBean(userId), content, sensitiveWords);
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
        		sendMessageToAll((StringUtil.isNull(type) || !"welcome".equals(type)), jsonObject, userId);
        	}
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    }
    
    /**
     * 给自己发信息
     * @param jsonObject
	 * @param userId
     */
    public void sendMessageToSelf(JSONObject jsonObject, int userId){
    	this.session.getAsyncRemote().sendText(jsonObject.toString());
    }
    
    /**
     * 是否保存该操作记录
     * @param saveRecord
     * @param jsonObject
     * @param userId
     */
    public void sendMessageToAll(boolean saveRecord, JSONObject jsonObject, int userId){    	
    	if(saveRecord){
    		saveRecore(userId, jsonObject);
    	}
    	//群发消息
        for (ChatWebSocket item : chatSocketSet) {
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
    	UserHandler userHandler = (UserHandler) SpringUtil.getBean("userHandler");
		int score = scoreService.getTotalScore(userId);
		if(score < 1){
			jsonObject.put("content", "积分不足，无法发送弹屏！");
			jsonObject.put("type", "error");
			sendMessageToSelf(jsonObject, userId);
			return;
		}else{
			//扣除积分
			ResponseModel model = scoreService.reduceScore(1, "发弹屏扣积分", "", 0, userHandler.getUserBean(userId));
			if(model.isSuccess()){
				jsonObject.put("screenText", screenText); //弹屏内容，纯文本
				sendMessageToAll((StringUtil.isNull(type) || !"welcome".equals(type)), jsonObject, userId);
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
    	logger.info("发生错误");
        error.printStackTrace();
    }


    public void sendMessage(String message) throws IOException {
    	//这个是同步，线程阻塞
        this.session.getBasicRemote().sendText(message);
        //this.session.getAsyncRemote().sendText(message);
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