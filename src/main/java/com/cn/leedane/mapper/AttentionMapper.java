package com.cn.leedane.mapper;

import com.cn.leedane.model.AttentionBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 关注的mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:03:44
 * Version 1.0
 */
public interface AttentionMapper extends BaseMapper<AttentionBean>{

    /**
     * 分页获取我的关注列表
     * @param userId
     * @param start
     * @param rows
     * @return
     */
    public List<Map<String, Object>> getMyAttentions(@Param("userId")long userId, @Param("start")int start, @Param("limit")int rows);
}
