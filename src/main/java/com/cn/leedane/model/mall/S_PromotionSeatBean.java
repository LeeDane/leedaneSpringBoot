package com.cn.leedane.model.mall;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

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
	 * 推广位ID
	 */
	@Column("seat_id")
	private long seatId;
	
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
	 * 推广位被使用的用户ID
	 */
	@Column("user_id")
	private long userId;

	public long getSeatId() {
		return seatId;
	}

	public void setSeatId(long seatId) {
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
}
