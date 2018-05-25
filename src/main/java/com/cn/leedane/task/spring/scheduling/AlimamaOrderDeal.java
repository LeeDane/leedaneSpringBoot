package com.cn.leedane.task.spring.scheduling;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import com.cn.leedane.taobao.api.AlimamaShareLink;
import com.cn.leedane.utils.JsonUtil;

/**
 * 阿里妈妈推广订单的任务
 * @author LeeDane
 * 2018年3月26日 下午1:50:06
 * version 1.0
 */
@Component("alimamaOrderDeal")
public class AlimamaOrderDeal extends AbstractScheduling{
	private static Logger logger = Logger.getLogger(AlimamaStayCookie.class);
	
	public static long LAST_REQUEST_TIME = System.currentTimeMillis(); //最后一次的请求时间
	
	@Override
	public void execute() throws SchedulerException {
 		
		long start = System.currentTimeMillis();
		
		JSONObject params = getParams();
		String taobaoId = JsonUtil.getStringValue(params, "taobaoId" , "549091479417");
		AlimamaShareLink.cookie2 = JsonUtil.getStringValue(params, "cookie2" );
		
		//如果是3分钟内已经发送过的请求，则不做处理
		if(AlimamaShareLink.isSuccess && start - LAST_REQUEST_TIME < 3 * 60 * 1000){
			logger.info("不继续执行保持阿里妈妈登录状态任务");
			return;
		}
		System.out.println("cookie2="+ AlimamaShareLink.cookie2);
		AlimamaShareLink alimama = new AlimamaShareLink();
		try {
			logger.info("保持cookie状态："+ alimama.doParse(taobaoId).toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		logger.info("保持阿里妈妈登录状态任务总计耗时：" + (end - start) +"毫秒");
	}
	
}
