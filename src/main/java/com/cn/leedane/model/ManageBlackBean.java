package com.cn.leedane.model;

import java.util.Date;

/**
 * 黑名单实体类
 * @author LeeDane
 * 2020年4月19日 下午3:39:24
 * Version 1.0
 */
public class ManageBlackBean extends RecordTimeBean{

    /**
     * 拉黑的ID
     */
    private long userId;

    /**
     * 授权信息，为空表示所有的功能都不可用
     */
    private String authorization;


    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }
}
