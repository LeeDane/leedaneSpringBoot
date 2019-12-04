package com.cn.leedane.task.spring.scheduling;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.quartz.SchedulerException;

import com.cn.leedane.utils.StringUtil;

/**
 * 抽象的任务调度类
 * @author LeeDane
 * 2016年7月12日 下午3:24:14
 * Version 1.0
 */
public abstract class AbstractScheduling implements BaseScheduling {
	
	/**
	 * 额外参数
	 */
	private JSONObject params;

	@Override
	public JSONObject getParams() {
		return params;
	}
	
	@Override
	public void setParams(String params) {
		this.params = JSONObject.fromObject(parseParams(params));	
	}

	@Override
	public void execute() throws SchedulerException {
		
	}
	
	/**
	 * 解析参数
	 * @param params
	 * @return
	 */
	private Map<String, Object> parseParams(String params){
		Map<String, Object> psMap = new HashMap<String, Object>();
		if(StringUtil.isNotNull(params)){
			String[] pArray = params.split("&&");
			for(String one: pArray){
				String[] oneArray = one.split("=");
				psMap.put(oneArray[0].trim(), oneArray[1].trim());
			}
		}
		return psMap;
	}
	
}
