package com.cn.leedane.model;


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
	 * 操作方式，0：网页端，1：手机端
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
	 * 操作的浏览器名称
	 */
	private String browser;
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
}
