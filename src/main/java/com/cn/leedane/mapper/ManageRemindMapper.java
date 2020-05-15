package com.cn.leedane.mapper;

import com.cn.leedane.model.ManageRemindBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 事件提醒mapper接口类
 * @author LeeDane
 * 2020年5月15日 下午16:39:24
 * Version 1.0
 */
public interface ManageRemindMapper extends BaseMapper<ManageRemindBean>{
	/**
	 * 获取所有事件提醒列表
	 * @return
	 */
	public List<ManageRemindBean> all();

	/**
	 * 分页获取事件提醒列表
	 * @param userId 用户ID
	 * @return
	 */
	public List<Map<String, Object>> reminds(@Param("userId") long userId, @Param("start") int start, @Param("limit") int rows);
}
