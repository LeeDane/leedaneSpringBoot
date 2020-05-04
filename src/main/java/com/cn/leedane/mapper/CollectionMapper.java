package com.cn.leedane.mapper;

import com.cn.leedane.model.CollectionBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 收藏夹的mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:08:06
 * Version 1.0
 */
public interface CollectionMapper extends BaseMapper<CollectionBean>{
    /**
     * 分页获取我的收藏记录
     * @param userId
     * @param start
     * @param rows
     * @return
     */
    public List<Map<String, Object>> getMyCollections(@Param("userId")long userId, @Param("start")int start, @Param("limit")int rows);
}
