package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import com.cn.leedane.model.ChatBgUserBean;

/**
 * 聊天背景与用户关系相关mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:06:56
 * Version 1.0
 */
public interface ChatBgUserMapper extends BaseMapper<ChatBgUserBean>{
	
	/**
	 * 判断是否下载过
	 * @param userId
	 * @param chatBgTableId
	 * @return
	 */
	public List<Map<String, Object>> hasDownload(long userId, long chatBgTableId);
}
