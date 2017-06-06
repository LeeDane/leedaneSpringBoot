package com.cn.leedane.model;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 计划任务信息实体bean
 * @author LeeDane
 * 2017年6月5日 下午2:31:20
 * version 1.0
 */
public class JobManageBean extends RecordTimeBean{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -8628940168254243135L;
	
	/** 任务名称 */
	@Column(value = "job_name")
	@Field
    private String jobName;
    /** 任务分组 */
	@Column(value = "job_group")
	@Field
    private String jobGroup;
    
    /** 任务运行时间表达式 */
	@Column(value = "expression")
	@Field
    private String cronExpression;
    /** 任务描述 */
	@Column(value = "job_desc")
	@Field
    private String jobDesc;
	
	/** 任务排序 */
	@Column(value = "job_order")
	@Field
    private String jobOrder;
	
	/** 任务实体映射名称 */
	@Column(value = "class_name")
	@Field
    private String className;
   
    public String getJobName() {
        return jobName;
    }
    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getJobGroup() {
        return jobGroup;
    }
    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }
    public String getCronExpression() {
        return cronExpression;
    }
    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
	public String getJobDesc() {
		return jobDesc;
	}
	public void setJobDesc(String jobDesc) {
		this.jobDesc = jobDesc;
	}
	public String getJobOrder() {
		return jobOrder;
	}
	public void setJobOrder(String jobOrder) {
		this.jobOrder = jobOrder;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
   
}
