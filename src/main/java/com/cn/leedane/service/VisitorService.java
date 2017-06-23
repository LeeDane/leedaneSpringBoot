package com.cn.leedane.service;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
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
	 * 获取访客列表
	 * @param tableId
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getVisitorsByLimit(int tableId,
			JSONObject json, UserBean user, HttpServletRequest request);
}
