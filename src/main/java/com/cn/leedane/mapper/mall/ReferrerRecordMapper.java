package com.cn.leedane.mapper.mall;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.mall.ReferrerRelationBean;
import com.cn.leedane.model.mall.S_ReferrerRecordBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 推荐关系mapper接口类
 * @author LeeDane
 * 2017年12月7日 下午11:33:02
 * version 1.0
 */
public interface ReferrerRecordMapper extends BaseMapper<S_ReferrerRecordBean>{

    /**
     * 获取用户的推荐人
     * @param userId
     * @return
     */
    public S_ReferrerRecordBean findReferrer(@Param("userId")long userId);

    /**
     * 获取推荐关系
     * @param userId
     * @param level
     * @return
     */
    public List<ReferrerRelationBean> getReferrerRelation(@Param("userId")long userId, @Param("level")int level);
}
