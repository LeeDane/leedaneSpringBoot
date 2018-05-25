package com.cn.leedane.mapper.mall;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_OrderBean;

/**
 * 订单mapper接口类
 * @author LeeDane
 * 2017年12月7日 下午11:33:02
 * version 1.0
 */
public interface S_OrderMapper extends BaseMapper<S_OrderBean>{
	/**
	 * 获取用户的未处理订单数量
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getNoDealNumber(@Param("userId") int userId);
	
	/**
	 * 分页获取订单列表
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> paging(@Param("userId") int userId, @Param("status") int status, 
			@Param("start")int start, @Param("pageSize") int pageSize);
	
}
