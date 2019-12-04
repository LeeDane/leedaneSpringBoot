package com.cn.leedane.task.spring.scheduling;

import com.cn.leedane.mall.taobao.api.AlimamaShareLink;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.JsonUtil;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 商城订单详情的处理
 * @author LeeDane
 * 2018年3月26日 下午1:50:06
 * version 1.0
 */
@Component("mallOrderDetail")
public class MallOrderDetail extends AbstractScheduling{
	private static Logger logger = Logger.getLogger(MallOrderDetail.class);
	
	public static long LAST_REQUEST_TIME = System.currentTimeMillis(); //最后一次的请求时间
	
	@Override
	public void execute() throws SchedulerException {
 		
		long start = System.currentTimeMillis();
		
		JSONObject params = getParams();
		//遍历 webroot/order目录下的所有未处理的excel文件

		//对单个未处理的excel文件都出来

		String ss = ConstantsUtil.getDefaultSaveFileFolder() +"temporary"+ File.separator+ "";
		long end = System.currentTimeMillis();
		logger.info("本次商城订单详情的处理任务总计耗时：" + (end - start) +"毫秒");
	}
	
}
