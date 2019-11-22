package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.CategoryBean;

/**
 * 分类管理的mapper接口类
 * @author LeeDane
 * 2017年6月28日 下午4:55:26
 * version 1.0
 */
public interface CategoryMapper extends BaseMapper<CategoryBean>{

	/**
	 * 获取直接下一级节点
	 * @param pid
	 * @param userId
	 * @return
	 */
	public List<Map<String, Object>> children(
			@Param("pid")long pid,
			@Param("userId")long userId);

	/**
	 * 判断该节点是否能被登录用户删除的
	 * @param id
	 * @param id
	 * @return
	 */
	public List<Map<String, Object>> canDelete(
			@Param("id")long id,
			@Param("userId")long userId);

	public List<Map<String, Object>> getParentCategorys(@Param("cid")long cid);

}
