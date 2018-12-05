package com.cn.leedane.service.clock;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 任务动态的Service类
 * @author LeeDane
 * 2018年11月26日 上午11:57:16
 * version 1.0
 */
@Transactional
public interface ClockDynamicService <T extends IDBean>{
	/**
	 * 获取任务动态列表
	 * @param clockId 任务id
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> dynamics(int clockId, JSONObject jo, UserBean user, HttpServletRequest request);
}	
