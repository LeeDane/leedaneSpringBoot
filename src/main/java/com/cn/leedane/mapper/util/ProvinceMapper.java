package com.cn.leedane.mapper.util;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.ReferrerRelationBean;
import com.cn.leedane.model.mall.S_PromotionSeatBean;
import com.cn.leedane.model.mall.S_ReferrerRecordBean;
import com.cn.leedane.model.util.ProvinceBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 省mapper接口类
 * @author LeeDane
 * 2017年12月7日 下午11:33:02
 * version 1.0
 */
public interface ProvinceMapper extends BaseMapper<ProvinceBean>{
    /**
     * 批量保存省列表
     * @param provinces
     * @return
     */
    public int batchSave(@Param("provinces") List<ProvinceBean> provinces);

    /**
     * 获取所有的省
     * @return
     */
    public List<ProvinceBean> getProvinces();
}
