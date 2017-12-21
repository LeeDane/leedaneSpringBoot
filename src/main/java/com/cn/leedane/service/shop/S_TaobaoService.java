package com.cn.leedane.service.shop;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 淘宝相关service接口类
 * @author LeeDane
 * 2017年12月6日 下午6:31:23
 * version 1.0
 */
@Transactional
public interface S_TaobaoService <T extends IDBean>{
	/**
	 * 搜索商品
	 * @param jo 参数
	 * @param user 用户
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public  Map<String,Object> search(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 构建淘宝分享的链接
	 * @param taobaoId
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> buildShare(String taobaoId,UserBean user, HttpServletRequest request);
	
}
