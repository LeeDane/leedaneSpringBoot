package com.cn.leedane.service.shop;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 购物商品大事件service接口类
 * @author LeeDane
 * 2017年11月10日 下午12:38:59
 * version 1.0
 */
@Transactional
public interface S_BigEventService <T extends IDBean>{
	/**
	 * 发布商品
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	public  Map<String,Object> save(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 分页获取大事件列表
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> paging(int productId, JSONObject jo, UserBean user, HttpServletRequest request);
	
}
