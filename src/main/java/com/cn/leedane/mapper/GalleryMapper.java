package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.GalleryBean;
/**
 * 图库mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:12:12
 * Version 1.0
 */
public interface GalleryMapper extends BaseMapper<GalleryBean>{
	
	/**
	 * 检查该图片是否已经加入图库
	 * @param user
	 * @param path
	 * @return
	 */
	public List<Map<String, Object>> isExist(@Param("userId")int userId, @Param("path")String path);
}
