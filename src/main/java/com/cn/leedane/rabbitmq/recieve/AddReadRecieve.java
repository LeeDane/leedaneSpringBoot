package com.cn.leedane.rabbitmq.recieve;

import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.MoodMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.springboot.SpringUtil;
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
	private int model;
	public AddReadRecieve(int model){
		this.model = model;
	}

	public final static String QUEUE_NAME = "read_rabbitmq";
	@Override
	public String getQueueName() {
		return QUEUE_NAME;
	}

	@Override
	public Class<?> getQueueClass() {
		Class<?> cl = null;
		switch(model){
		case MODEL_BLOG:
			cl = BlogBean.class;
			break;
		case MODEL_MOOD:
			cl = MoodBean.class;
			break;
		}
		return cl;
	}

	@Override
	public boolean excute(Object obj) { 
		boolean success = false;
		
		try{
			if(obj instanceof BlogBean){
				BlogBean blogBean = (BlogBean)obj;
				//更新读取数量
				int readNum = StringUtil.changeObjectToInt(blogBean.getReadNumber());
				BlogMapper blogMapper = (BlogMapper) SpringUtil.getBean("blogMapper");
				success = blogMapper.updateSql(getQueueClass(), " set read_number = ? , is_read = true where id = ?", readNum, blogBean.getId()) > 0;
			}else if(obj instanceof MoodBean){
				MoodBean moodBean = (MoodBean)obj;
				//更新读取数量
				int readNum = StringUtil.changeObjectToInt(moodBean.getReadNumber());
				MoodMapper moodMapper = (MoodMapper) SpringUtil.getBean("moodMapper");
				success = moodMapper.updateSql(getQueueClass(), " set read_number = ? where id = ?", readNum, moodBean.getId()) > 0;
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
