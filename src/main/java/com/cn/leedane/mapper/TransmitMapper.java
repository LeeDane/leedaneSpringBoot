package com.cn.leedane.mapper;

import com.cn.leedane.model.TransmitBean;

/**
 * 转发的mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:18:49
 * Version 1.0
 */
public interface TransmitMapper extends BaseMapper<TransmitBean>{
	
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
}
