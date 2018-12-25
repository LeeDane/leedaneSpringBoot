package com.cn.leedane.display.clock;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 任务统计信息展示类bean
 * @author LeeDane
 * 2018年12月3日 下午3:35:09
 * version 1.0
 */
public class ClockStatisticsDisplay implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int clockId;
	
	private int createUserId;  //创建人
	
	private String createAccount; //创建者名称
	
	private String createTime; //创建时间
	
	private String clockDesc;//任务的描述，如任务已经开始，距离任务结束还有10天
	
	private int member;//成员数
	
	private List<Map<String, Object>> members; // 排行榜中成员列表
	
	private Map<String, Integer> sexs;//性别的统计
	
	private Map<String, Integer> ages; // 年龄烦人统计

	private List<Map<String, Object>> results; // 最近30天的打卡时间统计
//
	public int getClockId() {
		return clockId;
	}

	public void setClockId(int clockId) {
		this.clockId = clockId;
	}

	public int getCreateUserId() {
		return createUserId;
	}

	public void setCreateUserId(int createUserId) {
		this.createUserId = createUserId;
	}

	public String getCreateAccount() {
		return createAccount;
	}

	public void setCreateAccount(String createAccount) {
		this.createAccount = createAccount;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getClockDesc() {
		return clockDesc;
	}

	public void setClockDesc(String clockDesc) {
		this.clockDesc = clockDesc;
	}

	public int getMember() {
		return member;
	}

	public void setMember(int member) {
		this.member = member;
	}

	public Map<String, Integer> getAges() {
		return ages;
	}

	public void setAges(Map<String, Integer> ages) {
		this.ages = ages;
	}

	public Map<String, Integer> getSexs() {
		return sexs;
	}

	public void setSexs(Map<String, Integer> sexs) {
		this.sexs = sexs;
	}

//	public List<Map<String, Object>> getClockIns() {
//		return clockIns;
//	}
//
//	public void setClockIns(List<Map<String, Object>> clockIns) {
//		this.clockIns = clockIns;
//	}

	public List<Map<String, Object>> getMembers() {
		return members;
	}

	public void setMembers(List<Map<String, Object>> members) {
		this.members = members;
	}

	public List<Map<String, Object>> getResults() {
		return results;
	}

	public void setResults(List<Map<String, Object>> results) {
		this.results = results;
	}
}
