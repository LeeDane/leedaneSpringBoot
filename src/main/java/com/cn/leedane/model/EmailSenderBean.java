package com.cn.leedane.model;

import java.util.Date;
import java.util.Set;

/**
 * 邮件发送者实体类
 * @author LeeDane
 * 2016年7月12日 上午10:43:57
 * Version 1.0
 */
public class EmailSenderBean extends IDBean{

	private static final long serialVersionUID = 1L;

	/**
	 * 邮件发送者地址，如smtp.163.com
	 */
	private String host;

	/**
	 * 端口
	 */
	private String port;
	/**
	 * 邮件发送者名称
	 */
	private String name;
	
	/**
	 * 邮件发送者邮箱
	 */
	private String email;
	
	/**
	 * 邮件发送者的token
	 */
	private String token;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
