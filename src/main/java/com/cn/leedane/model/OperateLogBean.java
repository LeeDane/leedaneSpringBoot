package com.cn.leedane.model;


import com.cn.leedane.mybatis.table.annotation.Column;
import org.apache.solr.client.solrj.beans.Field;

/**
 * 操作日志实体类
 * @author LeeDane
 * 2016年7月12日 上午10:48:29
 * Version 1.0
 */
//@Table(name="T_OPERATE_LOG")
public class OperateLogBean extends RecordTimeBean {
	
	private static final long serialVersionUID = 1L;
	//状态，0：失败，1：正常，2：未知异常
	/**
	 * 标题，如XX登录系统
	 */
	private String subject;
	/**
	 * 操作时间
	 */
	private long operateTime;
	
	/**
	 * 操作方式，0：接口端，1：网页端
	 */
	private int operateType;
	
	/**
	 * 方式类型：register,login等
	 */
	private String method;
	
	/**
	 * 操作的ip地址
	 */
	private String ip;

	/**
	 * 操作的地址信息
	 */
	private String location;

	/**
	 * 操作的浏览器名称
	 */
	private String browser;

	/**
	 * 是否被添加到es索引中
	 */
	@Field
	@Column("es_index")
	private boolean esIndex;

	/**
	 * 排序字段，根据从大到小排序，大于0表示置顶字段
	 */
	private int stick;

	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	//@Column(name="operate_time")
	public long getOperateTime() {
		return operateTime;
	}
	public void setOperateTime(long operateTime) {
		this.operateTime = operateTime;
	}
	
	//@Column(name="operate_type" )
	public int getOperateType() {
		return operateType;
	}
	public void setOperateType(int operateType) {
		this.operateType = operateType;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getBrowser() {
		return browser;
	}
	public void setBrowser(String browser) {
		this.browser = browser;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}

	public boolean isEsIndex() {
		return esIndex;
	}

	public void setEsIndex(boolean esIndex) {
		this.esIndex = esIndex;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public int getStick() {
		return stick;
	}

	public void setStick(int stick) {
		this.stick = stick;
	}
}
