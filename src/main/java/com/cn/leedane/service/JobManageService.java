package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.quartz.SchedulerException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 具体任务的管理(增删改查)
 * @author LeeDane
 * 2017年6月5日 下午2:32:28
 * version 1.0
 */
@Transactional
public interface JobManageService <T extends IDBean>{
	
	/**
	 * 添加任务
	 * @param jo
	 * @param user
	 * @param request
	 * @throws SchedulerException
	 */
    public Map<String, Object> add(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws SchedulerException;
    
    /**
     * 修改任务
     * @param jo
     * @param user
     * @param request
     * @throws SchedulerException
     */
    public Map<String, Object> update(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws SchedulerException;

    /**
     * 删除任务
     * @param jid
     * @param request
     * @throws SchedulerException
     */
    public Map<String, Object> delete(int jid, HttpRequestInfoBean request) throws SchedulerException;
    /**
	 * 分页获取任务列表
	 * @param jsonObject
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject jsonFromMessage, UserBean userFromMessage, HttpRequestInfoBean request);

	/**
	 * 批量删除任务
	 * @param pmids
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deletes(String jobids, UserBean userFromMessage, HttpRequestInfoBean request) throws SchedulerException;
}
