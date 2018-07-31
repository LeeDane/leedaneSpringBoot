package com.cn.leedane.handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.model.JobManageBean;
import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.rabbitmq.RecieveMessage;
import com.cn.leedane.rabbitmq.recieve.DeleteServerFileRecieve;
import com.cn.leedane.rabbitmq.recieve.EmailRecieve;
import com.cn.leedane.rabbitmq.recieve.FinancialMonthReportRecieve;
import com.cn.leedane.rabbitmq.recieve.IRecieve;
import com.cn.leedane.rabbitmq.recieve.LogRecieve;
import com.cn.leedane.rabbitmq.recieve.VisitorDeleteRecieve;
import com.cn.leedane.rabbitmq.recieve.VisitorRecieve;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.task.spring.QuartzJobFactory;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.FinancialCategoryUtil;
import com.cn.leedane.utils.FinancialWebImeiUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.utils.sensitiveWord.SensitiveWordInit;

/**
 * 初始化缓存数据
 * @author LeeDane
 * 2017年3月20日 下午5:35:00
 * Version 1.0
 */
@Component
public class InitCacheData {
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 最热门的圈子
	 */
	//public static List<CircleBean> hotestCircleBeans = new ArrayList<CircleBean>();
	
	/**
	 * 最新的圈子
	 */
	//public static List<CircleBean> newestCircleBeans = new ArrayList<CircleBean>();
	
	/**
	 * 推荐的圈子
	 */
	//public static List<CircleBean> recommendCircleBeans = new ArrayList<CircleBean>();
	
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private BaseMapper<RecordTimeBean> baseMapper;
	
	@Autowired
	private Scheduler scheduler;
	
	@Autowired
	private CircleMapper circleMapper;
	
