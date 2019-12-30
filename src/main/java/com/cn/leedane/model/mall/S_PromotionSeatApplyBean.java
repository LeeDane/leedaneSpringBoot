package com.cn.leedane.model.mall;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.mybatis.table.annotation.Table;

import java.util.Date;

/**
 * 推广位申请记录实体bean
 * @author LeeDane
 * 2017年12月7日 下午10:50:32
 * version 1.0
 */
@Table(value="t_mall_promotion_seat_apply")
public class S_PromotionSeatApplyBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 申请平台
	 */
	@Column("platform")
	private String platform;

	/**
	 * 最终分配的推广位的ID,这事冗余字段，为了记录最终申请的结果
	 */
	@Column("seat_id")
	private long seatId;

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public long getSeatId() {
		return seatId;
	}

	public void setSeatId(long seatId) {
		this.seatId = seatId;
	}
}
