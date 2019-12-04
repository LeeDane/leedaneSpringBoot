package com.cn.leedane.mapper.mall;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_HomeItemProductBean;

/**
 * 首页分类mapper接口类
 * @author LeeDane
 * 2017年12月26日 下午2:39:53
 * version 1.0
 */
public interface S_HomeItemProductMapper extends BaseMapper<S_HomeItemProductBean>{
	/**
	 * 根据分类id获取首页对应的商品列表
	 * @return
	 */
	public List<S_HomeItemProductBean> getProducts(
			@Param("itemId") long itemId
			,@Param("limit") int limit);

	/**
	 * 删除该项关联的商品记录
	 * @param itemId
	 */
	public void deleteProducts(@Param("itemId")long itemId);
	
}
