package com.cn.leedane.mapper.clock;

import com.cn.leedane.display.clock.ClockInResourceDisplay;
import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.clock.ClockInResourcesBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务打卡的图片mapper接口类
 * @author LeeDane
 * 2018年9月11日 下午9:55:13
 * version 1.0
 */
public interface ClockInResourcesMapper extends BaseMapper<ClockInResourcesBean>{
    /**
     * 获取资源列表
     * @param clockId
     * @param resourceType
     * @param start
     * @param pageSize 为0表示获取全部
     * @return
     */
    public List<ClockInResourceDisplay> resources(@Param("clockId")long clockId, @Param("resourceType")int resourceType,
                                                  @Param("start")int start,
                                                  @Param("pageSize")int pageSize);
//	/**
//	 * 获得指定日期范围内指定任务的打卡情况
//	 * @param clockId
//	 * @param startDate
//	 * @param end
//	 * @return
//	 */
//	public List<Map<String, String>> getClockInsRangeDate(
//            @Param("clockId") int clockId,
//            @Param("start") String startDate,
//            @Param("end") String end);

}
