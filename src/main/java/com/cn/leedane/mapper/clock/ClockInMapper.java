package com.cn.leedane.mapper.clock;

import com.cn.leedane.display.clock.ClockInDisplay;
import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.clock.ClockInBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 任务打卡mapper接口类
 * @author LeeDane
 * 2018年9月11日 下午9:55:13
 * version 1.0
 */
public interface ClockInMapper extends BaseMapper<ClockInBean>{
	/**
	 * 获得指定日期范围内指定任务的打卡情况
	 * @param clockId
	 * @param startDate
	 * @param end
	 * @return
	 */
	public List<Map<String, String>> getClockInsRangeDate(
			@Param("clockId")long clockId,
			@Param("start")String startDate, 
			@Param("end")String end);

	/**
	 * 获得任务Top排行的成员(只有打卡记录的成员才会找到)
	 * 已经取消这个方法的使用，请参考 {@link com.cn.leedane.mapper.clock.ClockMemberMapper #membersSortByIns(int, int)})
	 * @param clockId
	 * @param limit 0表示获取全部
	 * @return
	 */
	@Deprecated
	public List<Map<String, Object>> getTopMember(@Param("clockId")long clockId, @Param("limit")int limit);

	/**
	 * 获得任务在指定日期的打卡情况
	 * @param clockId
	 * @param date
	 * @param limit
	 * @return
	 */
	public List<Map<String, Object>> membersSortByIns(@Param("clockId")long clockId, @Param("date")String date, @Param("limit")int limit);

	/**
	 * 获取用户打卡信息(不校验状态)
	 * @param clockId
	 * @param toUserId
	 * @param date
	 * @return
	 */
	public ClockInDisplay getUserClockIn(@Param("clockId")long clockId, @Param("toUserId")long toUserId, @Param("date")String date);
}
