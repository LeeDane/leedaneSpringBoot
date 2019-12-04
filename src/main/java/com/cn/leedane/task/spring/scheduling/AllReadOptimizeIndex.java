package com.cn.leedane.task.spring.scheduling;

import com.cn.leedane.handler.AllReadHandler;
import com.cn.leedane.mapper.*;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.thread.ThreadUtil;
import com.cn.leedane.thread.single.EsIndexAddThread;
import com.cn.leedane.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 定时对所有系统访问页面的优化的定时任务
 * @author LeeDane
 * 2019年7月4日 下午18:55:57
 * version 1.0
 */
@Component("allReadOptimizeIndex")
public class AllReadOptimizeIndex extends AbstractScheduling{
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private AllReadHandler allReadHandler;
	@Override
	public void execute() throws SchedulerException {
		logger.info("开始优化系统所有访问页统计缓存");
		allReadHandler.delete();
	}
}