	public void init(){

		checdRidesOpen();//检查redis服务器有没有打开
		initCache();
		loadOptionTable(); //加载选项表中的数据
		startJob(); //加载定时任务
		//getApplicationData(); //获取系统上下文的全局数据
		new OptionUtil();//加载选项到内存中
		
		loadFilterUrls(); //加载过滤的url地址
		loadLeeDaneProperties(); // 加载leedane.properties文件
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				startRabbitMqLogListener(); //异步启动rabbitmq的操作日志队列的监听
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				startRabbitMqDeleteFileListener(); //异步启动rabbitmq的操作删除临时文件的监听
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				startRabbitMqEmailListener(); //异步启动rabbitmq的操作邮件队列的监听
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				startRabbitMqFinancialReportListener(); //异步启动rabbitmq记账月报队列的监听
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				startRabbitMqVisitorListener(); //异步启动rabbitmq访客队列的监听
			}
		}).start();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				startRabbitMqVisitorDeleteListener(); //异步启动rabbitmq删除访客队列的监听
			}
		}).start();
		
		SensitiveWordInit.getInstance(); //加载敏感词库
		
		FinancialCategoryUtil.getInstance();
		
		FinancialWebImeiUtil.getInstance();
	}
	
	/**
	 * 启动rabbitmq记账月报队列的监听
	 */
	private void startRabbitMqFinancialReportListener() {
		
		try {
			//记账月报队列的监听
			IRecieve recieve = new FinancialMonthReportRecieve();
			RecieveMessage recieveMessage = new RecieveMessage(recieve);
			recieveMessage.getMsg();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 启动rabbitmq访客队列的监听
	 */
	private void startRabbitMqVisitorListener() {
		
		try {
			//访客队列的监听
			IRecieve recieve = new VisitorRecieve();
			RecieveMessage recieveMessage = new RecieveMessage(recieve);
			recieveMessage.getMsg();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 启动rabbitmq删除访客队列的监听
	 */
	private void startRabbitMqVisitorDeleteListener() {
		
		try {
			//删除访客队列的监听
			IRecieve recieve = new VisitorDeleteRecieve();
			RecieveMessage recieveMessage = new RecieveMessage(recieve);
			recieveMessage.getMsg();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 启动rabbitmq日志队列的监听
	 */
	private void startRabbitMqLogListener() {
		
		try {
			//日志队列的监听
			IRecieve recieve = new LogRecieve();
			RecieveMessage recieveMessage = new RecieveMessage(recieve);
			recieveMessage.getMsg();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 启动rabbitmq删除临时文件的监听
	 */
	private void startRabbitMqDeleteFileListener() {
		
		try {
			//日志队列的监听
			IRecieve recieve = new DeleteServerFileRecieve();
			RecieveMessage recieveMessage = new RecieveMessage(recieve);
			recieveMessage.getMsg();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * 启动rabbitmq邮件队列的监听
	 */
	private void startRabbitMqEmailListener() {
		
		try {
			//邮件队列的监听
			IRecieve email = new EmailRecieve();
			RecieveMessage emailRecieveMessage = new RecieveMessage(email);
			emailRecieveMessage.getMsg();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	/**
	 * 检查redis服务器有没有打开
	 */
	private void checdRidesOpen() {
		if(RedisUtil.getInstance().isPing()){
			logger.warn("Redis服务已经启动");
		}else{
			logger.error("Redis服务还未启动");
		}
	}

	/**
	 * 加载leedane.properties文件
	 */
	private void loadLeeDaneProperties() {
		long begin = System.currentTimeMillis();  
		try {
			Map<String,Object> properties = new HashMap<String, Object>();
			InputStream in = CommonUtil.getResourceAsStream("leedane.properties");  
			 // 创建Properties实例
			Properties prop = new Properties();
			// 将Properties和流关联
			prop.load(in);
			Set<Object> set = prop.keySet();
		    Iterator<Object> it = set.iterator();
		    while(it.hasNext()) {
		    	String key = String.valueOf(it.next());
		   	 	properties.put(key, prop.getProperty(key));
		    }
		    systemCache.addCache("leedaneProperties", properties);
		    long end = System.currentTimeMillis();  
		    logger.warn("加载leedane.properties中的数据进缓存中结束，共计耗时："+(end - begin) +"ms"); 
		} catch (IOException e) {
			//e.printStackTrace();
			System.err.println("加载leedane.properties属性配置文件出错");
		}
	}

	/**
	 * 加载需要过滤掉的url地址
	 */
	private void loadFilterUrls() {
		InputStreamReader reader = null;
		long begin = System.currentTimeMillis();  
		try {
			
			List<String> urls = new ArrayList<String>();
			InputStream in = CommonUtil.getResourceAsStream("filter-url.txt");  
			reader = new InputStreamReader(in);
			BufferedReader bufferedReader = new BufferedReader(reader);
			String text = "";
			while( (text = bufferedReader.readLine()) != null){
				urls.add(text);
			}		
			systemCache.addCache("filterUrls", urls);
			bufferedReader.close();
			long end = System.currentTimeMillis();  
			logger.warn("加载filter-url.txt中的数据进缓存中结束，共计耗时："+(end - begin) +"ms"); 
		} catch (Exception e) {
			//e.printStackTrace();
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("加载filter-url.txt过滤的地址文件出错");
				}
			}
		}
	}

	/**
	 * 初始化
	 */
	private void initCache() {
		systemCache = (SystemCache) SpringUtil.getBean("systemCache");		
		EhCacheCacheManager ehCacheCacheManager = (EhCacheCacheManager) SpringUtil.getBean("ehCacheCacheManager");
		EhCacheCache cache = (EhCacheCache) ehCacheCacheManager.getCache("systemEhCache");
		SystemCache.setSystemEhCache(cache);
	}

	/**
	 * 加载选项表中的键和值
	 */
	private void loadOptionTable(){
		try {
			long begin = System.currentTimeMillis();
			List<Map<String, Object>> results = baseMapper.executeSQL("select option_key, option_value from t_option where STATUS = 1");
			if(CollectionUtil.isNotEmpty(results)){
				for(Map<String, Object> result: results){
					//OptionBean option = new OptionBean();  
					//option.setOptionKey(StringUtil.changeNotNull(result.get("option_key")));
					//option.setOptionValue(StringUtil.changeNotNull(result.get("option_value")));
					systemCache.addCache(StringUtil.changeNotNull(result.get("option_key")), StringUtil.changeNotNull(result.get("option_value")));
				}
			}
			long end = System.currentTimeMillis();  
			logger.warn("加载选项表数据进缓存中结束，共计耗时："+(end - begin) +"ms");  
			
			//logger.info("---->"+systemCache.getCache("page-size"));
		} catch (Exception ex) {  
			logger.error("初始化读取T_OPTION表出现异常");
			ex.printStackTrace();
		} 
	}
	
	/**
	 * 加载定时任务到任务队列中
	 */
	private void startJob(){
		try {
			long begin = System.currentTimeMillis();
			List<Map<String, Object>> results = baseMapper.executeSQL("SELECT * FROM t_job_manage where STATUS = 1");
			if(CollectionUtil.isNotEmpty(results)){
				for(Map<String, Object> result: results){
					JSONObject json = JSONObject.fromObject(result);
					json.remove("create_time");//暂时不处理创建日期
					json.remove("modify_time");//暂时不处理修改日期
					SqlUtil sqlUtil = new SqlUtil();
					JobManageBean jobManageBean = (JobManageBean) sqlUtil.getBean(json, JobManageBean.class);
					if(jobManageBean != null){
						TriggerKey triggerKey = TriggerKey.triggerKey(jobManageBean.getJobName(), jobManageBean.getJobGroup());
			            //获取trigger，即在spring配置文件中定义的 bean id="myTrigger"
			            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
			         
			            //存在，删掉
			            if (trigger != null) {
			                scheduler.deleteJob(trigger.getJobKey());
			            }
			            
			            //按新的trigger重新设置job执行
			            JobDetail jobDetail = JobBuilder.newJob(QuartzJobFactory.class)
			                    .withIdentity(jobManageBean.getJobName(), jobManageBean.getJobGroup()).build();
			            jobDetail.getJobDataMap().put("scheduleJob", jobManageBean);
			     
			            //表达式调度构建器
			            CronScheduleBuilder scheduleBuilder1 = CronScheduleBuilder.cronSchedule(jobManageBean
			                .getCronExpression());
			     
			            //按新的cronExpression表达式构建一个新的trigger
			            trigger = TriggerBuilder.newTrigger().withIdentity(jobManageBean.getJobName(), jobManageBean.getJobGroup()).withSchedule(scheduleBuilder1).build();
			            scheduler.scheduleJob(jobDetail, trigger);
					}
					
				}
			}
			long end = System.currentTimeMillis();  
			logger.warn("加载定时任务表数据进缓存中结束，共计耗时："+(end - begin) +"ms");  
			
			//logger.info("---->"+systemCache.getCache("page-size"));
		} catch (Exception ex) {  
			logger.error("初始化读取T_JOB_MANAGE表出现异常");
			ex.printStackTrace();
		}
	}
	
	/**
	 * 获取系统上下文的全局数据
	 */
	public void getApplicationData(){
		logger.warn("加载系统上下文的全局数据开始"); 
		long begin = System.currentTimeMillis(); 
		//hotestCircleBeans = circleMapper.getHotests(DateUtil.getDayBeforeOrAfter(-4, true), 6);
		//newestCircleBeans = circleMapper.getNewests(5);
		//recommendCircleBeans = circleMapper.getRecommends(6);
		long end = System.currentTimeMillis();
		logger.warn("加载系统上下文的全局数据结束，共计耗时："+(end - begin) +"ms"); 
	}
}
