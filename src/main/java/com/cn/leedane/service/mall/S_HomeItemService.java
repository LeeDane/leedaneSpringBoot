package com.cn.leedane.service.mall;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
	public ResponseModel addItem(JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 获取该分类的数据
	 * @param categoryId
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel getItem(long categoryId,
			UserBean user, HttpRequestInfoBean request);

	/**
	 * 修改分类项
	 * @param itemId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel updateItem(long itemId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
	
	/**
	 * 删除分类项
	 * @param categoryId
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel deleteItem(long categoryId,
			UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 首页展示的分类列表
	 * @return
	 */
	public ResponseModel showCategoryList();

	/**
	 * 获得单项分类的匹配
	 * @param itemId
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel matchingCategory(long itemId,
			UserBean user, HttpRequestInfoBean request);

	/**
	 * 修改某一项的分类
	 * @param itemId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel updateCategory(long itemId, JSONObject json, UserBean user, HttpRequestInfoBean request);

	/**
	 * 添加某一项的商品
	 * @param itemId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel addProduct(long itemId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
	
	/**
	 * 删除某一项的商品
	 * @param itemId
	 * @param productId
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel deleteProduct(long itemId,
											 long productId, UserBean user,
			HttpRequestInfoBean request);

	/**
	 * 获取未添加的项
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel noList(
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);

	/**
	 * 获取所有首页分类项的列表
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel getItems(UserBean user, HttpRequestInfoBean request);
}
