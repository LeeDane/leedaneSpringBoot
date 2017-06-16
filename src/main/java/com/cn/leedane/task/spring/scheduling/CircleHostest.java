package com.cn.leedane.task.spring.scheduling;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.handler.InitCacheData;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 获取最热门的圈子任务
 * 最热门的定义：3天内的所有的圈子： 用户打卡记录 * 0.3 + 用户净新增记录(新增-退出) * 0.6 + 任务完成总数 * 0.4 + 打开的次数 * 0.1 
 * @author LeeDane
 * 2017年6月13日 下午4:15:25
 * version 1.0
 */
@Component("circleHostest")
public class CircleHostest extends AbstractScheduling{
	private static Logger logger = Logger.getLogger(FinancialMonth.class);
	
	@Autowired
	private CircleMapper circleMapper;
	
	@Autowired
	private CircleHandler circleHandler;
	
	@Override
	public void execute() throws SchedulerException {
		
		long start = System.currentTimeMillis();
		
		JSONObject params = getParams();
		int recent = JsonUtil.getIntValue(params, "recent" , -4);//默认是最近3天
		int limit = JsonUtil.getIntValue(params, "recent" , 5);//默认显示最热门的5条记录
		
		//circleMapper
		Date time = DateUtil.getDayBeforeOrAfter(recent, true);
		circleMapper.calculateGetHotests(time, limit);
		long end = System.currentTimeMillis();
		logger.info("执行最热门的圈子任务总计耗时：" + (end - start) +"毫秒");
	}
	
	
	
}
