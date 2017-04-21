package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.PermissionBean;

/**
 * 权限管理mapper接口类
 * @author LeeDane
 * 2017年4月17日 下午3:52:08
 * version 1.0
 */
public interface PermissionMapper extends BaseMapper<PermissionBean>{
	public List<Map<String, Object>> roles(@Param("pmid")int pmid, @Param("status") int status);
}
