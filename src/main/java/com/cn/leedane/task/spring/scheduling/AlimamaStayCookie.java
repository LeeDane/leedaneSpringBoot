package com.cn.leedane.task.spring.scheduling;

import com.cn.leedane.mall.taobao.api.AlimamaShareLink;
import com.cn.leedane.utils.JsonUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

/**
 * 使阿里妈妈保持登录状态cookie的任务
 * @author LeeDane
 * 2017年12月7日 上午8:22:20
 * version 1.0
 */
@Component("alimamaStayCookie")
public class AlimamaStayCookie extends AbstractScheduling{
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
