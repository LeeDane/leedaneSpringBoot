package com.cn.leedane.mapper;

import com.cn.leedane.model.LogoutBean;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 注销账号记录mapper接口类
 * @author LeeDane
 * 2020年4月11日 下午9:39:24
 * Version 1.0
 */
public interface LogoutMapper extends BaseMapper<LogoutBean>{
	/**
	 * 获取用户的注销记录
	 * @param userId 用户ID
	 * @return
	 */
	public LogoutBean recode(@Param("userId") long userId);
}
