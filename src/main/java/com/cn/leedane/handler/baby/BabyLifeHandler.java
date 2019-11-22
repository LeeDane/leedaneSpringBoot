package com.cn.leedane.handler.baby;

import java.io.IOException;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.mapper.baby.BabyLifeMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.baby.BabyBean;
import com.cn.leedane.model.baby.BabyLifeBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 宝宝生活方式的处理类
 * @author LeeDane
 * 2018年6月12日 上午9:19:39
 * version 1.0
 */
@Component
public class BabyLifeHandler {
	@Autowired
	private BabyLifeMapper babyLifeMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private BabyHandler babyHandler;
	
	
	/**
	 * 获取正常状态的宝宝生活方式对象
	 * 
	 * @param babyId
	 * @return
	 */
	/**
	 * 获取正常状态的宝宝生活方式对象
	 * 不是自己的宝宝或者自己的宝宝生活方式对象将抛出权限异常
	 * @param babyId
	 * @param lifeId
	 * @param user
	 * @return
	 */
	public BabyLifeBean getNormalBabyLife(long babyId, long lifeId, UserBean user){
		BabyLifeBean baby = getBabyLife(babyId, lifeId, user);
		
		//不是自己的宝宝将抛出权限异常
		if(baby == null || user == null
				||  (baby.getStatus() != ConstantsUtil.STATUS_NORMAL) 
				|| baby.getCreateUserId() != user.getId()){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		}
		return baby;
	}
	
	/**
	 * 获取宝宝生活方式对象(不去判断是否是正常状态的宝宝)
	 * @param babyId
	 * @return
	 */
	public BabyLifeBean getBabyLife(long babyId, long lifeId, UserBean user){
		babyHandler.getNormalBaby(babyId, user);
		
		if(lifeId < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该宝宝生活方式记录不存在.value));
		
		String key = getBabyLifeKey(babyId, lifeId);
		Object obj = systemCache.getCache(key);
		BabyLifeBean babyLifeBean = null;
		/*for(int i = 0; i < 22; i++)
			deleteBabyBeanCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					babyLifeBean =  (BabyLifeBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), BabyLifeBean.class);
					if(babyLifeBean != null){
						systemCache.addCache(key, babyLifeBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						babyLifeBean = babyLifeMapper.findById(BabyLifeBean.class, babyId);
						if(babyLifeBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(babyLifeBean));
								systemCache.addCache(key, babyLifeBean);
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
				babyLifeBean = babyLifeMapper.findById(BabyBean.class, babyId);
				if(babyLifeBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(babyLifeBean));
						systemCache.addCache(key, babyLifeBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			babyLifeBean = (BabyLifeBean)obj;
		}
		
		if(babyLifeBean == null || babyLifeBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return babyLifeBean;
	}
	
	/**
	 * 根据宝宝以及其生活方式ID删除该宝宝生活方式的cache和redis缓存
	 * @param babyId
	 * @return
	 */
	public boolean deleteBabyLifeCache(long babyId, long lifeId){
		String key = getBabyLifeKey(babyId, lifeId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取宝宝生活方式在redis的key
	 * @param babyId
	 * @param lifeId
	 * @return
	 */
	public static String getBabyLifeKey(long babyId, long lifeId){
		return ConstantsUtil.BABY_REDIS + babyId +"_l_"+ lifeId;
	}
}
