package com.cn.leedane.mapper;

import com.cn.leedane.model.ChatSquareBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 聊天广场的mapper接口类
 * @author LeeDane
 * 2017年2月10日 下午4:35:02
 * Version 1.0
 */
public interface ChatSquareMapper extends BaseMapper<ChatSquareBean>{
    /**
     * 分页获取聊天记录
     * @param lastId
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> paging(
            @Param("status")int status,
            @Param("last")long lastId,
            @Param("pageSize") int pageSize);
}
