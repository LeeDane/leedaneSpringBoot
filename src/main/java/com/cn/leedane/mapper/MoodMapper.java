package com.cn.leedane.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.cn.leedane.model.MoodBean;

/**
 * 心情mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:12:40
 * Version 1.0
 */
@Repository
public interface MoodMapper extends BaseMapper<MoodBean>{
	
	/**
	 * 基础更新SQL的方法
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	//public boolean updateSQL(String sql, Object ... obj);
	
	/**
	 * 获取总数
	 * @param tableName  表名
	 * @param where where后面语句，参数需直接填写在字符串中
	 * @return
	 */
	//public int getTotal(String tableName, String where);
	
	/**
	 * 摇一摇搜索心情
	 * @param statusNormal
	 * @return
	 */
	public int shakeSearch(@Param("createUserId")int createUserId, @Param("status")int status);

}
