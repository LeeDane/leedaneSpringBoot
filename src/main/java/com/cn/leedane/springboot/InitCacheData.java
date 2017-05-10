package com.cn.leedane.springboot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.OptionBean;
import com.cn.leedane.rabbitmq.RecieveMessage;
import com.cn.leedane.rabbitmq.recieve.DeleteServerFileRecieve;
import com.cn.leedane.rabbitmq.recieve.EmailRecieve;
import com.cn.leedane.rabbitmq.recieve.FinancialMonthReportRecieve;
import com.cn.leedane.rabbitmq.recieve.IRecieve;
import com.cn.leedane.rabbitmq.recieve.LogRecieve;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.FinancialCategoryUtil;
import com.cn.leedane.utils.FinancialWebImeiUtil;
import com.cn.leedane.utils.OptionUtil;
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
	
	@Autowired
	private SystemCache systemCache;
	
	public void init(){
		if(systemCache == null)
			systemCache = (SystemCache) SpringUtil.getBean("systemCache");

		checdRidesOpen();//检查redis服务器有没有打开
		initCache();
		loadOptionTable(); //加载选项表中的数据
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
		cache.put("ttt", "hhh");
		logger.info(cache.get("ttt").get());
		SystemCache.setSystemEhCache(cache);
	}

	/**
	 * 加载选项表中的键和值
	 */
	private void loadOptionTable(){
		Connection conn = null;
		try {
			String dbDriver = CommonUtil.getProperties("appConfig.properties", "jdbc.driver");
			String dbURL = CommonUtil.getProperties("appConfig.properties", "jdbc.url");
			String user = CommonUtil.getProperties("appConfig.properties", "jdbc.username");
			String pass = CommonUtil.getProperties("appConfig.properties", "jdbc.password");
			 
			Class.forName(dbDriver);  
			conn = DriverManager.getConnection(dbURL, user, pass);  
			long begin = System.currentTimeMillis();  
			Statement s = conn.createStatement();  
			String sql = "select option_key, option_value from t_option where STATUS = 1";  
			ResultSet querySet = s.executeQuery(sql);  
			while(querySet.next()) {  
				OptionBean option = new OptionBean();  
				option.setOptionKey(querySet.getString(1));
				option.setOptionValue(querySet.getString(2));
				systemCache.addCache(option.getOptionKey(), option.getOptionValue());
			}  
			long end = System.currentTimeMillis();  
			logger.warn("加载选项表数据进缓存中结束，共计耗时："+(end - begin) +"ms");  
			
			//logger.info("---->"+systemCache.getCache("page-size"));
		} catch (Exception ex) {  
			logger.error("初始化读取T_OPTION表出现异常");
			ex.printStackTrace();
		} finally { 
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					logger.error("初始化缓存关闭connection连接出现异常");
				}  
			}
		} 
	}
}
