package com.cn.leedane.handler.clock;

import java.util.Date;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.EnumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.model.clock.ClockDynamicBean;
import com.cn.leedane.model.clock.ClockDynamicQueueBean;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.ClockDynamicThread;
import com.cn.leedane.utils.ConstantsUtil;

/**
 * 任务动态的处理类
 * @author LeeDane
 * 2018年11月26日 下午4:23:09
 * version 1.0
 */
@Component
public class ClockDynamicHandler {

	@Autowired
	private UserHandler userHandler;

	/**
	 * 获取用户名(对于自己创建的动态，可以不获取用户名)
	 * @param user
	 * @param userId
	 * @return
	 */
	public String getUserName(UserBean user, long userId){
		String name = "";
		if(userId < 1 || user.getId() == userId)
			return name;
		return userHandler.getUserName(userId);
	}
	/**
	 * 保存动态的信息
	 * @param clockId
	 * @param systemDate
	 * @param userId
	 * @param desc
	 * @param publicity
	 */
	public void saveDynamic(long clockId, Date systemDate, long userId, String desc, boolean publicity, int messageType){
		//设置动态信息
		ClockDynamicQueueBean clockDynamicQueueBean = new ClockDynamicQueueBean();
		clockDynamicQueueBean.setError(0);
		ClockDynamicBean dynamicBean = new ClockDynamicBean();
		dynamicBean.setClockId(clockId);
		dynamicBean.setMessageType(messageType);
		dynamicBean.setCreateTime(systemDate);
		dynamicBean.setCreateUserId(userId);
		dynamicBean.setDynamicDesc(desc);
		dynamicBean.setModifyTime(systemDate);
		dynamicBean.setModifyUserId(userId);
		dynamicBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		dynamicBean.setPublicity(publicity);
		clockDynamicQueueBean.setClockDynamicBean(dynamicBean);
		new ThreadUtil().singleTask(new ClockDynamicThread(clockDynamicQueueBean));
	}
}
