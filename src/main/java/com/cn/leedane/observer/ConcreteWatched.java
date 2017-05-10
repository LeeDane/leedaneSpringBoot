package com.cn.leedane.observer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.FriendMapper;
import com.cn.leedane.model.FriendModel;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.observer.template.NotificationTemplate;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;

/**
 * 被观察者实现类
 * @author LeeDane
 * 2016年7月12日 下午1:53:05
 * Version 1.0
 */
@Component
public class ConcreteWatched implements Watched{
	private Logger logger = Logger.getLogger(getClass());
	
	@Resource
	private FriendMapper friendMapper;
	
	public void setFriendMapper(FriendMapper friendMapper) {
		this.friendMapper = friendMapper;
	}
	
	/**
	 * 多个观察者
	 */
	private List<Watcher> mWatchers = new ArrayList<Watcher>();
	
	@Override
	public void notifyWatchers(UserBean watchedBean, NotificationTemplate template) {	
		/**
		 * 通知的多个用户
		 */
		List<FriendModel> friends = new ArrayList<FriendModel>();
		friendMapper = (FriendMapper) SpringUtil.getBean("friendMapper");
		
		String sql = " select from_user_id id, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id =? and status =?"
				+" UNION " 
				+" select to_user_id id, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status =?) else from_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id = ? and status=?";
		List<Map<String, Object>> friendObject = friendMapper.executeSQL(sql, ConstantsUtil.STATUS_NORMAL, watchedBean.getId(), ConstantsUtil.STATUS_NORMAL, ConstantsUtil.STATUS_NORMAL, watchedBean.getId(), ConstantsUtil.STATUS_NORMAL);
		
		if(CollectionUtil.isNotEmpty(friendObject)){
			for(Map<String, Object> friend: friendObject){
				friends.add(new FriendModel(StringUtil.changeObjectToInt(friend.get("id")), StringUtil.changeNotNull(friend.get("remark"))));		
			}
			
			for(Watcher watcher: mWatchers){
				watcher.updateMood(friends, watchedBean, template);
			}
		}else{
			logger.warn(watchedBean.getAccount() +"还没有好友");
		}
	}
	@Override
	public void addWatcher(Watcher watcher) {
		mWatchers.add(watcher);
	}

	@Override
	public void removeWatcher(Watcher watcher) {
		mWatchers.remove(watcher);
	}
	
	

}
