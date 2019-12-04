package com.cn.leedane.observer;

import java.util.List;

import com.cn.leedane.model.FriendModel;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.observer.template.NotificationTemplate;

/**
 * 观察者
 * @author LeeDane
 * 2016年7月12日 下午1:53:29
 * Version 1.0
 */
public interface Watcher {

	/**
	 * 执行更新
	 * @param friends
	 * @param watchedBean
	 * @return
	 */
	boolean updateMood(List<FriendModel> friends, UserBean watchedBean, NotificationTemplate template);
	
	/**
	 * 获取错误的对象
	 * @return
	 */
	List<FriendModel> getError();
}
