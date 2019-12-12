package com.cn.leedane.task.spring.scheduling;

import com.cn.leedane.mall.jingdong.api.OrderListApi;
import com.cn.leedane.mall.model.S_OrderTaobaoDetailBean;
import com.cn.leedane.mapper.mall.S_OrderDetailMapper;
import com.cn.leedane.model.mall.S_OrderDetailBean;
import com.cn.leedane.utils.*;
import com.github.liaochong.myexcel.core.SaxExcelReader;
import com.jd.open.api.sdk.JdException;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 拼多多商城订单详情的处理
 * @author LeeDane
 * 2019年12月26日 下午18:50:06
 * version 1.0
 */
@Component("mallPinduoduoOrderDetail")
public class MallPinduoduoOrderDetail extends AbstractScheduling{
	private static Logger logger = Logger.getLogger(MallPinduoduoOrderDetail.class);

	@Autowired
	private S_OrderDetailMapper orderDetailMapper;
	@Override
	public void execute() throws SchedulerException {
		long start = System.currentTimeMillis();

		JSONObject params = getParams();
		try {
			JSONArray times = JSONArray.fromObject(JsonUtil.getStringValue(params, "times"));
			if(times != null && times.size() > 0){
				for(int i= 0; i < times.size(); i++){
					List<S_OrderDetailBean> detailBeans = OrderListApi.get(params.optInt("pageNo"), params.optInt("pageSize"), params.optInt("type"), times.optString(i));
					if(CollectionUtil.isNotEmpty(detailBeans)){
						//入库
						int count = orderDetailMapper.insertByBatchOnDuplicate(detailBeans);
						System.out.println("京东订单批量入库后返回的数据："+ count);
					}
				}
			}
		} catch (JdException e) {
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		logger.info("本次京东商城订单详情的处理任务总计耗时：" + (end - start) +"毫秒");
	}
}
