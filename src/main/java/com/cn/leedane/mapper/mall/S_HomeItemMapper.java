package com.cn.leedane.mapper.mall;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_HomeItemBean;

/**
 * 首页分类mapper接口类
 * @author LeeDane
 * 2017年12月26日 下午2:39:53
 * version 1.0
 */
public interface S_HomeItemMapper extends BaseMapper<S_HomeItemBean>{
	/**
	 * 获取首页分类列表
	 * @return
	 */
	public List<S_HomeItemBean> showCategoryList();

	/**
	 * 根据分类id查找该分类的数据
	 * @param categoryId
	 * @return
	 */
	@Deprecated
	public List<S_HomeItemBean> findItemByCategoryId(@Param("categoryId") long categoryId);

	/**
	 * 根据分类项id获取分类项
	 * @param itemId
	 * @return
	 */
	public List<S_HomeItemBean> findItemById(@Param("itemId") long itemId);
	
}
