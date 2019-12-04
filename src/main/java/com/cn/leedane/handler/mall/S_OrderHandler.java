package com.cn.leedane.handler.mall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.mall.S_OrderMapper;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 订单的处理类
 * @author LeeDane
 * 2017年12月7日 下午11:32:17
 * version 1.0
 */
@Component
public class S_OrderHandler {
	@Autowired
	private S_OrderMapper orderMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	
	/**
	 * 获取未处理订单数量对象
	 * @param userId
	 * @return
	 */
	public int getNoDealOrderNumber(long userId){
		//deleteNoDealOrderCache(userId);
		String key = getNoDealOrderKey(userId);
		Object obj = systemCache.getCache(key);
		int number = 0;
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				number =  StringUtil.changeObjectToInt(redisUtil.getString(key));
				systemCache.addCache(key, number);
			}else{//redis没有的处理
				number = SqlUtil.getTotalByList(orderMapper.getNoDealNumber(userId));
				redisUtil.addString(key, number +"");
				redisUtil.expire(key, 1000 * 60 * 60 * 24); //设置24个小时后过期
				systemCache.addCache(key, number);
			}
		}else{
			number = StringUtil.changeObjectToInt(obj);
		}
		return number;
	}
	
	/**
	 * 根据用户ID删除该用户的未处理订单的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deleteNoDealOrderCache(long userId){
		String key = getNoDealOrderKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}

	/**
	 * 判断订单记录是否存在
	 * @param platform
	 * @param orderCode
	 * @return
	 */
	public boolean inRecode(String platform, String orderCode) {
		return false;
	}
	
	/**
	 * 获取未处理订单在redis的key
	 * @param userId
	 * @return
	 */
	public static String getNoDealOrderKey(long userId){
		return ConstantsUtil.NO_DEAL_ORDER_REDIS + userId;
	}


}
