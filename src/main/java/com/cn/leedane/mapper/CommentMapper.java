package com.cn.leedane.mapper;

import com.cn.leedane.model.CommentBean;

/**
 * 评论的mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:08:44
 * Version 1.0
 */
public interface CommentMapper extends BaseMapper<CommentBean>{
	
	/**
	 * 基础更新SQL的方法
	 * @param sql
	 * @param obj
	 * @return
	 * @throws Exception
	 */
	//public boolean updateSQL(String sql, Object ... obj);
}
