package com.cn.leedane.mapper.mall;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_BigEventBean;

/**
 * 商品大事件mapper接口类
 * @author LeeDane
 * 2017年11月10日 下午12:43:00
 * version 1.0
 */
public interface S_BigEventMapper extends BaseMapper<S_BigEventBean>{
	/**
	 * 获取大事件列表
	 * @return
	 */
	public List<Map<String, Object>> getEvents(@Param("productId") long productId, @Param("status") int status,
			@Param("start")int start, @Param("pageSize") int pageSize);
	
}
