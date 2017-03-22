package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import com.cn.leedane.model.OperateLogBean;

/**
 * 操作日志的mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:13:51
 * Version 1.0
 */
public interface OperateLogMapper extends BaseMapper<OperateLogBean>{
	
	/**
	 * 分页获取单表的全部字段的数据
	 * 注意：1、这个只获取单表
	 *      2、获取的是该表的全部字段
	 * @param tableName  表名
	 * @param where  where语句，参数需直接填写在字符串中
	 * @param pageSize  每页条数
	 * @param pageNo  第几页
	 * @return
	 */
	public List<Map<String, Object>> getlimits(String tableName, String where, int pageSize, int pageNo);
}
