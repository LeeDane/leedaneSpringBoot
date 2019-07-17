package com.cn.leedane.rabbitmq.recieve;

import com.cn.leedane.mapper.OperateLogMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.EsIndexAddThread;

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
			if(success){
				//再次异步去添加ES索引
				new ThreadUtil().singleTask(new EsIndexAddThread<OperateLogBean>(opLogBean));
			}
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
