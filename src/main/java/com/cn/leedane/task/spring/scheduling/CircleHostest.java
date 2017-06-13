package com.cn.leedane.task.spring.scheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.model.EmailBean;
import com.cn.leedane.model.FinancialBean;
import com.cn.leedane.model.FinancialReport;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.rabbitmq.SendMessage;
import com.cn.leedane.rabbitmq.send.EmailSend;
import com.cn.leedane.rabbitmq.send.FinancialMonthReportSend;
import com.cn.leedane.rabbitmq.send.ISend;
import com.cn.leedane.service.FinancialService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil.EmailType;
import com.cn.leedane.utils.StringUtil;

/**
 * 获取最热门的圈子任务
 * 最热门的定义：3天内的所有的圈子： 用户打卡记录 * 0.3 + 用户净新增记录(新增-退出) * 0.6 + 任务完成总数 * 0.4 + 打开的次数 * 0.1 
 * @author LeeDane
 * 2017年6月13日 下午4:15:25
 * version 1.0
 */
@Component("circleHostest")
public class CircleHostest implements BaseScheduling{
	private static Logger logger = Logger.getLogger(FinancialMonth.class);
	
	@Autowired
	private CircleMapper circleMapper;
	
	@Override
	public void execute() throws SchedulerException {
		
		long start = System.currentTimeMillis();
		//circleMapper
		
		long end = System.currentTimeMillis();
		logger.info("执行最热门的圈子任务总计耗时：" + (end - start) +"毫秒");
	}
}
