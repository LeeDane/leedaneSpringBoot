package com.cn.leedane.handler;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.SignInMapper;
import com.cn.leedane.model.SignInMarkBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 签到的处理类
 * @author LeeDane
 * 2016年3月31日 下午1:42:31
 * Version 1.0
 */
@Component
public class SignInHandler {
	
	@Autowired
	private SignInMapper signInMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();

	@Autowired
	private SystemCache systemCache;
	/**
	 * 获取所有签到标记的列表
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> marks(long userId){
		SignInMarkBean datas = null;
		String key = getSignInMarkKey(userId);
		Object obj = systemCache.getCache(key);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					datas =  (SignInMarkBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), SignInMarkBean.class);
					if(datas != null){
						systemCache.addCache(key, datas, true);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<Map<String, Object>> data = signInMapper.getSignInMark(userId);
						if(CollectionUtil.isNotEmpty(data)){
							try {
								datas = new SignInMarkBean();
								datas.setList(data);
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(datas));
								systemCache.addCache(key, datas, true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				List<Map<String, Object>> data = signInMapper.getSignInMark(userId);
				if(CollectionUtil.isNotEmpty(data)){
					try {
						datas = new SignInMarkBean();
						datas.setList(data);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(datas));
						systemCache.addCache(key, datas, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			datas = (SignInMarkBean) obj;
		}
		return datas != null ? datas.getList(): new ArrayList<>();
	}


	/**
	 * 根据用户ID删除该用户的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deleteSignInMarkKey(long userId){
		String key = getSignInMarkKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}

	/**
	 * 获取用户签到列表在redis的key
	 * @return
	 */
	public static String getSignInMarkKey(long userId){
		return ConstantsUtil.SIGNIN_MARK_REDIS + "_"+ userId;
	}

	/**
	 * 判断当前的用户是否是第一次签到(第一次签到有赠送大积分)
	 * @param userId
	 * @return
	 */
	public boolean hasHistorySign(long userId){
		String firstSignInKey = getIsFirstSignInKey(userId);
		boolean isFirst = false;
		//是否第一次
		if(redisUtil.hasKey(firstSignInKey)){
			String first = redisUtil.getString(firstSignInKey);
			isFirst = Boolean.parseBoolean(first);
		}else{
			isFirst = SqlUtil.getBooleanByList(this.signInMapper.hasHistorySign(userId));
		}
		return isFirst;
	}
	
	/**
	 * 增加历史签到记录标记
	 * @param userId
	 */
	public void addHistorySignIn(long userId){
		String firstSignInKey = getIsFirstSignInKey(userId);
		redisUtil.addString(firstSignInKey, String.valueOf("true"));
	}
	
	/**
	 * isFirst签到在redis的key
	 * @param userId
	 * @return
	 */
	public static String getIsFirstSignInKey(long userId){
		return ConstantsUtil.FIRST_SIGN_IN_REDIS +userId;
	}
}
