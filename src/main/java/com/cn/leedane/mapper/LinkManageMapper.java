package com.cn.leedane.mapper;

import java.util.List;

import com.cn.leedane.model.LinkManageBean;

/**
 * 链接权限管理的mapper接口类
 * @author LeeDane
 * 2017年4月10日 下午4:51:31
 * version 1.0
 */
public interface LinkManageMapper  extends BaseMapper<LinkManageBean>{
	
	public List<LinkManageBean> getAllLinks();
}
