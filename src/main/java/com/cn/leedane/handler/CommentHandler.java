package com.cn.leedane.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.SqlBaseService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 评论的处理类
 * @author LeeDane
 * 2016年7月12日 上午11:54:18
 * Version 1.0
 */
@Component
public class CommentHandler {
	
	@Autowired
	private SqlBaseService<IDBean> sqlBaseService;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	public void addComment(String tableName, int tableId){
		/**
		 * 通过表名+ID唯一存储
		 */
		RedisUtil redisUtil = RedisUtil.getInstance();
		String key = getCommentKey(tableName, tableId);
		int count = 0;
		//还没有添加到redis中
		if(StringUtil.isNull(redisUtil.getString(key))){
			//获取数据库中所有评论的数量
			count = SqlUtil.getTotalByList(sqlBaseService.executeSQL("select count(*) ct from "+DataTableType.评论.value+" where table_name=? and table_id = ?", tableName, tableId));	
		}else{
			count = Integer.parseInt(redisUtil.getString(key)) + 1;
		}
		redisUtil.addString(key, String.valueOf(count));
	}
	
	/**
	 * 获取redis存储的key
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	private String getCommentKey(String tableName, int tableId){
		StringBuffer buffer = new StringBuffer();
		buffer.append(ConstantsUtil.COMMENT_REDIS);
		buffer.append(tableName);
		buffer.append("_");
		buffer.append(tableId);
		return buffer.toString();
	}
	

	/**
	 * 获取评论总数
	 * @param tableId
	 * @param tableName
	 * @return
	 */
	public int getCommentNumber(int tableId, String tableName){
		String commentKey = getCommentKey(tableId, tableName);
		int commentNumber;
		//评论
		if(!redisUtil.hasKey(commentKey)){
			commentNumber = sqlBaseService.getTotal(DataTableType.评论.value, "where table_id = "+tableId+" and table_name='"+tableName+"'");
			redisUtil.addString(commentKey, String.valueOf(commentNumber));
		}else{
			commentNumber = Integer.parseInt(redisUtil.getString(commentKey));
		}
		return commentNumber;
	}
	
	/**
	 * 获取该用户的总评论数
	 * @param userId
	 * @return
	 */
	public int getComments(int userId){
		return sqlBaseService.getTotalByUser(DataTableType.评论.value, userId);
	}
	
	/**
	 * 删除评论后修改评论数量
	 * @param tableId
	 * @param tableName
	 */
	public void deleteComment(int tableId, String tableName){
		String commentKey = getCommentKey(tableId, tableName);
		int commentNumber;
		//评论
		if(!redisUtil.hasKey(commentKey)){
			commentNumber = sqlBaseService.getTotal(DataTableType.评论.value, "where table_id = "+tableId+" and table_name='"+tableName+"'");
			redisUtil.addString(commentKey, String.valueOf(commentNumber));
		}else{
			commentNumber = Integer.parseInt(redisUtil.getString(commentKey));
			commentNumber = commentNumber -1;//评论减去1
			redisUtil.addString(commentKey, String.valueOf(commentNumber));
		}
	}
	
	/**
	 * 获取评论在redis的key
	 * @param id
	 * @return
	 */
	public static String getCommentKey(int tableId, String tableName){
		return ConstantsUtil.COMMENT_REDIS +tableName+"_"+tableId;
	}
}
