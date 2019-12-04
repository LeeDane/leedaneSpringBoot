package com.cn.leedane.handler.baby;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.mapper.baby.BabyMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.baby.BabyBean;
import com.cn.leedane.model.baby.BabyBeans;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 宝宝的处理类
 * @author LeeDane
 * 2018年6月5日 下午3:56:11
 * version 1.0
 */
@Component
public class BabyHandler {
	@Autowired
	private BabyMapper babyMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	
	/**
	 * 获取正常状态的宝宝对象
	 * 不是自己的宝宝将抛出权限异常
	 * @param babyId
	 * @return
	 */
	public BabyBean getNormalBaby(long babyId, UserBean user){
		BabyBean baby = getBaby(babyId);
		
		//不是自己的宝宝将抛出权限异常
		if(baby == null || user == null
				||  (baby.getStatus() != ConstantsUtil.STATUS_NORMAL) 
				|| baby.getCreateUserId() != user.getId()){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		}
		return baby;
	}
	
	/**
	 * 获取宝宝对象(不去判断是否是正常状态的宝宝)
	 * @param babyId
	 * @return
	 */
	public BabyBean getBaby(long babyId){
		if(babyId < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该宝宝不存在.value));
		String key = getBabyKey(babyId);
		Object obj = systemCache.getCache(key);
		BabyBean babyBean = null;
		/*for(int i = 0; i < 22; i++)
			deleteBabyBeanCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					babyBean =  (BabyBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), BabyBean.class);
					if(babyBean != null){
						systemCache.addCache(key, babyBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						babyBean = babyMapper.findById(BabyBean.class, babyId);
						if(babyBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(babyBean));
								systemCache.addCache(key, babyBean);
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
				babyBean = babyMapper.findById(BabyBean.class, babyId);
				if(babyBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(babyBean));
						systemCache.addCache(key, babyBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			babyBean = (BabyBean)obj;
		}
		
		if(babyBean == null || babyBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return babyBean;
	}
	
	/**
	 * 获取该用户的所有宝宝对象
	 * @param userId
	 * @return
	 */
	public List<BabyBean> getBabys(long userId){
		String key = getBabysKey(userId);
		Object obj = systemCache.getCache(key);
		BabyBeans babyBeans = null;
		/*for(int i = 0; i < 22; i++)
			deleteBabyBeansCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					babyBeans =  (BabyBeans) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), BabyBeans.class);
					if(babyBeans != null){
						systemCache.addCache(key, babyBeans);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<BabyBean> list = babyMapper.getMyBabys(userId, ConstantsUtil.STATUS_NORMAL);
						if(CollectionUtil.isNotEmpty(list)){
							try {
								babyBeans = new BabyBeans();
								babyBeans.setBabys(list);
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(babyBeans));
								systemCache.addCache(key, babyBeans);
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
				List<BabyBean> list = babyMapper.getMyBabys(userId, ConstantsUtil.STATUS_NORMAL);
				if(CollectionUtil.isNotEmpty(list)){
					try {
						babyBeans = new BabyBeans();
						babyBeans.setBabys(list);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(babyBeans));
						systemCache.addCache(key, babyBeans);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			babyBeans = (BabyBeans)obj;
		}
		
		if(babyBeans != null ){
			return babyBeans.getBabys();
		}
		return new ArrayList<BabyBean>();
	}
	
	
	/**
	 * 根据宝宝ID删除该宝宝的cache和redis缓存
	 * @param babyId
	 * @return
	 */
	public boolean deleteBabyBeanCache(long babyId){
		String key = getBabyKey(babyId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 根据用户ID删除该用户所有宝宝的cache和redis缓存
	 * @return
	 */
	public boolean deleteBabyBeansCache(long userId){
		String key = getBabysKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}

	/**
	 * 获取宝宝在redis的key
	 * @return
	 */
	public static String getBabyKey(long babyId){
		return ConstantsUtil.BABY_REDIS + babyId;
	}
	
	/**
	 * 获取所有宝宝在redis的key
	 * @return
	 */
	public static String getBabysKey(long userId){
		return ConstantsUtil.BABYS_REDIS + userId;
	}
	
}
