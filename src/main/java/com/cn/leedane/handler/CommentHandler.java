package com.cn.leedane.handler;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.CommentMapper;
import com.cn.leedane.model.CommentBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 评论的处理类
 * @author LeeDane
 * 2016年7月12日 上午11:54:18
 * Version 1.0
 */
@Component
public class CommentHandler {
	
	@Autowired
	private CommentMapper commentMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();

	@Autowired
	private SystemCache systemCache;
	/**
	 * 添加评论
	 * @param tableName
	 * @param tableId
	 */
	public void addComment(String tableName, int tableId){
		String key = getCommentKey(tableName, tableId);
		int count = 0;
		//还没有添加到redis中
		if(StringUtil.isNull(redisUtil.getString(key))){
			//获取数据库中所有评论的数量
			count = SqlUtil.getTotalByList(commentMapper.executeSQL("select count(*) ct from "+DataTableType.评论.value+" where table_name=? and table_id = ? and status = ?", tableName, tableId, ConstantsUtil.STATUS_NORMAL));
		}else{
			count = Integer.parseInt(redisUtil.getString(key)) + 1;
		}
		redisUtil.addString(key, String.valueOf(count));
	}

	/**
	 * 获取评论缓存对象
	 * @param commentId
	 * @return
	 */
	public CommentBean getComment(int commentId){
		CommentBean commentBean = null;
		String key = getCommentKey(commentId);
		Object obj = systemCache.getCache(key);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					commentBean =  (CommentBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), CommentBean.class);
					if(commentBean != null){
						systemCache.addCache(key, commentBean, true);
					}else{
						commentBean =  commentMapper.findById(CommentBean.class, commentId);
						if(commentBean != null) {
							systemCache.addCache(key, commentBean, true);
							redisUtil.addSerialize(key, SerializeUtil.serializeObject(commentBean));
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				commentBean =  commentMapper.findById(CommentBean.class, commentId);
				if(commentBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(commentBean));
						systemCache.addCache(key, commentBean, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			commentBean = (CommentBean) obj;
		}
		return commentBean;
	}

	/**
	 * 获取评论总数
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public int getCommentNumber(String tableName, int tableId){
		String commentKey = getCommentKey(tableName, tableId);
		int commentNumber;
		//评论
		if(!redisUtil.hasKey(commentKey)){
			commentNumber = SqlUtil.getTotalByList(commentMapper.getTotal(DataTableType.评论.value, "where table_id = "+tableId+" and table_name='"+tableName+"' and status = " + ConstantsUtil.STATUS_NORMAL));
			redisUtil.addString(commentKey, String.valueOf(commentNumber));
		}else{
			commentNumber = Integer.parseInt(redisUtil.getString(commentKey));
		}
		return commentNumber < 1 ? 0: commentNumber;
	}
	
	/**
	 * 获取该用户的总评论数
	 * @param userId
	 * @return
	 */
	public int getComments(int userId){
		return SqlUtil.getTotalByList(commentMapper.getTotalByUser(DataTableType.评论.value, userId));
	}
	
	/**
	 * 删除评论后修改评论数量
	 * @param tableName
	 * @param tableId
	 */
	public void deleteComment(String tableName, int tableId){
		String commentKey = getCommentKey(tableName, tableId);
		int commentNumber;
		//评论
		if(!redisUtil.hasKey(commentKey)){
			commentNumber = SqlUtil.getTotalByList(commentMapper.getTotal(DataTableType.评论.value, "where table_id = "+tableId+" and table_name='"+tableName+"' and status = "+ ConstantsUtil.STATUS_NORMAL));
			redisUtil.addString(commentKey, String.valueOf(commentNumber));
		}else{
			commentNumber = Integer.parseInt(redisUtil.getString(commentKey));
			commentNumber = commentNumber -1;//评论减去1
			redisUtil.addString(commentKey, String.valueOf(commentNumber));
		}
	}

	/**
	 * 获取评论在redis的key
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public static String getCommentKey(String tableName, int tableId){
		return ConstantsUtil.COMMENT_REDIS +tableName+"_"+tableId;
	}

	/**
	 * 根据用户ID删除该评论的cache和redis缓存
	 * @return
	 */
	public boolean deleteComment(int commentId){
		String key = getCommentKey(commentId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}

	/**
	 * 获取该评论在redis的key
	 * @return
	 */
	public static String getCommentKey(int commentId){
		return ConstantsUtil.COMMENT_REDIS + "id"+ commentId;
	}
}
