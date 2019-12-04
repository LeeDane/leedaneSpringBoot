package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.LinkManageBean;

/**
 * 链接权限管理的mapper接口类
 * @author LeeDane
 * 2017年4月10日 下午4:51:31
 * version 1.0
 */
public interface LinkManageMapper  extends BaseMapper<LinkManageBean>{
	
	public List<LinkManageBean> getAllLinks(@Param("status") int status);
	
	/**
	 * 分页获取链接列表
	 * @param start
	 * @param pageSize
	 * @return
	 */
	public List<Map<String, Object>> paging(@Param("start")int start, @Param("pageSize")int pageSize, @Param("status") int status);
	
	/**
	 * 获取该链接的全部角色
	 * @param lnid
	 * @return
	 */
	public List<Map<String, Object>> roles(@Param("lnid")long lnid, @Param("status") int status);
	
	/**
	 * 获取该链接的全部权限
	 * @param lnid
	 * @return
	 */
	public List<Map<String, Object>> permissions(@Param("lnid")long lnid, @Param("status") int status);
}
