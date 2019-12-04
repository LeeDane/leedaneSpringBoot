package com.cn.leedane.rabbitmq.recieve;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Component;

import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.mapper.clock.ClockDynamicMapper;
import com.cn.leedane.message.JpushCustomMessage;
import com.cn.leedane.message.notification.CustomMessage;
import com.cn.leedane.model.clock.ClockDynamicQueueBean;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.ClockDynamicThread;
import com.cn.leedane.thread.single.ClockScoreThread;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;

/**
 * 任务动态处理消费者（执行三次失败就将通知发送给创建者）
 * @author LeeDane
 * 2018年11月26日 下午3:59:38
 * version 1.0
 */
@Component
public class ClockDynamicRecieve implements IRecieve{

	public final static String QUEUE_NAME = "clock_dynamic_rabbitmq";
	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		return ClockDynamicQueueBean.class;
	}

	@Override
	public boolean excute(Object obj) {
		boolean success = false;
		ClockDynamicQueueBean clockDynamicQueueBean = null;
		try{
			clockDynamicQueueBean = (ClockDynamicQueueBean)obj;
			ClockDynamicMapper dynamicMapper = (ClockDynamicMapper) SpringUtil.getBean("clockDynamicMapper");
			success = dynamicMapper.save(clockDynamicQueueBean.getClockDynamicBean()) > 0;
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//不成功
		if(!success && clockDynamicQueueBean != null){
			long createUserId = clockDynamicQueueBean.getClockDynamicBean().getCreateUserId();
			//已经有三次失败记录，发送给创建者
			if(clockDynamicQueueBean.getError() == 2){
				CustomMessage customMessage = new JpushCustomMessage();
				Map<String, Object> mp = new HashMap<String, Object>();
				mp.put("user_id", createUserId);
				mp.put("content", "有任务动态失败超过3次，请核实！");
				customMessage.sendToAlias("leedane_user_"+ createUserId,  JSONObject.fromObject(mp).toString(), EnumUtil.CustomMessageExtraType.动态失败超过三次.value);
			}else{
				//继续添加到队列中做进一步的处理
				clockDynamicQueueBean.setError(clockDynamicQueueBean.getError() + 1);
				new ThreadUtil().singleTask(new ClockDynamicThread(clockDynamicQueueBean));
			}
		}else{
			//通知创建者
		}
		return true;
	}
	
	@Override
	public boolean errorDestroy() {
		return false;
	}
}
