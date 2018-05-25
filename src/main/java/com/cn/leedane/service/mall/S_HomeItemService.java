package com.cn.leedane.service.mall;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 首页商品分类service接口类
 * @author LeeDane
 * 2017年12月26日 下午2:27:29
 * version 1.0
 */
@Transactional
public interface S_HomeItemService <T extends IDBean>{
	/**
	 * 添加商品分类项
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> addItem(JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 获取该分类的数据
	 * @param categoryId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> getItem(int categoryId,
			UserBean user, HttpServletRequest request);

	/**
	 * 修改分类项
	 * @param itemId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> updateItem(int itemId,
			JSONObject json, UserBean user,
			HttpServletRequest request);
	
	/**
	 * 删除分类项
	 * @param categoryId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteItem(int categoryId,
			UserBean user, HttpServletRequest request);
	
	/**
	 * 首页展示的分类列表
	 * @return
	 */
	public Map<String, Object> showCategoryList();

	/**
	 * 获得单项分类的匹配
	 * @param itemId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> matchingCategory(int itemId,
			UserBean user, HttpServletRequest request);

	/**
	 * 修改某一项的分类
	 * @param itemId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> updateCategory(int itemId, JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 添加某一项的商品
	 * @param itemId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addProduct(int itemId,
			JSONObject json, UserBean user,
			HttpServletRequest request);
	
	/**
	 * 删除某一项的商品
	 * @param itemId
	 * @param productId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteProduct(int itemId,
			int productId, UserBean user,
			HttpServletRequest request);

	/**
	 * 获取未添加的项
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> noList(
			JSONObject json, UserBean user,
			HttpServletRequest request);

	/**
	 * 获取所有首页分类项的列表
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> getItems(UserBean user, HttpServletRequest request);
}
