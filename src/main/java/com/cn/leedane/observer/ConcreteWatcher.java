package com.cn.leedane.observer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.cn.leedane.model.FriendModel;
import com.cn.leedane.model.NotificationBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.observer.template.NotificationTemplate;
import com.cn.leedane.service.NotificationService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.DateUtil;

/**
 * 观察者实现类
 * @author LeeDane
 * 2016年7月12日 下午1:53:15
 * Version 1.0
 */
public class ConcreteWatcher implements Watcher{
	
	private Logger logger = Logger.getLogger(getClass());
	
	private NotificationService<NotificationBean> notificationService;
	/**
	 * 记录错误的对象
	 */
	private List<FriendModel> errorList = new ArrayList<FriendModel>();

	@Override
	public boolean updateMood(List<FriendModel> friends, UserBean watchedBean, NotificationTemplate template){
		
		boolean result = true;
		int size = friends.size();
		ExecutorService executorService = null;
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		if(size > 5){
			executorService = Executors.newFixedThreadPool(5);
		}else{
			executorService = Executors.newScheduledThreadPool(size);
		}
		
		NotificationBean notificationBean;
		String content;
		SingleSendNotification notification;
		for (int i = 0; i < size; i++) {
			logger.info(friends.get(i).getId()+",你的关注的人"+friends.get(i).getRemark()+"更新了信息");
			notificationBean = new NotificationBean();
			notificationBean.setFromUserId(watchedBean.getId());
			content = template.getNotifitionContent();
			if(content.contains("{from_user_id}")){
				content = content.replaceAll("\\{from_user_id\\}", String.valueOf(watchedBean.getId()));
			}
			if(content.contains("{to_user_id}")){
				content = content.replaceAll("\\{to_user_id\\}", String.valueOf(friends.get(i).getId()));
			}
			
			if(content.contains("{from_user_remark}")){
				content = content.replaceAll("\\{from_user_remark\\}", watchedBean.getAccount());
			}
			if(content.contains("{to_user_remark}")){
				content = content.replaceAll("\\{to_user_remark\\}", friends.get(i).getRemark());
			}
			
			notificationBean.setContent("您的好友"+ friends.get(i).getRemark() + "更新了说说");
			notificationBean.setCreateTime(DateUtil.DateToString(new Date()));
			notificationBean.setToUserId(friends.get(i).getId());
			notificationBean.setType(template.getNotifitionType().value);
			notification = new SingleSendNotification(notificationBean);
			futures.add(executorService.submit(notification));
			
		}
		executorService.shutdown();
		
		for(int i =0; i < futures.size(); i++){
			try {
				Boolean b = futures.get(i).get();
				if(b != true){
					logger.info("发送给"+ friends.get(i).getId()+"的通知失败");
					errorList.add(friends.get(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
				futures.get(i).cancel(true);
			}
		}
		
		return result;
	}

	@Override
	public List<FriendModel> getError() {
		return errorList;
	}
	/**
	 * 单个发送消息通知的类
	 * @author LeeDane
	 * 2015年11月30日 下午2:40:05
	 * Version 1.0
	 */
	class SingleSendNotification implements Callable<Boolean>{
		private NotificationBean mNotificationBean;
		
		@SuppressWarnings("unchecked")
		SingleSendNotification(NotificationBean notificationBean){
			mNotificationBean = notificationBean;
			if(notificationService == null){
				notificationService = (NotificationService<NotificationBean>) SpringUtil.getBean("notificationService");
			}
		}

		@Override
		public Boolean call() throws Exception {
			return notificationService.save(mNotificationBean);
		} 
		
	}

}
