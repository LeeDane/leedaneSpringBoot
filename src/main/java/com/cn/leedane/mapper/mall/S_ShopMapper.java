package com.cn.leedane.mapper.mall;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_ShopBean;

/**
 * 商店mapper接口类
 * @author LeeDane
 * 2017年11月15日 上午9:47:20
 * version 1.0
 */
public interface S_ShopMapper extends BaseMapper<S_ShopBean>{
	/**
	 * 获取商店
	 * @param userId
	 * @return
	 */
	public List<S_ShopBean> getShop(@Param("shopId") int shopId, @Param("status") int status);
	
}
