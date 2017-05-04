package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.NotificationBean;

/**
 * 通知的mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:13:23
 * Version 1.0
 */
public interface NotificationMapper extends BaseMapper<NotificationBean>{

	/**
	 * 分页获取通知
	 * @param pmid
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> paging(@Param("toUserId") int toUserId, @Param("type") String type, @Param("status") int status, 
			@Param("start")int start, @Param("pageSize") int pageSize);
	
	/**
	 * 更新该类型的全部为已读
	 * @param type
	 * @return
	 */
	public int updateAllRead(@Param("type") String type, @Param("read") boolean read);
	
	/**
	 * 获取消息列表
	 * @param toUserId
	 * @param read true表示已读， false表示未读
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> noReadNumber(@Param("toUserId") int toUserId, @Param("read") boolean read, @Param("status") int status);
	
}
