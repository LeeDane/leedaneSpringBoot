package com.cn.leedane.mapper.clock;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

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
	 * 获取成员列表(只是按照创建时间做了排序)
	 * @param clockId
	 * @return
	 */
	public List<ClockMemberBean> members(@Param("clockId")int clockId);
	
	/**
	 * 判断成员是否在任务成员中
	 * (已取消从这个位置判断的方式，请参考 {@link com.cn.leedane.handler.clock.ClockMemberHandler #inMember(int, int)})
	 * @param clockId
	 * @param userId
	 * @return
	 */
	@Deprecated
	public List<ClockMemberBean> inMember(@Param("clockId")int clockId, @Param("userId")int userId);

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
	 * 获取该成员在任务的记录（不校验状态）
	 * @param clockId
	 * @param memberId
	 * @return
	 */
	public ClockMemberBean findClockMember(@Param("clockId")int clockId, @Param("memberId")int memberId);

	/**
	 * 获取任务的成员列表(通过打卡次数进行排序)
	 * @param clockId
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> membersSortByIns(@Param("clockId")int clockId, @Param("limit")int limit);
}
