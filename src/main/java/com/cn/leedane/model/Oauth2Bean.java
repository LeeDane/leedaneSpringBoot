package com.cn.leedane.model;

import com.cn.leedane.mybatis.table.annotation.Column;
import org.apache.solr.client.solrj.beans.Field;

/**
 * 第三方授权的实体bean
 * @author LeeDane
 * 2017年6月5日 下午2:31:20
 * version 1.0
 */
public class Oauth2Bean extends RecordTimeBean{
	/** 平台名称 */
	@Column(value = "platform")
    private String platform;

    /** 所在平台关联的ID */
	@Column(value = "oauth2_id")
    private long oauth2Id;

	/** 所在平台的open_id */
	@Column(value = "open_id")
	private String openId;

	/** 所在平台的name, 可能为空*/
	@Column(value = "name")
	private String name;


	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public long getOauth2Id() {
		return oauth2Id;
	}

	public void setOauth2Id(long oauth2Id) {
		this.oauth2Id = oauth2Id;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
