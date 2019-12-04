package com.cn.leedane.test;

import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.cn.leedane.message.JPushMessageNotificationImpl;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.MessageNotification;
import com.cn.leedane.model.ChatBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.NotificationType;

/**
 * 极光推送相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:11:34
 * Version 1.0
 */
public class JPushTest extends BaseTest{

	//public static void main(String[] args) {
		/*JPushClient jpushClient = new JPushClient(ConstantsUtil.JPUSH_MASTER_SECRET, ConstantsUtil.JPUSH_APPKEY, 10);
		
		// For push, all you need do is to build PushPayload object.
        PushPayload payload = buildPushObject_all_all_alert();

        try {
            PushResult result = jpushClient.sendPush(payload);
            //jpushClient.sendIosNotificationWithRegistrationID(alert, extras, registrationID)
            logger.info("Got result - " + result);

        } catch (APIConnectionException e) {
            // Connection error, should retry later
        	logger.error("Connection error, should retry later");

        } catch (APIRequestException e) {
            // Should review the error, and fix the request
        	logger.error("Should review the error, and fix the request");
            logger.error("HTTP Status: " + e.getStatus());
            logger.error("Error Code: " + e.getErrorCode());
            logger.error("Error Message: " + e.getErrorMessage());
        }*/
		/*MessageNotification messageNotification = new JPushMessageNotificationImpl();
		messageNotification.sendToAlias("leedane_user_"+1, "你好。。 server3");*/
		/*Notification notification = new Notification();
		notification.setToUser(toUser);
		
		
		JpushCustomMessage message= new JpushCustomMessage();
		logger.info(message.sendToAlias("leedane_user_1", "我是测试号", "toUserId", "2"));*/
	//}
	
	@Resource
	private UserService<UserBean> userService;
	
	/**
	 * 测试通知
	 */
	@Test
	public void notification() {
		MessageNotification message= new JPushMessageNotificationImpl();
		logger.info(message.sendToAlias("leedane_user_"+1, "hello leedane", NotificationType.全部));
	}
	
	/**
	 * 测试发送聊天
	 */
	@Test
	public void chat() {
		UserBean user = userService.findById(1);
		if(user == null){
			user = new UserBean();
			user.setId(11);
		}
		ChatBean chatBean = new ChatBean();
		chatBean.setId(1107);
		chatBean.setContent("测试内容1");
		chatBean.setCreateTime(new Date());
		chatBean.setCreateUserId(user.getId());
		chatBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		chatBean.setToUserId(1);
		chatBean.setType(1);
		
		Map<String, Object> chatMap = null;
		JpushCustomMessage message= new JpushCustomMessage();
		logger.info(message.sendToAlias("leedane_user_1", JSONObject.fromObject(chatMap).toString(), EnumUtil.CustomMessageExtraType.聊天.value));
		
		
	}
	
}
