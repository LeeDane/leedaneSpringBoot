package com.cn.leedane.Limit;

import java.util.Map;

/**
 * 请求限制的接口
 */
public interface RequestLimitImplement {
    /**
     * 请求限制的时间
     */
    long time();

    /**
     * 请求参数
     */
    Map<String, Object> params();

    /**
     * 超过请求限制时间后的执行
     */
    void error(Map<String, Object> response);

    /**
     * 没有超过请求限制的执行
     */
    void success(Map<String, Object> response);
}
