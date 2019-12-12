package com.cn.leedane.mapper;

import com.cn.leedane.model.Oauth2Bean;
import com.cn.leedane.model.mall.S_OrderDetailBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 第三方平台授权mapper接口类
 * @author LeeDane
 * 2019年12月7日 下午11:33:02
 * version 1.0
 */
public interface Oauth2Mapper extends BaseMapper<Oauth2Bean>{

    /**
     * 绑定授权
     * @param openId
     * @param oauth2Id
     * @param platform
     * @param createUserId
     * @return
     */
    public int bind(@Param("openId") String openId, @Param("oauth2Id") long oauth2Id, @Param("platform") String platform, @Param("createUserId") long createUserId);

}
