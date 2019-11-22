package com.cn.leedane.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.ChatBean;

/**
 * 聊天相关mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:07:31
 * Version 1.0
 */
public interface ChatMapper extends BaseMapper<ChatBean>{
	/**
	 * 获取登录用户的全部与其有过聊天记录的用户的最新一条聊天信息
	 * @param toUserId
	 * @param status
	 * @return
	 */
	public List<ChatBean> getOneChatByAllUser(@Param("to_user_id") long toUserId, @Param("status") int status);
}
