package com.cn.leedane.model.baby;

import java.util.Date;

import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.model.RecordTimeBean;
import com.cn.leedane.mybatis.table.annotation.Column;
import com.cn.leedane.utils.StringUtil;

/**
 * 宝宝实体bean
 * @author LeeDane
 * 2018年6月5日 下午3:35:38
 * version 1.0
 */
public class BabyBean extends RecordTimeBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//宝宝的状态：0：禁用， 1：正常
	
	/**
	 * 宝宝的类型(0:备孕中， 1：已出生)
	 */
	@Field
	private boolean born;
	
	/**
	 * 宝宝的昵称
	 */
	@Field
	private String nickname;
	
	/**
	 * 宝宝的姓名
	 */
	@Field
	private String name;
	
	/**
	 * 宝宝的公历生日
	 */
	@Column("gregorian_birthday")
	@Field
	private Date gregorianBirthDay;
	
	/**
	 * 宝宝的农历生日
	 */
	@Column("lunar_birthday")
	@Field
	private Date lunarBirthDay;
	
	/**
	 * 宝宝的头像图片地址
	 */
	@Column("head_pic")
	@Field
	private String headPic;
	
	/**
	 * 宝宝的性别
	 */
	@Field
	private String sex;
	
	/**
	 * 宝宝的个性签名
	 */
	@Column("personalized_signature")
	@Field
	private String personalizedSignature;
	
	/**
	 * 宝宝的健康状态
	 */
	@Column("healthy_state")
	@Field
	private int healthyState;
	
	/**
	 * 宝宝的排列序号
	 */
	@Field
	private int sorting;
	
	/**
	 * 宝宝的简介
	 */
	@Field
	private String introduction;
	
	/*********************** 备孕中的时间 *****************************/
	/**
	 * 宝宝的怀孕时间
	 */
	@Column("pregnancy_date")
	@Field
	private Date pregnancyDate;
	
	/**
	 * 宝宝的预产期
	 */
	@Column("pre_production")
	@Field
	private Date preProduction;

	public String getNickname() {
		return StringUtil.isNull(nickname) ? getName(): nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getGregorianBirthDay() {
		return gregorianBirthDay;
	}

	public void setGregorianBirthDay(Date gregorianBirthDay) {
		this.gregorianBirthDay = gregorianBirthDay;
	}

	public Date getLunarBirthDay() {
		return lunarBirthDay;
	}

	public void setLunarBirthDay(Date lunarBirthDay) {
		this.lunarBirthDay = lunarBirthDay;
	}

	public String getHeadPic() {
		return headPic;
	}

	public void setHeadPic(String headPic) {
		this.headPic = headPic;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getPersonalizedSignature() {
		return personalizedSignature;
	}

	public void setPersonalizedSignature(String personalizedSignature) {
		this.personalizedSignature = personalizedSignature;
	}

	public int getHealthyState() {
		return healthyState;
	}

	public void setHealthyState(int healthyState) {
		this.healthyState = healthyState;
	}

	public int getSorting() {
		return sorting;
	}

	public void setSorting(int sorting) {
		this.sorting = sorting;
	}

	public String getIntroduction() {
		return introduction;
	}

	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	public boolean isBorn() {
		return born;
	}

	public void setBorn(boolean born) {
		this.born = born;
	}

	public Date getPregnancyDate() {
		return pregnancyDate;
	}

	public void setPregnancyDate(Date pregnancyDate) {
		this.pregnancyDate = pregnancyDate;
	}

	public Date getPreProduction() {
		return preProduction;
	}

	public void setPreProduction(Date preProduction) {
		this.preProduction = preProduction;
	}
	
	
}
