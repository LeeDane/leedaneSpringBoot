package com.cn.leedane.mapper.circle;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.circle.CircleSettingBean;

/**
 * 圈子设置mapper接口类
 * @author LeeDane
 * 2017年6月13日 下午1:32:35
 * version 1.0
 */
public interface CircleSettingMapper extends BaseMapper<CircleSettingBean>{

	/**
	 * 获取圈子的设置
	 * @param circleId
	 * @return
	 */
	public List<CircleSettingBean> getSetting(@Param("circleId") long circleId);

}
