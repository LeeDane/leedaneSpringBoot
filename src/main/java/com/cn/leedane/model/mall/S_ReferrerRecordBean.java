package com.cn.leedane.model.mall;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

/**
 * 推荐记录实体bean
 * @author LeeDane
 * 2017年12月11日 下午3:31:21
 * version 1.0
 */
@Table(value="t_mall_referrer_record")
public class S_ReferrerRecordBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//推荐人的状态：0：禁用， 1：正常  2：删除

	@Column("user_id")
	private long userId;

	/**
	 * 推荐人的生成码,冗余字段
	 */
	@Column("code")
	private String code;

	/**
	 *
	 * 用户名称，不需要存在数据库中
	 */
	@Column(required = false)
	private String name;

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
