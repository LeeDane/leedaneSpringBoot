package com.cn.leedane.message;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import cn.jpush.api.JPushClient;
import cn.jpush.api.common.resp.APIConnectionException;
import cn.jpush.api.common.resp.APIRequestException;
import cn.jpush.api.push.PushResult;
import cn.jpush.api.push.model.Platform;
import cn.jpush.api.push.model.PushPayload;
import cn.jpush.api.push.model.audience.Audience;
import cn.jpush.api.push.model.notification.Notification;

import com.cn.leedane.message.notification.MessageNotification;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.NotificationType;

public class JPushMessageNotificationImpl implements MessageNotification {

	/*private JPushClient jpushClient = new JPushClient(
			ConstantsUtil.JPUSH_MASTER_SECRET, ConstantsUtil.JPUSH_APPKEY, 10);
	private PushPayload payload = null;*/
	
	private Logger logger = Logger.getLogger(getClass());

	@Override
	public boolean sendToAlias(String alias, String content, NotificationType type) {
		long timeLive = 7*24*60*60;
		JPushClient jpushClient = new JPushClient(
				ConstantsUtil.JPUSH_MASTER_SECRET, ConstantsUtil.JPUSH_APPKEY, false, timeLive);
		// For push, all you need do is to build PushPayload object.
		PushPayload payload = buildPushAlias(alias, content, type);
		try {
			PushResult result = jpushClient.sendPush(payload);
			// jpushClient.sendIosNotificationWithRegistrationID(alert, extras,
			// registrationID)
			logger.info("Got result - " + result);
			return result.getResponseCode() == 200;

		} catch (APIConnectionException e) {
			// Connection error, should retry later
			logger.error("Connection error, should retry later");

		} catch (APIRequestException e) {
			// Should review the error, and fix the request
			logger.error("Should review the error, and fix the request");
			logger.error("HTTP Status: " + e.getStatus());
			logger.error("Error Code: " + e.getErrorCode());
			logger.error("Error Message: " + e.getErrorMessage());
		}
		return false;
	}

	@Override
	public boolean sendToTag(String tag, String content) {
		return false;
	}

	@Override
	public boolean sendToAllUser(String content) {
		long timeLive = 7*24*60*60;
		JPushClient jpushClient = new JPushClient(
				ConstantsUtil.JPUSH_MASTER_SECRET, ConstantsUtil.JPUSH_APPKEY, false, timeLive);
		PushPayload payload = buildPushAllUser(content);
		try {
			PushResult result = jpushClient.sendPush(payload);
			logger.info("Got result - " + result);
			return result.getResponseCode() == 200;

		} catch (APIConnectionException e) {
			logger.error("Connection error, should retry later");

		} catch (APIRequestException e) {
			logger.error("Should review the error, and fix the request");
			logger.error("HTTP Status: " + e.getStatus());
			logger.error("Error Code: " + e.getErrorCode());
			logger.error("Error Message: " + e.getErrorMessage());
		}
		return false;
	}
	
	/**
	 * 把通知发送给指定的用户
	 * @param alias
	 * @param content
	 * @param typeW
	 * @return
	 */
	private static PushPayload buildPushAlias(String alias, String content, NotificationType type) {
//        return PushPayload.newBuilder()
//                .setPlatform(Platform.all())
//                .setAudience(Audience.alias(alias))
//                .setNotification(Notification.alert(content))
////                .setMessage(Message.newBuilder().setMsgContent(content).addExtra("mykey", "myvalue").build())
//                .build();
        
        Map<String, String> mp = new HashMap<String, String>();
        mp.put("type", type.value);
		PushPayload load = PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.alias(alias))
                .setNotification(Notification.ios(content, mp))
                .setNotification(Notification.android(content, "新"+ type.value, mp))
                //.setNotification(Notification.android("title", "content", mp).ios("title", mp).winphone("title", mp))
//                .setMessage(Message.newBuilder().setMsgContent(content).addExtra("mykey", "myvalue").build())
                .build();
		return load;
    }
	
	/**
	 * 把通知发送给所有的用户
	 * @param alias
	 * @param content
	 * @return
	 */
	private static PushPayload buildPushAllUser(String content) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setNotification(Notification.alert(content))
                .build();
    }

	@Override
	public boolean sendToRegistrationIDs(Set<String> registrationIds,
			String content) {
		long timeLive = 7*24*60*60;
		JPushClient jpushClient = new JPushClient(
				ConstantsUtil.JPUSH_MASTER_SECRET, ConstantsUtil.JPUSH_APPKEY, false, timeLive);
		// For push, all you need do is to build PushPayload object.
		PushPayload payload = buildPushRegistrationIDs(registrationIds, content);
		try {
			PushResult result = jpushClient.sendPush(payload);
			// jpushClient.sendIosNotificationWithRegistrationID(alert, extras,
			// registrationID)
			logger.info("Got result - " + result);
			return result.getResponseCode() == 200;

		} catch (APIConnectionException e) {
			// Connection error, should retry later
			logger.error("Connection error, should retry later");

		} catch (APIRequestException e) {
			// Should review the error, and fix the request
			logger.error("Should review the error, and fix the request");
			logger.error("HTTP Status: " + e.getStatus());
			logger.error("Error Code: " + e.getErrorCode());
			logger.error("Error Message: " + e.getErrorMessage());
		}
		return false;
	}
	
	/**
	 * 把通知发送给指定的设备
	 * @param registrationIDs
	 * @param content
	 * @return
	 */
	private static PushPayload buildPushRegistrationIDs(Set<String> registrationIDs, String content) {
        return PushPayload.newBuilder()
                .setPlatform(Platform.all())
                .setAudience(Audience.registrationId(registrationIDs))
                .setNotification(Notification.alert(content))
                .build();
    }

}
