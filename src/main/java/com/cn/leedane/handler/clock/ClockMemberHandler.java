package com.cn.leedane.handler.clock;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.mapper.clock.ClockMemberMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.clock.ClockBean;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.model.clock.ClockMembersBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.CommonUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 任务成员的处理类
 * @author LeeDane
 * 2018年8月7日 上午11:39:34
 * version 1.0
 */
@Component
public class ClockMemberHandler {
	@Autowired
	private ClockMapper clockMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private ClockMemberMapper clockMemberMapper;
	
	@Autowired
	private ClockHandler clockHandler;
	
	@Autowired 
	private UserHandler userHandler;
	/**
	 * 获取成员列表
	 * @param clockId
	 * @return
	 */
	public List<ClockMemberBean> members(int clockId){
		ClockBean clock = clockHandler.getNormalClock(clockId);
		if(clock == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该提醒任务不存在或者不支持共享.value));
		String key = getClockMembersKey(clockId);
		Object obj = systemCache.getCache(key);
		ClockMembersBean clockMembersBean = null;
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					clockMembersBean =  (ClockMembersBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), ClockMembersBean.class);
					if(clockMembersBean != null){
						systemCache.addCache(key, clockMembersBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<ClockMemberBean> memberBeans = clockMemberMapper.members(clockId);
						if(CollectionUtil.isNotEmpty(memberBeans)){
							try {
								setMemberAgeAndSex(memberBeans);
								clockMembersBean = new ClockMembersBean();
								clockMembersBean.setMembers(memberBeans);
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(clockMembersBean));
								systemCache.addCache(key, clockMembersBean);
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
				List<ClockMemberBean> memberBeans = clockMemberMapper.members(clockId);
				if(CollectionUtil.isNotEmpty(memberBeans)){
					try {
						setMemberAgeAndSex(memberBeans);
						clockMembersBean = new ClockMembersBean();
						clockMembersBean.setMembers(memberBeans);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(clockMembersBean));
						systemCache.addCache(key, clockMembersBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			clockMembersBean = (ClockMembersBean)obj;
		}
		return clockMembersBean.getMembers();
	}
	
	/**
	 * 判断成员是否在任务成员列表中
	 * @param userId
	 * @param clockId
	 * @return
	 */
	public boolean inMember(int userId, int clockId){
		List<ClockMemberBean> members = members(clockId);
		if(CollectionUtil.isNotEmpty(members)){
			for(ClockMemberBean member: members){
				if(member.getMemberId() != userId)
					continue;
				else
					return true;
			}
		}
		return false;
	}
	
	/**
	 * 查找成员的年龄和性别
	 * @param memberBeans
	 */
	private void setMemberAgeAndSex(List<ClockMemberBean> memberBeans) {
		for(ClockMemberBean member: memberBeans){
			UserBean user = userHandler.getUserBean(member.getMemberId());
			member.setAge(CommonUtil.getAgeByUser(user));
			member.setSex(user.getSex());
		}
	}


	/**
	 * 删除任务成员列表的cache和redis缓存
	 * @return
	 */
	public boolean deleteClockMembersCache(int clockId){
		String key = getClockMembersKey(clockId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	
	/**
	 * 获取任务成员列表在redis的key
	 * @return
	 */
	public static String getClockMembersKey(int clockId){
		return ConstantsUtil.CLOCK_MEMBERS_REDIS + clockId;
	}
}
