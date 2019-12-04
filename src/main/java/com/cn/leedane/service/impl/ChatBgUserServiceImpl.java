package com.cn.leedane.service.impl;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.mapper.ChatBgUserMapper;
import com.cn.leedane.model.ChatBgUserBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.service.ChatBgUserService;
import com.cn.leedane.service.OperateLogService;

/**
 * 聊天信息列表service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:21:50
 * Version 1.0
 */
@Service("chatBgUserService")
public class ChatBgUserServiceImpl implements ChatBgUserService<ChatBgUserBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private ChatBgUserMapper chatBgUserMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public boolean exists(int userId, int chatBgTableId) {
		logger.info("ChatBgUserServiceImpl-->exists():userId="+userId+",chatBgTableId="+chatBgTableId);
		return chatBgUserMapper.executeSQL("select id from "+DataTableType.聊天背景与用户.value+" where create_user_id=? and chat_bg_table_id=?", userId, chatBgTableId).size() > 0;
	}
}
