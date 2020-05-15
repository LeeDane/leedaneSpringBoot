package com.cn.leedane.model;

import java.util.Date;

/**
 * 事件提醒实体类
 * @author LeeDane
 * 2020年5月15日 下午3:39:24
 * Version 1.0
 */
public class ManageRemindBean extends RecordTimeBean{

    /**
     * 事件的名称(显示)
     */
    private String name;

    /**
     * 事件的周期
     */
    private String cycle;

    /**
     * 事件的时间
     */
    private String time;

    /**
     * 事件的触发表达式(周期加time处理的结果)
     */
    private String cron;

    /**
     * 结束时间(可以为空)
     */
    private Date end;

    /**
     * 事件的类型，如吃药提醒事件，吃药再次提醒事件
     */
    private String type;

    /**
     * 事件的方式，如短信，站内通知，邮件等
     */
    private String way;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCycle() {
        return cycle;
    }

    public void setCycle(String cycle) {
        this.cycle = cycle;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
