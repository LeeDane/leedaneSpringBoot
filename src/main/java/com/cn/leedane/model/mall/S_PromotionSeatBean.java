package com.cn.leedane.model.mall;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

import java.util.Date;

/**
 * 推广位实体bean
 * @author LeeDane
 * 2017年12月7日 下午10:50:32
 * version 1.0
 */
@Table(value="t_mall_promotion_seat")
public class S_PromotionSeatBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 推广位ID(为了兼容拼多多的，必须用字符串)
	 */
	@Column("seat_id")
	private String seatId;
	
	/**
	 * 推广位名称
	 */
	@Column("seat_name")
	private String seatName;

	/**
	 * 平台
	 */
	@Column("platform")
	private String platform;

	/**
	 * 推广位分配给用户的时间
	 */
	@Column("allot_time")
	private Date allotTime;

	/**
	 * 推广位被使用的用户ID
	 */
	@Column("user_id")
	private long userId;

	public String getSeatId() {
		return seatId;
	}

	public void setSeatId(String seatId) {
		this.seatId = seatId;
	}

	public String getSeatName() {
		return seatName;
	}

	public void setSeatName(String seatName) {
		this.seatName = seatName;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public Date getAllotTime() {
		return allotTime;
	}

	public void setAllotTime(Date allotTime) {
		this.allotTime = allotTime;
	}
}
