package com.cn.leedane.handler.circle;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.circle.CircleMemberMapper;
import com.cn.leedane.model.circle.CirclesMemberSerializeBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 圈子成员的处理类
 * @author LeeDane
 * 2017年6月20日 上午10:39:43
 * version 1.0
 */
@Component
public class CircleMemberHandler {
	@Autowired
	private CircleMemberMapper circleMemberMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	/**
	 * 获取圈子的热门成员列表
	 * @param circleId
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public CirclesMemberSerializeBean getHostest(long circleId) throws IOException, ClassNotFoundException{
		String hostestKey = getHostestMemberKey(circleId);
		CirclesMemberSerializeBean circlesMemberSerializeBean = new CirclesMemberSerializeBean();
		//redisUtil.delete(hostestKey);
		//热门
		if(!redisUtil.hasKey(hostestKey)){
			circlesMemberSerializeBean.setCircleMemberBeans(circleMemberMapper.getHotests(circleId, DateUtil.getDayBeforeOrAfter(OptionUtil.circleHostestBeforeDay, true), 4, ConstantsUtil.STATUS_NORMAL));
			redisUtil.addSerialize(hostestKey, SerializeUtil.serializeObject(circlesMemberSerializeBean));
			redisUtil.expire(hostestKey, 60 * 60); //设置一个小时过期
		}else{
			circlesMemberSerializeBean = (CirclesMemberSerializeBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(hostestKey.getBytes()), CirclesMemberSerializeBean.class);
		}
		return circlesMemberSerializeBean;
	}
	
	/**
	 * 获取圈子的最新成员列表
	 * @param circleId
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public CirclesMemberSerializeBean getNestest(long circleId) throws IOException, ClassNotFoundException{
		String newestKey = getNewestMemberKey(circleId);
		CirclesMemberSerializeBean circlesMemberSerializeBean = new CirclesMemberSerializeBean();
		//redisUtil.delete(newestKey);
		//最新
		if(!redisUtil.hasKey(newestKey)){
			circlesMemberSerializeBean.setCircleMemberBeans(circleMemberMapper.getNewests(circleId, 4, ConstantsUtil.STATUS_NORMAL));
			redisUtil.addSerialize(newestKey, SerializeUtil.serializeObject(circlesMemberSerializeBean));
			redisUtil.expire(newestKey, 60); //设置一分钟过期
		}else{
			circlesMemberSerializeBean = (CirclesMemberSerializeBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(newestKey.getBytes()), CirclesMemberSerializeBean.class);
		}
		return circlesMemberSerializeBean;
	}
	
	/**
	 * 获取圈子推荐的用户列表
	 * @return
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 */
	public CirclesMemberSerializeBean getRecommend(long circleId) throws IOException, ClassNotFoundException{
		String recommendKey = getRecommendMemberKey(circleId);
		CirclesMemberSerializeBean circlesMemberSerializeBean = new CirclesMemberSerializeBean();
		//redisUtil.delete(recommendKey);
		//推荐
		if(!redisUtil.hasKey(recommendKey)){
			circlesMemberSerializeBean.setCircleMemberBeans(circleMemberMapper.getRecommends(circleId, 4, ConstantsUtil.STATUS_NORMAL));
			redisUtil.addSerialize(recommendKey, SerializeUtil.serializeObject(circlesMemberSerializeBean));
			redisUtil.expire(recommendKey, 60); //设置一分钟过期
		}else{
			circlesMemberSerializeBean = (CirclesMemberSerializeBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(recommendKey.getBytes()), CirclesMemberSerializeBean.class);
		}
		return circlesMemberSerializeBean;
	}
	
	/**
	 * 根据圈子id删除其对应的最新成员的cache和redis缓存
	 * @param circleId
	 * @return
	 */
	public boolean deleteNewestMemberCache(long circleId){
		String key = getNewestMemberKey(circleId);
		redisUtil.delete(key);
		return true;
	}
	
	/**
	 * 根据圈子id删除其对应的人热门成员的cache和redis缓存
	 * @param circleId
	 * @return
	 */
	public boolean deleteHostestMemberCache(long circleId){
		String key = getHostestMemberKey(circleId);
		redisUtil.delete(key);
		return true;
	}
	
	/**
	 * 根据圈子id删除其对应的推荐成员的cache和redis缓存
	 * @param circleId
	 * @return
	 */
	public boolean deleteRecommendMemberCache(long circleId){
		String key = getRecommendMemberKey(circleId);
		redisUtil.delete(key);
		return true;
	}
	
	/**
	 * 获取圈子热门成员在redis的key
	 * @param circleId
	 * @return
	 */
	public static String getHostestMemberKey(long circleId){
		return "t_member_hostest_"+ circleId;
	}
	
	/**
	 * 获取圈子最新成员在redis的key
	 * @param circleId
	 * @return
	 */
	public static String getNewestMemberKey(long circleId){
		return "t_member_newest_"+ circleId;
	}
	
	/**
	 * 获取圈子推荐成员在redis的key
	 * @param circleId
	 * @return
	 */
	public static String getRecommendMemberKey(long circleId){
		return "t_member_recommend_"+ circleId;
	}
}
