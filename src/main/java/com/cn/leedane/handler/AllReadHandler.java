package com.cn.leedane.handler;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AllReadHandler {

	private static final String SYSTEM_ONLINE_KEY = "system_online";

	private RedisUtil redisUtil = RedisUtil.getInstance();

	@Autowired
	private SystemCache systemCache;

	/**
	 * 添加查看总数
	 */
	public synchronized void addRead(){
		int count = getAllRead();
		count = count + 1;
		systemCache.addCache(SYSTEM_ONLINE_KEY, count, true);
		redisUtil.addString(SYSTEM_ONLINE_KEY, count +"");
	}

	/**
	 * 获取查看总数
	 * @return
	 */
	public int getAllRead(){
		int count = 0;
		Object obj = systemCache.getCache(SYSTEM_ONLINE_KEY);
		if(obj == ""){
			if(redisUtil.hasKey(SYSTEM_ONLINE_KEY)){
				count = StringUtil.changeObjectToInt(redisUtil.getString(SYSTEM_ONLINE_KEY));
				if(count > 0){
					systemCache.addCache(SYSTEM_ONLINE_KEY, count, true);
				}

			}else{//redis没有的处理
				OperateLogService<OperateLogBean> operateLogService = (OperateLogService<OperateLogBean>)SpringUtil.getBean("operateLogService");
				count = operateLogService.getAllReadNumber(); //获取所有的页面访问总数
				if(count > 0){
					count = count + 1;
					systemCache.addCache(SYSTEM_ONLINE_KEY, count, true);
					redisUtil.addString(SYSTEM_ONLINE_KEY, count +"");
				}
			}
		}else{
			count = StringUtil.changeObjectToInt(obj);
		}
		return count;
	}

	/**
	 * 优化查看总数
	 */
	public synchronized void optimize(){
		systemCache.removeCache(SYSTEM_ONLINE_KEY);
		redisUtil.delete(SYSTEM_ONLINE_KEY);
	}
}
