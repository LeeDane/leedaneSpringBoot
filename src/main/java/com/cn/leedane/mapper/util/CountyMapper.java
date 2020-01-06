package com.cn.leedane.mapper.util;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.util.CityBean;
import com.cn.leedane.model.util.CountyBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 县mapper接口类
 * @author LeeDane
 * 2017年12月7日 下午11:33:02
 * version 1.0
 */
public interface CountyMapper extends BaseMapper<CountyBean>{
    /**
     * 批量保存市列表
     * @param countys
     * @return
     */
    public int batchSave(@Param("countys") List<CountyBean> countys);
}
