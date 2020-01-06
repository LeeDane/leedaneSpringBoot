package com.cn.leedane.mapper.util;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.util.CityBean;
import com.cn.leedane.model.util.ProvinceBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 市mapper接口类
 * @author LeeDane
 * 2017年12月7日 下午11:33:02
 * version 1.0
 */
public interface CityMapper extends BaseMapper<CityBean>{
    /**
     * 批量保存市列表
     * @param citys
     * @return
     */
    public int batchSave(@Param("citys") List<CityBean> citys);

    /**
     * 获取所有的市
     * @return
     */
    public List<CityBean> getCitys();
}
