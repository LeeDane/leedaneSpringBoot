package com.cn.leedane.mapper.mall;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.S_OrderDetailBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 第三方平台订单的汇总管理mapper接口类
 * @author LeeDane
 * 2019年12月7日 下午11:33:02
 * version 1.0
 */
public interface S_OrderDetailMapper extends BaseMapper<S_OrderDetailBean>{

    /**
     * 批量更新数据，有重复的数据存在就更新
     * @param list
     * @return 返回受影响的数据
     */
    public int insertByBatchOnDuplicate(@Param("list") List<S_OrderDetailBean> list);

}
