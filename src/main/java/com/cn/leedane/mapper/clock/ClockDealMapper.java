package com.cn.leedane.mapper.clock;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.display.clock.ClockMemberDisplay;
import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.clock.ClockDealBean;
import com.cn.leedane.model.clock.ClockMemberBean;

/**
 * 任务成员关系处理mapper接口类
 * @author LeeDane
 * 2018年10月23日 下午3:05:45
 * version 1.0
 */
public interface ClockDealMapper extends BaseMapper<ClockDealBean>{
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

	/**
	 * 获取我的申请加入任务列表
	 * @param userId
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<ClockMemberDisplay> addClocks(@Param("userId")int userId, @Param("start")int start, @Param("pageSize")int pageSize);
	
	/**
	 * 获取邀请我加入的任务列表
	 * @param userId
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<ClockMemberDisplay> inviteClocks(@Param("userId")int userId, @Param("start")int start, @Param("pageSize")int pageSize);
	
	/**
	 * 获取我邀请的任务列表
	 * @param userId
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<ClockMemberDisplay> myInviteClocks(@Param("userId")int userId, @Param("start")int start, @Param("pageSize")int pageSize);
	
	/**
	 * 获取我的同意加入任务列表
	 * @param userId
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<ClockMemberDisplay> agreeClocks(@Param("userId")int userId, @Param("start")int start, @Param("pageSize")int pageSize);
	
}
