package com.cn.leedane.service.mall;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 购物商品service接口类
 * @author LeeDane
 * 2017年11月7日 下午4:46:37
 * version 1.0
 */
@Transactional
public interface S_ProductService <T extends IDBean>{
	/**
	 * 发布商品
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> save(JSONObject jo, UserBean user, HttpRequestInfoBean request);

	/**
	 * 统计商品
	 * @param productId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> statistics(long productId, JSONObject json,
			UserBean user, HttpRequestInfoBean request);

	/**
	 * 商品的推荐
	 * @param productId
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> recommend(long productId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request);
	
	
	
}
