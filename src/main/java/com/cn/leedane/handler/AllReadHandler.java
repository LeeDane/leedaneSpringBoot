package com.cn.leedane.handler;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.AllReadBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class AllReadHandler extends BaseCacheHandler<AllReadBean>{

	//系统浏览记录key
	private static final String SYSTEM_BROWSE_KEY = "st_browses";

	private RedisUtil redisUtil = RedisUtil.getInstance();

	@Autowired
	private SystemCache systemCache;

	//通过读写锁限制
	private ReadWriteLock rwLocak = new ReentrantReadWriteLock();

	/**
	 * 添加查看总数
	 */
	public boolean add(){
		try{
			rwLocak.writeLock().lock();
			return super.addCache(SYSTEM_BROWSE_KEY, new AllReadBean(StringUtil.changeObjectToInt(get()) +1));
		}catch (Exception e){
		    e.printStackTrace();
		}finally {
			rwLocak.writeLock().unlock();
		}
		return false;
	}

	@Override
	protected AllReadBean getBean() {
		return new AllReadBean();
	}

	/**
	 * 获取查看总数
	 * @return
	 */
	@Override
	public Object get(Object... params) {
		try{
			rwLocak.readLock().lock();
			Object obj = super.get(params);
			if(obj != null){
				return ((AllReadBean)obj).getNumber();
			}
		}catch (Exception e){
		    e.printStackTrace();
		}finally {
			rwLocak.readLock().unlock();
		}
		return 0;
	}

	@Override
	protected AllReadBean getT(Object... params) {
		OperateLogService<OperateLogBean> operateLogService = (OperateLogService<OperateLogBean>)SpringUtil.getBean("operateLogService");
		return new AllReadBean(operateLogService.getAllReadNumber()); //获取所有的页面访问总数;
	}

	@Override
	public String getKey(Object... params) {
		return SYSTEM_BROWSE_KEY;
	}
}
