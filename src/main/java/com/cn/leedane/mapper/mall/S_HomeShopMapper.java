package com.cn.leedane.mapper.mall;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_HomeShopBean;

/**
 * 首页商店接口类
 * @author LeeDane
 * 2018年1月11日 下午2:27:45
 * version 1.0
 */
public interface S_HomeShopMapper extends BaseMapper<S_HomeShopBean>{
	/**
	 * 获取首页商店列表
	 * @param status
	 * @param limit
	 * @return
	 */
	public List<S_HomeShopBean> shops(
			@Param("status") int status,
			@Param("limit") int limit);
	
}
