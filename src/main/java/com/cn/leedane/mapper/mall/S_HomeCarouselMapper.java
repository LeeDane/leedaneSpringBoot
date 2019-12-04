package com.cn.leedane.mapper.mall;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_HomeCarouselBean;

/**
 * 首页轮播mapper接口类
 * @author LeeDane
 * 2017年12月26日 下午2:39:53
 * version 1.0
 */
public interface S_HomeCarouselMapper extends BaseMapper<S_HomeCarouselBean>{
	/**
	 * 获取轮播商品列表
	 * @param userId
	 * @return
	 */
	public List<S_HomeCarouselBean> carousel(
			@Param("status") int status,
			@Param("limit") int limit);
	
}
