package com.cn.leedane.mapper;

import com.cn.leedane.model.OptionBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 选项配置的mapper接口类
 * @author LeeDane
 * 2019年12月2日 上午8:14:25
 * Version 1.0
 */
public interface OptionManageMapper extends BaseMapper<OptionBean>{
    /**
     * 判断记录是否存在
     * @param key
     * @param version
     * @return
     */
    public List<Map<String, Object>> isExist(@Param("key")String key, @Param("version")float version);

    /**
     * 分页获取任务列表
     * @param start
     * @param pageSize
     * @return
     */
    public List<Map<String, Object>> paging(@Param("start")int start, @Param("pageSize")int pageSize, @Param("status") int status);
}
