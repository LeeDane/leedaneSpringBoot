package com.cn.leedane.task.spring.scheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.mapper.circle.CircleMemberMapper;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.OptionUtil;

/**
 * 获取圈子最热门的成员任务
 * @author LeeDane
 * 2017年6月20日 上午11:32:38
 * version 1.0
 */
@Component("circleMemberHostest")
public class CircleMemberHostest extends AbstractScheduling{
	private static Logger logger = Logger.getLogger(FinancialMonth.class);
	
	@Autowired
	private CircleMemberMapper circleMemberMapper;
	
	@Autowired
	private CircleMapper circleMapper;
	
	@Autowired
	private SystemCache systemCache;
	
	@Override
	public void execute() throws SchedulerException {
 		
		long start = System.currentTimeMillis();
		
		JSONObject params = getParams();
		int limit = JsonUtil.getIntValue(params, "limit" , 100);//默认显示最热门的5条记录
		Date time = DateUtil.getDayBeforeOrAfter(OptionUtil.circleHostestBeforeDay, true);
		//获取最近热门的100个圈子才去计算其成员热门
		List<CircleBean> circleBeans = circleMapper.getHotests(time, limit);
		if(CollectionUtil.isEmpty(circleBeans))
			return;
		
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		ExecutorService threadpool = Executors.newFixedThreadPool(circleBeans.size() >5 ? 5: circleBeans.size());
		SingleCalculateTask dealTask;
		for(CircleBean bean: circleBeans){
			dealTask = new SingleCalculateTask(bean);
			futures.add(threadpool.submit(dealTask));
		}
		
		threadpool.shutdown();
		
		for(int i = 0; i < futures.size(); i++){
			try {
				if(!futures.get(i).get()){
					logger.error(circleBeans.get(i).getName() + "--->圈子的热门成员计算失败");
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}		
		
		long end = System.currentTimeMillis();
		logger.info("执行圈子最热门的成员任务总计耗时：" + (end - start) +"毫秒");
	}
	
	/**
	 * 计算单个圈子的热门成员任务
	 * @author LeeDane
	 * 2017年6月20日 下午12:03:54
	 * version 1.0
	 */
	class SingleCalculateTask implements Callable<Boolean>{
		private CircleBean mCircle;
		
		public SingleCalculateTask(CircleBean circle){
			this.mCircle = circle;
		}
		@Override
		public Boolean call() throws Exception {
			
			try{
				circleMemberMapper.calculateHotests(mCircle.getId());
				return true;
			}catch(Exception e){
				e.printStackTrace();
				return false;
			}
		}
	}
}
