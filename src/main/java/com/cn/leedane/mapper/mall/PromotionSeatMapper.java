package com.cn.leedane.mapper.mall;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_PromotionSeatBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 推广位管理mapper接口类
 * @author LeeDane
 * 2019年12月7日 下午11:33:02
 * version 1.0
 */
public interface PromotionSeatMapper extends BaseMapper<S_PromotionSeatBean>{
	/**
	 * 分页获取推广位列表
	 * @param platform
	 * @param status
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> paging(@Param("platform") String platform, @Param("status") int status,
                                            @Param("start") int start, @Param("pageSize") int pageSize);
	
}
