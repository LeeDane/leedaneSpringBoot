package com.cn.leedane.rabbitmq.recieve;

import com.cn.leedane.mapper.VisitorMapper;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.springboot.SpringUtil;

/**
 * 访客消费者
 * @author LeeDane
 * 2017年5月11日 下午5:24:11
 * version 1.0
 */
public class VisitorRecieve implements IRecieve{


	public final static String QUEUE_NAME = "visitor_rabbitmq";
	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		return VisitorBean.class;
	}

	@Override
	public boolean excute(Object obj) {
		boolean success = false;
		try{
			VisitorBean visitorBean = (VisitorBean)obj;
			VisitorMapper visitorMapper = (VisitorMapper) SpringUtil.getBean("visitorMapper");
			success = visitorMapper.save(visitorBean) > 0;
		}catch(Exception e){
			e.printStackTrace();
		}
		return success;
	}
	
	@Override
	public boolean errorDestroy() {
		return false;
	}
}
