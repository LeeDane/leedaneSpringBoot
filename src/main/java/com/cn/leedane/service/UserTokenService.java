package com.cn.leedane.service;

import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.UserTokenBean;
import com.cn.leedane.utils.ResponseMap;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户与token关系service接口类
 * @author LeeDane
 * 2017年3月24日 下午1:43:39
 * version 1.0
 */
@Transactional
public interface UserTokenService<T extends UserTokenBean>{
	
	/**
	 * 都是没有过期的token，不过缓存存在的时候获取的是没有overdue
	 * @param user
	 * @param token
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<UserTokenBean> getUserToken(UserBean user, String token, HttpRequestInfoBean request);
	
	/**
	 * 添加token
	 * @param user
	 * @param token
	 * @param overdue 过期时间并且为空或者大于服务器时间不超过10秒以内将为无过期
	 * @param request
	 * @return
	 */
	public ResponseMap addUserToken(UserBean user, String token, Date overdue, HttpRequestInfoBean request);
}
