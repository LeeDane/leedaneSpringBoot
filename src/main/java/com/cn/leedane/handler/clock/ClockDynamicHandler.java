package com.cn.leedane.handler.clock;

import java.util.Date;

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
	
	/**
	 * 保存动态的信息
	 * @param clockId
	 * @param systemDate
	 * @param userId
	 * @param desc
	 * @param publicity
	 */
	public void saveDynamic(int clockId, Date systemDate, int userId, String desc, boolean publicity){
		//设置动态信息
		ClockDynamicQueueBean clockDynamicQueueBean = new ClockDynamicQueueBean();
		clockDynamicQueueBean.setError(0);
		ClockDynamicBean dynamicBean = new ClockDynamicBean();
		dynamicBean.setClockId(clockId);
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
