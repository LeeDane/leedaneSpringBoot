package com.cn.leedane.rabbitmq.recieve;

import com.cn.leedane.mapper.VisitorMapper;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.springboot.SpringUtil;

/**
 * 删除访客消费者
 * @author LeeDane
 * 2017年6月29日 下午4:30:41
 * version 1.0
 */
public class VisitorDeleteRecieve implements IRecieve{


	public final static String QUEUE_NAME = "visitor_delete_rabbitmq";
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
			visitorMapper.deleteSql(VisitorBean.class, " where table_name = ? and table_id= ?", visitorBean.getTableName(), visitorBean.getTableId());
			success = true;
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
