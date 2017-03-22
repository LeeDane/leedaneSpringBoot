package com.cn.leedane.mapper;

import java.util.List;

import com.cn.leedane.model.FilePathBean;

/**
 * 文件路径mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:10:56
 * Version 1.0
 */
public interface FilePathMapper extends BaseMapper<FilePathBean>{
	
	/**
	 * 基础更新实体的方法
	 * @param sql
	 * @param params
	 * @return
	 */
	//public boolean update(String sql, Object ... params);
	
	/**
	 * 基础根据表ID删除的方法
	 * @param tableName  表的名称
	 * @param id 表中的id字段的值
	 * @return
	 * @throws Exception
	 */
	//public boolean deleteById(String tableName, final int id);
	
	/**
	 * 执行HQL获得实体
	 * @param beanName  实体名称如：xxxBean
	 * @param where where语句，参数需直接填写在字符串中
	 * @return
	 */
	//public List<FilePathBean> executeHQL(String beanName, String where);
	
	/**
	 * 批量更新uuid
	 * @param oldUuids
	 * @param newUuids
	 * @return
	 * @throws Exception
	 */
	public boolean updateBatchFilePath(final String[] oldUuids, final String[] newUuids);
	
	/**
	 * 获取单个心情的多张图片，多个用用“，” 分割开(存在bug)
	 * @param tableUUid
	 * @param tableName
	 * @param picSize
	 * @return
	 * @throws Exception
	 */
	//public String getMoodImg(String tableUUid, String tableName, String picSize);
	
	/**
	 * 更新标记该文件已经上传到存储服务器
	 * @param fId
	 * @param qiniuPath
	 * @return
	 */
	//public boolean updateUploadQiniu(int fId, String qiniuPath);
}
