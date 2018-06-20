package com.cn.leedane.model.baby;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 宝宝生活实体bean(包括吃喝住行等)
 * @author LeeDane
 * 2018年6月11日 下午5:22:39
 * version 1.0
 */
public class BabyLifeBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//宝宝生活的状态：0：禁用， 1：正常
	
	/**
	 * 类型(枚举， 1：吃喝、2：睡觉、3：洗刷 4：生病)
	 */
	@Column("life_type")
	@Field
	private int lifeType;
	
	
	/**********   公共部分       ************/
	
	/**
	 * 宝宝ID
	 */
	@Column("baby_id")
	@Field
	private int babyId;
	
	/**
	 * 发生日期
	 */
	@Column("occur_date")
	@Field
	private Date occurDate;
	
	/**
	 * 发生时间（包括年月日）
	 */
	@Column("occur_time")
	@Field
	private Date occurTime;
	
	/**
	 * 宝宝反应情况, 1:积极配合, 2:一般配合, 3:不怎么配合, 4:不配合
	 */
	@Field
	private int reaction;
	
	/**
	 * 宝宝的描述信息
	 */
	@Column("baby_desc")
	@Field
	private String babyDesc;
	
	/**
	 * 发生的位置
	 */
	@Column("occur_place")
	@Field
	private String occurPlace;
	
	
	/**********   吃喝独立部分       ************/
	/**
	 * 喂养类型(1: 亲喂养, 2:瓶喂养，3:碗喂养)
	 */
	@Column("eat_type")
	@Field
	private int eatType;
	
	/**
	 * 亲喂养左侧容量
	 */
	@Column("left_capacity")
	@Field
	private float leftCapacity;
	
	/**
	 * 亲喂养右侧容量
	 */
	@Column("right_capacity")
	@Field
	private float rightCapacity;
	
	/**
	 * 总的容量
	 */
	@Field
	private float capacity;
	
	/**
	 * 总的温度
	 */
	@Field
	private float temperature;
	
	/**
	 * 品牌
	 */
	@Field
	private String brand;
	
	/**********   睡觉独立部分       ************/
	/**
	 * 起床时间
	 */
	@Column("wake_up_time")
	@Field
	private Date wakeUpTime;
	
	/**********   洗刷独立部分       ************/
	/**
	 * 洗刷结束时间
	 */
	@Column("wash_end_time")
	@Field
	private Date washEndTime;

	public int getLifeType() {
		return lifeType;
	}

	public void setLifeType(int lifeType) {
		this.lifeType = lifeType;
	}

	public Date getOccurDate() {
		return occurDate;
	}

	public void setOccurDate(Date occurDate) {
		this.occurDate = occurDate;
	}

	public Date getOccurTime() {
		return occurTime;
	}

	public void setOccurTime(Date occurTime) {
		this.occurTime = occurTime;
	}

	public int getReaction() {
		return reaction;
	}

	public void setReaction(int reaction) {
		this.reaction = reaction;
	}

	public String getBabyDesc() {
		return babyDesc;
	}

	public void setBabyDesc(String babyDesc) {
		this.babyDesc = babyDesc;
	}

	public String getOccurPlace() {
		return occurPlace;
	}

	public void setOccurPlace(String occurPlace) {
		this.occurPlace = occurPlace;
	}

	public int getEatType() {
		return eatType;
	}

	public void setEatType(int eatType) {
		this.eatType = eatType;
	}

	public float getLeftCapacity() {
		return leftCapacity;
	}

	public void setLeftCapacity(float leftCapacity) {
		this.leftCapacity = leftCapacity;
	}

	public float getRightCapacity() {
		return rightCapacity;
	}

	public void setRightCapacity(float rightCapacity) {
		this.rightCapacity = rightCapacity;
	}

	public float getCapacity() {
		return capacity;
	}

	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}

	public float getTemperature() {
		return temperature;
	}

	public void setTemperature(float temperature) {
		this.temperature = temperature;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public Date getWakeUpTime() {
		return wakeUpTime;
	}

	public void setWakeUpTime(Date wakeUpTime) {
		this.wakeUpTime = wakeUpTime;
	}

	public Date getWashEndTime() {
		return washEndTime;
	}

	public void setWashEndTime(Date washEndTime) {
		this.washEndTime = washEndTime;
	}

	public int getBabyId() {
		return babyId;
	}

	public void setBabyId(int babyId) {
		this.babyId = babyId;
	}
	
	
}
