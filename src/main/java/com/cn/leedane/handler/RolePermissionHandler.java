package com.cn.leedane.handler;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.UserRoleMapper;
import com.cn.leedane.model.RoleBean;
import com.cn.leedane.model.RolesBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.StringUtil;

@Component
public class RolePermissionHandler {
	/**
	 * 系统级别的缓存对象
	 */
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private UserRoleMapper userRoleMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	/**
	 * 获取用户的权限列表
	 * @param userId
	 * @return
	 */
	public List<RoleBean> getUserRoleBeans(int userId){
		String key = getRolePermissionKey(userId);
		Object obj = systemCache.getCache(key);
		RolesBean rolesBean = null;
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					rolesBean =  (RolesBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), RolesBean.class);
					if(rolesBean != null){
						systemCache.addCache(key, rolesBean);
						return rolesBean.getRoleBeans();
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						rolesBean = new RolesBean();
						rolesBean.setRoleBeans(userRoleMapper.getUserRoleBeans(userId, ConstantsUtil.STATUS_NORMAL));
						if(CollectionUtil.isNotEmpty(rolesBean.getRoleBeans())){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(rolesBean));
								systemCache.addCache(key, rolesBean);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				rolesBean = new RolesBean();
				rolesBean.setRoleBeans(userRoleMapper.getUserRoleBeans(userId, ConstantsUtil.STATUS_NORMAL));
				if(CollectionUtil.isNotEmpty(rolesBean.getRoleBeans())){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(rolesBean));
						systemCache.addCache(key, rolesBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			rolesBean = (RolesBean)obj;
		}
		
		
		return rolesBean.getRoleBeans();
	}
	
	/**
	 * 根据用户id删除其对应的权限的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deleteByUser(int userId){
		String key = getRolePermissionKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}

	/**
	 * isFirst签到在redis的key
	 * @param id
	 * @return
	 */
	public static String getRolePermissionKey(int userId){
		return ConstantsUtil.ROLE_PERMISSION_REDIS +userId;
	}
}
