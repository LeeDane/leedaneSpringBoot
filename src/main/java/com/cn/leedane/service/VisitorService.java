package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
/**
 * 访客的Service类
 * @author LeeDane
 * 2017年5月11日 下午4:37:11
 * version 1.0
 */
@Transactional
public interface VisitorService<T extends IDBean>{
	
	/**
	 * 保存访客记录
	 * @return
	 */
	public boolean saveVisitor(UserBean user, String froms, String tableName, int tableId, int status);
	
	/**
	 * 删除访客记录
	 * @return
	 */
	public boolean deleteVisitor(UserBean user, String tableName, int tableId);

	/**
	 * 获取访客列表
	 * @param tableId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getVisitorsByLimit(int tableId,
			JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取今日的访问数
	 * @param tableName
	 * @param tableId
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getTodayVisitors(String tableName, int tableId);
	
	/**
	 * 获取访问数
	 * @param tableName
	 * @param tableId
	 * @param time
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getVisitorsByTime(String tableName, int tableId, String time);
	
	/**
	 * 获取所有的访问数
	 * @param tableName
	 * @param tableId
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getAllVisitors(String tableName, int tableId);
}
