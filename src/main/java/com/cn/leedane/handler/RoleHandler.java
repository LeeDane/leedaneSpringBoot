package com.cn.leedane.handler;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.mapper.RoleMapper;
import com.cn.leedane.model.RoleUsersBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 角色的处理类
 * @author LeeDane
 * 2019年8月5日 下午10:24:12
 * Version 1.0
 */
@Component
public class RoleHandler {

	@Autowired
	private RoleMapper roleMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	/**
	 * 获取角色对应的用户列表对象
	 * @param roleId
	 * @return
	 */
	public List<Map<String, Object>> getUsers(int roleId){
		List<Map<String, Object>> users = null;
		String key = getRoleUsersKey(roleId);
		Object obj = systemCache.getCache(key);
		RoleUsersBean roleUsersBean = null;
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					roleUsersBean =  (RoleUsersBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), RoleUsersBean.class);
					if(roleUsersBean != null){
						systemCache.addCache(key, roleUsersBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<Map<String, Object>> rs = roleMapper.usersByRole(roleId, ConstantsUtil.STATUS_NORMAL);
						if(CollectionUtil.isEmpty(rs))
							rs = new ArrayList<>();
						try {
							roleUsersBean = new RoleUsersBean();
							roleUsersBean.setUsers(rs);
							redisUtil.addSerialize(key, SerializeUtil.serializeObject(roleUsersBean));
							systemCache.addCache(key, roleUsersBean);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				List<Map<String, Object>> rs = roleMapper.usersByRole(roleId, ConstantsUtil.STATUS_NORMAL);
				if(CollectionUtil.isEmpty(rs))
					rs = new ArrayList<>();
				try {
					roleUsersBean = new RoleUsersBean();
					roleUsersBean.setUsers(rs);
					redisUtil.addSerialize(key, SerializeUtil.serializeObject(roleUsersBean));
					systemCache.addCache(key, roleUsersBean);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else{
			roleUsersBean = (RoleUsersBean)obj;
		}
		
		if(roleUsersBean == null){
			return new ArrayList<>();
		}
		return roleUsersBean.getUsers();
	}
	
	
	/**
	 * 根据用户ID删除该用户的cache和redis缓存
	 * @param roleId
	 * @return
	 */
	public boolean deleteRoleUsersCache(int roleId){
		String key = getRoleUsersKey(roleId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取角色对应用户在redis的key
	 * @return
	 */
	public static String getRoleUsersKey(int roleId){
		return ConstantsUtil.ROLE_REDIS  + roleId;
	}
}
