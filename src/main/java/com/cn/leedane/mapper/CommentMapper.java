package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

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
	 * @param tableId
	 * @param status
	 * @param start
	 * @param pageSize
	 * @return
	 */
	//public boolean updateSQL(String sql, Object ... obj);
	
	public List<Map<String, Object>> getMessageBoards(@Param("tableId") int tableId, @Param("status") int status, 
			@Param("start")int start, @Param("pageSize") int pageSize);
	
	
	/**
	 * 获取该用户的全部评论
	 * @param userId
	 * @param status
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getAllByUser(@Param("userId") int userId, @Param("status") int status, 
			@Param("start")int start, @Param("pageSize") int pageSize);

	/**
	 * 获取该表资源的全部评论
	 * @param tableName
	 * @param tableId
	 * @param status
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> getAllByTable(
			@Param("tableName") String tableName, 
			@Param("tableId") int tableId, 
			@Param("status") int status, 
			@Param("start")int start, 
			@Param("pageSize") int pageSize);

	/**
	 * 获取关系的等级字符串
	 * @param pid
	 * @return
	 */
	public String getLevel(@Param("pid") int pid);
}
