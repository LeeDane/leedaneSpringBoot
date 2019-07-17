package com.cn.leedane.service.mall;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * 购物心愿单service接口类
 * @author LeeDane
 * 2017年11月13日 下午7:23:03
 * version 1.0
 */
@Transactional
public interface S_WishService <T extends IDBean>{
	/**
	 * 添加心愿单
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> add(JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 获取用户的心愿单数量
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> getWishNumber(UserBean user, HttpRequestInfoBean request);

	/**
	 * 分页获取心愿单列表
	 * @param current
	 * @param pageSize
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(int current,
			int pageSize, UserBean user,
			HttpRequestInfoBean request);

	/**
	 * 获取该商品的心愿单的总数
	 * @param productId
	 * @param toDayString
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getWishTotal(int productId, String toDayString);

	/**
	 * 删除心愿单
	 * @param wishId
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> delete(int wishId,
			UserBean user, HttpRequestInfoBean request);
	
	
}
