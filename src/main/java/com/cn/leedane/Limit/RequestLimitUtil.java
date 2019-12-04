package com.cn.leedane.Limit;

import java.util.Map;

/**
 * 对请求的时间做限制
 */
public class RequestLimitUtil {

    /**
     * 校验对象是否过期
     * @param implement
     */
    public void check(RequestLimitImplement implement){

        Map<String, Object> params = implement.params();
        if(params == null || params.isEmpty())
            return;



    }

}
