package com.cn.leedane.model;

/**
 * 签到实体类
 * @author LeeDane
 * 2016年7月12日 上午10:54:36
 * Version 1.0
 */
//@Table(name="T_SIGN_IN")
public class SignInBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 当前积分
	 */
	//private int score; 
	
	/**
	 * 上次记录的id
	 */
	private int pid;  
	
	/**
	 * 连续签到的天数
	 */
	private int continuous;
	
	/**
	 * 签到方式
	 */
	private String froms;
	
	/**
	 * 创建日期(用于和create_user_id做联合约束记录的唯一性)
	 */
	private String createDate;
	
	private String str1;
	private String str2;
	
	/*public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}*/
	
	//@Column(name="pid",nullable=true)
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	
	public int getContinuous() {
		return continuous;
	}
	public void setContinuous(int continuous) {
		this.continuous = continuous;
	}
	public String getStr1() {
		return str1;
	}
	public void setStr1(String str1) {
		this.str1 = str1;
	}
	public String getStr2() {
		return str2;
	}
	public void setStr2(String str2) {
		this.str2 = str2;
	}
	
	//@Column(name="create_date", length=10)
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
		
}
