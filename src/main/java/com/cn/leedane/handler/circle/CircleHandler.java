package com.cn.leedane.handler.circle;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CirclesSerializeBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 圈子的处理类
 * @author LeeDane
 * 2017年5月31日 上午9:59:16
 * version 1.0
 */
@Component
public class CircleHandler {
	@Autowired
	private CircleMapper circleMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	
	/**
	 * 获取正常状态的圈子对象
	 * @param circleId
	 * @return
	 */
	public CircleBean getNormalCircleBean(int circleId, UserBean user){
		CircleBean circle = getCircleBean(circleId);
		if(circle == null || user == null
				||  (circle.getStatus() == ConstantsUtil.STATUS_SELF && circle.getCreateUserId() != user.getId()) 
				|| (circle.getStatus() != ConstantsUtil.STATUS_SELF && circle.getStatus() != ConstantsUtil.STATUS_NORMAL)){
			return null;
		}
		return circle;
	}
	
	/**
	 * 获取圈子对象(不去判断是否是正常状态的圈子)
	 * @param circleId
	 * @return
	 */
	public CircleBean getCircleBean(int circleId){
		String key = getCircleKey(circleId);
		Object obj = systemCache.getCache(key);
		CircleBean circleBean = null;
		/*for(int i = 0; i < 22; i++)
			deleteCircleBeanCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					circleBean =  (CircleBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), CircleBean.class);
					if(circleBean != null){
						systemCache.addCache(key, circleBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						circleBean = circleMapper.findById(CircleBean.class, circleId);
						if(circleBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(circleBean));
								systemCache.addCache(key, circleBean);
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
				circleBean = circleMapper.findById(CircleBean.class, circleId);
				if(circleBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(circleBean));
						systemCache.addCache(key, circleBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			circleBean = (CircleBean)obj;
		}
		
		if(circleBean == null || circleBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return circleBean;
	}
	
	/**
	 * 根据圈子ID删除该圈子的cache和redis缓存
	 * @param circleId
	 * @return
	 */
	public boolean deleteCircleBeanCache(int circleId){
		String key = getCircleKey(circleId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	
	/**
	 * 获取该用户在该圈子角色的编码
	 * @param user
	 * @param circle
	 * @return
	 */
	public int getRoleCode(UserBean user, int circle){
		return 1;
	}
	
	/**
	 * 获取该用户所有的圈子
	 * @param user
	 * @param circle
	 * @return
	 */
	public List<CircleBean> getAllCircles(UserBean user){
		return circleMapper.getAllCircles(user.getId(), ConstantsUtil.STATUS_NORMAL);
	}
	
	/**
	 * 获取热门的圈子列表
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public CirclesSerializeBean getHostest() throws IOException, ClassNotFoundException{
		String hostestKey = getHostestKey();
		CirclesSerializeBean circlesSerializeBean = new CirclesSerializeBean();
		//redisUtil.delete(hostestKey);
		//热门
		if(!redisUtil.hasKey(hostestKey)){
			circlesSerializeBean.setCircleBeans(circleMapper.getHotests(DateUtil.getDayBeforeOrAfter(OptionUtil.circleHostestBeforeDay, true), 6, ConstantsUtil.STATUS_NORMAL));
			redisUtil.addSerialize(hostestKey, SerializeUtil.serializeObject(circlesSerializeBean));
			redisUtil.expire(hostestKey, 60 * 60); //设置一个小时过期
		}else{
			circlesSerializeBean = (CirclesSerializeBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(hostestKey.getBytes()), CirclesSerializeBean.class);
		}
		return circlesSerializeBean;
	}
	
	/**
	 * 获取最新的圈子列表
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public CirclesSerializeBean getNestest() throws IOException, ClassNotFoundException{
		String newestKey = getNewestKey();
		CirclesSerializeBean circlesSerializeBean = new CirclesSerializeBean();
		//redisUtil.delete(newestKey);
		//最新
		if(!redisUtil.hasKey(newestKey)){
			circlesSerializeBean.setCircleBeans(circleMapper.getNewests(6, ConstantsUtil.STATUS_NORMAL));
			redisUtil.addSerialize(newestKey, SerializeUtil.serializeObject(circlesSerializeBean));
			redisUtil.expire(newestKey, 60); //设置一分钟过期
		}else{
			circlesSerializeBean = (CirclesSerializeBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(newestKey.getBytes()), CirclesSerializeBean.class);
		}
		return circlesSerializeBean;
	}
	
	/**
	 * 获取推荐的圈子列表
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public CirclesSerializeBean getRecommend() throws IOException, ClassNotFoundException{
		String recommendKey = getRecommendKey();
		CirclesSerializeBean circlesSerializeBean = new CirclesSerializeBean();
		//redisUtil.delete(recommendKey);
		//推荐
		if(!redisUtil.hasKey(recommendKey)){
			circlesSerializeBean.setCircleBeans(circleMapper.getRecommends(6, ConstantsUtil.STATUS_NORMAL));
			redisUtil.addSerialize(recommendKey, SerializeUtil.serializeObject(circlesSerializeBean));
			redisUtil.expire(recommendKey, 60); //设置一分钟过期
		}else{
			circlesSerializeBean = (CirclesSerializeBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(recommendKey.getBytes()), CirclesSerializeBean.class);
		}
		return circlesSerializeBean;
	}
	
	/**
	 * 获取圈子在redis的key
	 * @return
	 */
	public static String getCircleKey(int circleId){
		return ConstantsUtil.CIRCLE_REDIS + circleId;
	}
	

	/**
	 * 获取热门在redis的key
	 * @return
	 */
	public static String getHostestKey(){
		return ConstantsUtil.CIRCLE_REDIS +"hostest";
	}
	
	/**
	 * 获取最新在redis的key
	 * @return
	 */
	public static String getNewestKey(){
		return ConstantsUtil.CIRCLE_REDIS +"newest";
	}
	
	/**
	 * 获取推荐在redis的key
	 * @return
	 */
	public static String getRecommendKey(){
		return ConstantsUtil.CIRCLE_REDIS +"recommend";
	}
}
