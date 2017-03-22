package com.cn.leedane.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.model.SignInBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.SignInService;

/**
 * 签到的处理类
 * @author LeeDane
 * 2016年3月31日 下午1:42:31
 * Version 1.0
 */
@Component
public class SignInHandler {
	
	@Autowired
	private SignInService<SignInBean> signInService;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	

	public void setSignInService(SignInService<SignInBean> signInService) {
		this.signInService = signInService;
	}
	
	/**
	 * 判断当前的用户是否是第一次签到(第一次签到有赠送大积分)
	 * @param userId
	 * @return
	 */
	public boolean hasHistorySign(int userId){
		String firstSignInKey = getIsFirstSignInKey(userId);
		boolean isFirst = false;
		//是否第一次
		if(redisUtil.hasKey(firstSignInKey)){
			String first = redisUtil.getString(firstSignInKey);
			isFirst = Boolean.parseBoolean(first);
		}else{
			isFirst = signInService.hasHistorySign(userId);
		}
		return isFirst;
	}
	
	/**
	 * 增加历史签到记录标记
	 * @param userId
	 */
	public void addHistorySignIn(int userId){
		String firstSignInKey = getIsFirstSignInKey(userId);
		redisUtil.addString(firstSignInKey, String.valueOf("true"));
	}
	
	/**
	 * isFirst签到在redis的key
	 * @param id
	 * @return
	 */
	public static String getIsFirstSignInKey(int userId){
		return ConstantsUtil.FIRST_SIGN_IN_REDIS +userId;
	}
}
