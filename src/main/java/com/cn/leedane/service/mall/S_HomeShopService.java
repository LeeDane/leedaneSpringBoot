package com.cn.leedane.service.mall;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 首页商店service接口类
 * @author LeeDane
 * 2018年1月11日 下午2:17:53
 * version 1.0
 */
@Transactional
public interface S_HomeShopService <T extends IDBean>{
	/**
	 * 添加商店
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> add(JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 删除商店
	 * @param shopId
	 * @param user
	 * @param request
	 * @return
	 */
	public  Map<String,Object> delete(int shopId, UserBean user, HttpServletRequest request);
	
	
	/**
	 * 获取商店列表
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> shops();

}
