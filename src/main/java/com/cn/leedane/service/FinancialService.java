package com.cn.leedane.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.FinancialBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 记账相关service接口类
 * @author LeeDane
 * 2016年7月22日 上午8:31:34
 * Version 1.0
 */
@Transactional
public interface FinancialService<T extends IDBean>{
	
	/**
	 * 保存数据
	 * @param jo
	 * @param user
	 * @param request
	 * @return 返回服务器保存成功的ID以及设备本地保存的local_id
	 */
	public Map<String, Object> save(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 更新数据
	 * @param jo
	 * @param user
	 * @param request
	 * @return 
	 */
	public Map<String, Object> update(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除数据
	 * @param jo
	 * @param user
	 * @param request
	 * @return 
	 */
	public Map<String, Object> delete(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 客户端数据同步
	 * @param jo
	 * @param user
	 * @param request
	 * @return 返回成功同步的数量和有冲突的数据ID数组
	 */
	public Map<String, Object> synchronous(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 客户端强制更新数据(
     * 		在synchronous()后返回的冲突数据进行强制以客户端或者服务器端的为主，
     * 		要是以客户端的为主，将删掉服务器端的数据；
     * 		要是以服务器端的为主，将返回服务器端的数据，这时可以端需要做的就是替换掉客户端本地
     * 		数据为服务器端返回的数据。
     * )
	 * @param jo
	 * @param user
	 * @param request
	 * @return 
	 */
	public Map<String, Object> force(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取指定年份的数据
	 * @param jo
	 * @param user
	 * @param request
	 * @return 返回该年所有的记账记录
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getByYear(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取该用户全部的数据
	 * @param jo
	 * @param user
	 * @param request
	 * @return 返回该用户所有的记账记录
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getAll(JSONObject jsonObject, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取指定用户的范围内的记账记录
	 * @param createUserId
	 * @param status
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FinancialBean> getByTimeRange(int createUserId, int status, Date startTime, Date endTime);

	/**
	 * 查询
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> query(JSONObject json, UserBean user, HttpServletRequest request);

	
	/**
	 * 分页获取记账列表
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(JSONObject json, UserBean user,HttpServletRequest request);
}
