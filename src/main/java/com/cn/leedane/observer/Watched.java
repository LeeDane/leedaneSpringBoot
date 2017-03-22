package com.cn.leedane.observer;

import com.cn.leedane.model.UserBean;
import com.cn.leedane.observer.template.NotificationTemplate;

/**
 * 被观察者
 * @author LeeDane
 * 2016年7月12日 下午1:53:21
 * Version 1.0
 */
public interface Watched {
	/**
	 * 通知观察者
	 * @param str
	 */
	public void notifyWatchers(UserBean watchedBean, NotificationTemplate template);
	
	/**
	 * 获得观察者的实体bean对象
	 * @return
	 */
	//public UserBean getWatchedBean();
	
	/**
	 * 添加观察者
	 * @param watcher
	 */
	public void addWatcher(Watcher watcher);

	/**
	 * 删除观察者
	 * @param watcher
	 */
    public void removeWatcher(Watcher watcher);

}
