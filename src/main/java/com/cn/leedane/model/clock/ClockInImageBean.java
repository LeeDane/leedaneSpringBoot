package com.cn.leedane.model.clock;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.utils.EnumUtil.ClockInType;

import java.util.Date;
import java.util.List;

/**
 * 任务关联的图片实体bean
 * @author LeeDane
 * 2018年8月29日 上午10:40:03
 * version 1.0
 */
public class ClockInImageBean extends RecordTimeBean{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	//任务图片的状态：0：禁用， 1：正常，2：删除，9：结束

	/**
	 * 打卡的任务ID
	 */
	@Column(value="clock_in_id", required = true)
	private int clockInId;

	/**
	 * 打卡的图片的地址
	 */
	@Column(value="img", required = true)
	private String img;

	/**
	 * 是否是主图(如果任务是图片打卡任务，main标记的图片是无法删除的，一个任务只能有一张图片)
	 */
	@Column(value="main", required = true)
	private boolean main;

	public int getClockInId() {
		return clockInId;
	}

	public void setClockInId(int clockInId) {
		this.clockInId = clockInId;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public boolean isMain() {
		return main;
	}

	public void setMain(boolean main) {
		this.main = main;
	}
}
