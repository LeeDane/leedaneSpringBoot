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
			circlesSerializeBean.setCircleBeans(circleMapper.getHotests(DateUtil.getDayBeforeOrAfter(OptionUtil.circleHostestBeforeDay, true), 6));
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
			circlesSerializeBean.setCircleBeans(circleMapper.getNewests(6));
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
			circlesSerializeBean.setCircleBeans(circleMapper.getRecommends(6));
			redisUtil.addSerialize(recommendKey, SerializeUtil.serializeObject(circlesSerializeBean));
			redisUtil.expire(recommendKey, 60); //设置一分钟过期
		}else{
			circlesSerializeBean = (CirclesSerializeBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(recommendKey.getBytes()), CirclesSerializeBean.class);
		}
		return circlesSerializeBean;
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
