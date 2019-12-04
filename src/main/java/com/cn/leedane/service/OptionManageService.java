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
 * 选项管理的管理(增删改查)
 * @author LeeDane
 * 2019年12月2日 下午18:32:28
 * version 1.0
 */
@Transactional
public interface OptionManageService<T extends IDBean>{
	
	/**
	 * 添加option
	 * @param jo
	 * @param user
	 * @param request
	 * @throws SchedulerException
	 */
    public Map<String, Object> add(JSONObject jo, UserBean user, HttpRequestInfoBean request);
    
    /**
     * 修改option
     * @param jo
     * @param user
     * @param request
     * @throws SchedulerException
     */
    public Map<String, Object> update(JSONObject jo, UserBean user, HttpRequestInfoBean request);

    /**
     * 删除option
     * @param optionId
     * @param request
     * @throws SchedulerException
     */
    public Map<String, Object> delete(long optionId, HttpRequestInfoBean request);
    /**
	 * 分页获取option列表
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject jsonFromMessage, UserBean userFromMessage, HttpRequestInfoBean request);

	/**
	 * 批量删除option
	 * @param optionIds
	 * @param userFromMessage
	 * @param request
	 * @return
	 */
	public Map<String, Object> deletes(String optionIds, UserBean userFromMessage, HttpRequestInfoBean request);
}
