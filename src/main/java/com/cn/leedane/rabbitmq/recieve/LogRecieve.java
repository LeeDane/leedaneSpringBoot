package com.cn.leedane.rabbitmq.recieve;

import com.cn.leedane.mapper.OperateLogMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.springboot.SpringUtil;

/**
 * 日志消费者
 * @author LeeDane
 * 2016年8月5日 下午3:27:02
 * Version 1.0
 */
public class LogRecieve implements IRecieve{


	public final static String QUEUE_NAME = "log_rabbitmq";
	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		return OperateLogBean.class;
	}

	@Override
	public boolean excute(Object obj) {
		boolean success = false;
		try{
			OperateLogBean opLogBean = (OperateLogBean)obj;
			OperateLogMapper operateLogMapper = (OperateLogMapper) SpringUtil.getBean("operateLogMapper");
			success = operateLogMapper.save(opLogBean) > 0;
		}catch(Exception e){
			e.printStackTrace();
		}
		return success;
	}
	
	@Override
	public boolean errorDestroy() {
		return true;
	}
}
