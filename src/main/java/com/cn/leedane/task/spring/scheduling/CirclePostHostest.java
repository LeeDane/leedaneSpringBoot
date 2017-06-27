package com.cn.leedane.task.spring.scheduling;

import java.util.Date;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.circle.CirclePostMapper;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.OptionUtil;

/**
 * 计算最热门的帖子任务
 * 最热门的定义：一定时间内的所有的帖子： 访问数 * 0.01 + 点赞 * 0.3 + 评论数 * 0.6 + 转发数 * 0.7 + 打赏数 * 1.6 
 * @author LeeDane
 * 2017年6月27日 下午5:15:32
 * version 1.0
 */
@Component("circlePostHostest")
public class CirclePostHostest extends AbstractScheduling{
	private static Logger logger = Logger.getLogger(FinancialMonth.class);
	
	@Autowired
	private CirclePostMapper circlePostMapper;
	
	@Override
	public void execute() throws SchedulerException {
		long start = System.currentTimeMillis();
		JSONObject params = getParams();
		int limit = JsonUtil.getIntValue(params, "limit" , 5);//默认显示最热门的5条记录
		Date time = DateUtil.getDayBeforeOrAfter(OptionUtil.circleHostestBeforeDay, true);
		circlePostMapper.calculateHotests(time, limit);
		long end = System.currentTimeMillis();
		logger.info("执行最热门的帖子任务总计耗时：" + (end - start) +"毫秒");
	}
}
