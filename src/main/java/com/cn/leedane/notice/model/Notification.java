package com.cn.leedane.notice.model;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.utils.EnumUtil;

/**
 * @author LeeDane
 * 2019年12月24日 12:39
 * Version 1.0
 */
public class Notification extends IDBean {

    private boolean notifyMySelf;

    /**
     * 发送通知的用户
     */
    private long fromUserId;

    /**
     * 接收通知的用户
     */
    private long toUserId;

    /**
     * 消息的内容
     */
    private String content;

    /**
     * 通知的类型
     */
    private EnumUtil.NotificationType type;

    /**
     * 关联的表名
     */
    private String tableName;

    /**
     * 关联的表ID
     */
    private long tableId;

    /**
     * 可以为空
     */
    private Object objectBean;

    public boolean isNotifyMySelf() {
        return notifyMySelf;
    }

    public void setNotifyMySelf(boolean notifyMySelf) {
        this.notifyMySelf = notifyMySelf;
    }

    public long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public long getToUserId() {
        return toUserId;
    }

    public void setToUserId(long toUserId) {
        this.toUserId = toUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public EnumUtil.NotificationType getType() {
        return type;
    }

    public void setType(EnumUtil.NotificationType type) {
        this.type = type;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public Object getObjectBean() {
        return objectBean;
    }

    public void setObjectBean(Object objectBean) {
        this.objectBean = objectBean;
    }
}
