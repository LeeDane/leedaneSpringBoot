package com.cn.leedane.mapper.shop;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.shop.S_WishBean;

/**
 * 商品心愿单mapper接口类
 * @author LeeDane
 * 2017年11月13日 下午6:57:32
 * version 1.0
 */
public interface S_WishMapper extends BaseMapper<S_WishBean>{
	/**
	 * 获取用户的心愿单
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> getNumber(@Param("userId") int userId, @Param("status") int status);
	
	/**
	 * 分页获取心愿单列表
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> paging(@Param("userId") int userId, @Param("status") int status, 
			@Param("start")int start, @Param("pageSize") int pageSize);
	
}
