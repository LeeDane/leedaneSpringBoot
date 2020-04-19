package com.cn.leedane.model;

import java.util.Date;

/**
 * 注销账号记录实体类
 * @author LeeDane
 * 2020年4月11日 下午9:39:24
 * Version 1.0
 */
public class LogoutBean extends RecordTimeBean{

    /**
     * 注销原因
     */
    private String reason;

    /**
     * 备注信息
     */
    private String note;

    /**
     * 过期时间
     */
    private Date overdue;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Date getOverdue() {
        return overdue;
    }

    public void setOverdue(Date overdue) {
        this.overdue = overdue;
    }
}
