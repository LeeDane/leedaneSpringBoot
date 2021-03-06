package com.cn.leedane.handler.circle;

import java.io.IOException;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.exception.MustLoginException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.mapper.circle.CircleMemberMapper;
import com.cn.leedane.mapper.circle.CircleSettingMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CircleSettingBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.SqlUtil;

/**
 * 圈子设置的处理类
 * @author LeeDane
 * 2017年7月7日 下午6:03:14
 * version 1.0
 */
@Component
public class CircleSettingHandler {
	@Autowired
	private CircleSettingMapper circleSettingMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private CircleMemberMapper circleMemberMapper;
	
	
	/**
	 * 获取正常状态的圈子对象(私有状态也能返回正常的实体)
	 * 获取为空将抛异常
	 * @param circleId
	 * @return
	 */
	public CircleSettingBean getNormalSettingBean(long circleId, UserBean user){
		CircleSettingBean settingBean = null;
		String key = getCircleSettingKey(circleId);
		Object obj = systemCache.getCache(key);
		//deleteSettingBeanCache(circleId);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					settingBean =  (CircleSettingBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), CircleBean.class);
					if(settingBean != null){
						systemCache.addCache(key, settingBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<CircleSettingBean> circleSettingBeans = circleSettingMapper.getSetting(circleId);
						if(CollectionUtil.isEmpty(circleSettingBeans))
							throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
						
						settingBean = circleSettingBeans.get(0);
						if(settingBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(settingBean));
								systemCache.addCache(key, settingBean);
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
				List<CircleSettingBean> circleSettingBeans = circleSettingMapper.getSetting(circleId);
				if(CollectionUtil.isEmpty(circleSettingBeans))
					throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
				
				settingBean = circleSettingBeans.get(0);
				try {
					redisUtil.addSerialize(key, SerializeUtil.serializeObject(settingBean));
					systemCache.addCache(key, settingBean);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			settingBean = (CircleSettingBean)obj;
		}
		
		//获取圈子成员列表
		
		if(settingBean.getStatus() == ConstantsUtil.STATUS_SELF){
			
			if(user == null)
				throw new MustLoginException();
			//判断是否在成员中,不在其中是没有操作实例的
			if(!SqlUtil.getBooleanByList(circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL))){
				throw new UnauthorizedException();
			}
			
		}else if(settingBean == null || (settingBean.getStatus() != ConstantsUtil.STATUS_SELF && settingBean.getStatus() != ConstantsUtil.STATUS_NORMAL)){
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		}
		return settingBean;
	}
	
	
	/**
	 * 根据圈子设置ID删除该圈子的cache和redis缓存
	 * @param circleId
	 * @return
	 */
	public boolean deleteSettingBeanCache(long circleId){
		String key = getCircleSettingKey(circleId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取圈子在redis的key
	 * @return
	 */
	public static String getCircleSettingKey(long circleId){
		return ConstantsUtil.CIRCLE_REDIS  +circleId +"_st";
	}
}
