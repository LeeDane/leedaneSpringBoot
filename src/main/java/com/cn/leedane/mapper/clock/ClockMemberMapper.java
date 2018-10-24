package com.cn.leedane.mapper.clock;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.display.clock.ClockMemberDisplay;
import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.clock.ClockMemberBean;

/**
 * 任务成员mapper接口类
 * @author LeeDane
 * 2018年10月8日 下午6:02:33
 * version 1.0
 */
public interface ClockMemberMapper extends BaseMapper<ClockMemberBean>{

	/**
	 * 获取成员列表
	 * @param clockId
	 * @return
	 */
	public List<ClockMemberBean> members(@Param("clockId")int clockId);

	/**
	 * 退出任务
	 * @param clockId
	 * @param userId
	 * @return
	 */
	public boolean exitClock(@Param("clockId")int clockId
			, @Param("userId")int userId
			, @Param("modifyUserId")int modifyUserId
			, @Param("status")int status);

	/**
	 * 修改任务成员表的状态
	 * @param clockId
	 * @param memberId
	 * @return
	 */
	public boolean updateStatus(@Param("clockId")int clockId, @Param("memberId")int memberId, @Param("status")int statu);
}
