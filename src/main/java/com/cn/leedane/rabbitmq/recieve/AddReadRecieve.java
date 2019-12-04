package com.cn.leedane.rabbitmq.recieve;

import com.cn.leedane.handler.MoodHandler;
import com.cn.leedane.handler.ReadHandler;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.MoodMapper;
import com.cn.leedane.mapper.ReadMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.ReadBean;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 更新阅读数量的消费者
 * @author LeeDane
 * 2016年8月9日 上午10:34:22
 * Version 1.0
 */
public class AddReadRecieve implements IRecieve{

	public static final int MODEL_BLOG = 1;
	public static final int MODEL_MOOD = 2;
	/**
	 * 类型：1.博客，2.心情
	 */
	public AddReadRecieve(){
	}

	public final static String QUEUE_NAME = "read_rabbitmq";
	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		return ReadBean.class;
	}

	@Override
	public boolean excute(Object obj) { 
		boolean success = false;
		if(obj == null)
			return success;
		try{

			ReadMapper readMapper = (ReadMapper) SpringUtil.getBean("readMapper");
			ReadBean readBean = (ReadBean) obj;
			if(readBean.getCreateUserId() < 1)
				readBean.setCreateUserId(OptionUtil.adminUser.getId());
			success = readMapper.save(readBean) > 0;
			if(success){
				ReadHandler readHandler = (ReadHandler) SpringUtil.getBean("readHandler");
				//清空redis缓存
				readHandler.delete(readBean.getTableName(), readBean.getTableId());
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
