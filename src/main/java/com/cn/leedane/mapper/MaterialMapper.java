package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.MaterialBean;
/**
 * 素材mapper接口类
 * @author LeeDane
 * 2017年5月22日 上午10:14:01
 * version 1.0
 */
public interface MaterialMapper extends BaseMapper<MaterialBean>{
	
	/**
	 * 批量添加
	 * @param data
	 */
	public void insertByBatch(List<Map<String, Object>> data);
	
	/**
	 * 检查该图片是否已经加入图库
	 * @param user
	 * @param path
	 * @return
	 */
	public List<Map<String, Object>> isExist(@Param("userId")long userId, @Param("path")String path);
	
	/**
	 * 分页获取素材列表
	 * @param createUserId
	 * @param start
	 * @param pageSize
	 * @param type
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> getMaterialByLimit(
			@Param("createUserId")long createUserId,
			@Param("start")int start, 
			@Param("pageSize")int pageSize, 
			@Param("type")String type, 
			@Param("status") int status);
}
