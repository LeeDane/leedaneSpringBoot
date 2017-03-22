package com.cn.leedane.service;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import net.sf.json.JSONObject;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
/**
 * 操作日志的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:34:08
 * Version 1.0
 */
@Transactional("txManager")
public interface OperateLogService<T extends IDBean>{

	/**
	 * 保存操作日志
	 * @param user  用户
	 * @param request  请求
	 * @param createTime 创建时间
	 * @param subject  标题
	 * @param method  方式
	 * @param status  状态
	 * @param operateType 操作类型
	 * @return
	 */
	public boolean saveOperateLog(UserBean user,HttpServletRequest request,Date createTime,String subject, String method,int status,int operateType);

	/**
	 * 分页获取用户登录操作日志列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	//标记该方法不需要事务
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getUserLoginLimit(JSONObject jo, UserBean user, HttpServletRequest request);

}
