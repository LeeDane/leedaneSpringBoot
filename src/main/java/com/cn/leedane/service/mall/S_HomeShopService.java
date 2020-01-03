package com.cn.leedane.service.mall;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONObject;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

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
	public ResponseModel add(JSONObject json, UserBean user, HttpRequestInfoBean request);
	
	/**
	 * 删除商店
	 * @param shopId
	 * @param user
	 * @param request
	 * @return
	 */
	public ResponseModel delete(long shopId, UserBean user, HttpRequestInfoBean request);
	
	
	/**
	 * 获取商店列表
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public ResponseModel shops();

}
