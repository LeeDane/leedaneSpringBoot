package com.cn.leedane.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.FinancialTwoLevelCategoryBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 记账二级分类service接口类
 * @author LeeDane
 * 2016年12月8日 下午9:28:06
 * Version 1.0
 */
@Transactional
public interface FinancialTwoCategoryService<T extends IDBean>{
	
	/**
	 * 获取默认的所有的二级分类
	 * @param userId
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<FinancialTwoLevelCategoryBean> getAllDefault(int userId);
	
	/**
	 * 获取用户所有的二级分类
	 * @param userId
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getAll(JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 批量插入数据库
	 * @param beans
	 * @return
	 */
	public boolean batchInsert(List<FinancialTwoLevelCategoryBean> beans);

	/**
	 * 获取一级跟二级分类的组合，中间用“>>>”
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getOneAndTwoCategorys(JSONObject json, UserBean user, HttpServletRequest request);
}
