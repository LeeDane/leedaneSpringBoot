package com.cn.leedane.mapper.circle;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.circle.CirclePostBean;

/**
 * 帖子mapper接口类
 * @author LeeDane
 * 2017年6月20日 下午5:57:54
 * version 1.0
 */
public interface CirclePostMapper extends BaseMapper<CirclePostBean>{

	/**
	 * 获取帖子对象
	 * @param postId
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> getCirclePost(@Param("postId")int postId, @Param("status")int status);
	
	/**
	 * 获取帖子对象图片
	 * @param postId
	 * @param status
	 * @return
	 */
	public List<Map<String, Object>> getCirclePostImgs(@Param("postId")int postId, @Param("status")int status);
}
