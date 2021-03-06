package com.cn.leedane.mapper.mall;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_ProductBean;

/**
 * 商品mapper接口类
 * @author LeeDane
 * 2017年11月7日 下午5:21:10
 * version 1.0
 */
public interface S_ProductMapper extends BaseMapper<S_ProductBean>{
	/**
	 * 获取商品
	 * @return
	 */
	public List<S_ProductBean> getProduct(@Param("productId") long productId, @Param("status") int status);
	
	/**
	 * 获取所有的商品
	 * @return
	 */
	public List<Map<String, Object>> getProducts();
	
}
