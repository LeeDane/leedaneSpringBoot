package com.cn.leedane.task.spring.scheduling;

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
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 定时把数据放到es中的定时任务
 * @author LeeDane
 * 2019年7月4日 下午18:55:57
 * version 1.0
 */
@Component("addEsIndex")
public class AddEsIndex extends AbstractScheduling{
	private Logger logger = Logger.getLogger(getClass());
	
	private RedisUtil redis = new RedisUtil();

	@Autowired
	private ElasticSearchUtil elasticSearchUtil;

	@Autowired
	private SqlSearchMapper sqlSearchMapper;

	@Autowired
	private BlogMapper blogMapper;

	@Autowired
	private MoodMapper moodMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private OperateLogMapper operateLogMapper;

	@Override
	public void execute() throws SchedulerException {

		if(ConstantsUtil.isAddEsIndexDoing){
			logger.info("AddEsIndex-->execute(): 任务已经在执行中，请稍后");
			return;
		}

		ConstantsUtil.isAddEsIndexDoing = true;
		try {
			JSONObject params = getParams();
			//获取需要执行的数据表(这些数据表默认具有ID和isEs字段)
			JSONArray tables = JSONArray.fromObject(JsonUtil.getStringValue(params, "tables"));
			//到数据库查找未加入es索引的数据
			for(int i = 0; i < tables.size(); i++){
				while (dealTable(tables.optString(i))){
					Thread.sleep(100);
				}
			}

		} catch (Exception e) {
			ConstantsUtil.isAddEsIndexDoing = false;
			e.printStackTrace();
			logger.error("定时把数据库数据加载到索引中异常：", e);
		}

		ConstantsUtil.isAddEsIndexDoing = false;
	}


	/**
	 * 处理单个表的数据
	 * @param table
	 * @return
	 */
	private boolean dealTable(String table){
		List<Map<String, Object>> ids = sqlSearchMapper.executeSQL("select id from " + table + " where es_index = false limit 20");
		if(CollectionUtil.isEmpty(ids)){
			logger.info(table + "没有需要更新的索引数据");
			return false;
		}else{
			for(int k = 0; k < ids.size(); k++){
				int id = StringUtil.changeObjectToInt(ids.get(k).get("id"));
				if(EnumUtil.DataTableType.博客.value.equalsIgnoreCase(table)){
					new ThreadUtil().singleTask(new EsIndexAddThread<BlogBean>(blogMapper.findById(BlogBean.class, id)));
				} else if(EnumUtil.DataTableType.心情.value.equalsIgnoreCase(table)){
					new ThreadUtil().singleTask(new EsIndexAddThread<MoodBean>(moodMapper.findById(MoodBean.class, id)));
				}else if(EnumUtil.DataTableType.用户.value.equalsIgnoreCase(table)){
					new ThreadUtil().singleTask(new EsIndexAddThread<UserBean>(userMapper.findById(UserBean.class, id)));
				}else if(EnumUtil.DataTableType.操作日志.value.equalsIgnoreCase(table)){
					new ThreadUtil().singleTask(new EsIndexAddThread<OperateLogBean>(operateLogMapper.findById(OperateLogBean.class, id)));
				}
			}

			try {
				//休眠足够才行，由于是异步操作，还没有等加入索引-更新状态SQL就执行了，这里就每个给1秒钟来操作
				Thread.sleep(1000 * ids.size());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return dealTable(table);
		}
	}
}
