package com.cn.leedane.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.UserTokenBean;

/**
 * 用户token关系的mapper接口类
 * @author LeeDane
 * 2017年3月24日 下午1:11:15
 * version 1.0
 */
public interface UserTokenMapper  extends BaseMapper<UserTokenBean>{
	
	public List<UserTokenBean> getUserToken(
				@Param("createUserId") int createUserId, 
				@Param("status") int status, 
				@Param("token") String token, 
				@Param("serverTime") Date serverTime);
}
