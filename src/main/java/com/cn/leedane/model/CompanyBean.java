package com.cn.leedane.model;

import java.util.Date;
import java.util.Set;
/**
 * 品牌/公司实体类
 * @author LeeDane
 * 2016年7月12日 上午10:42:26
 * Version 1.0
 */
//@Table(name="T_COMPANY")
public class CompanyBean extends RecordTimeBean{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 品牌名称
	 */
	private String name;
	
	/**
	 * 品牌创建时间
	 */
	private Date companyCreateTime;
	
	/**
	 * 品牌创始人
	 */
	private String boss;
	
	/**
	 * 品牌价值(市值)
	 */
	private String price;
	
	/**
	 * 员工数量
	 */
	private int employee;
	
	/**
	 * 所属的类型，如国企，民企，独资，合资等等
	 */
	private String type;
	
	/**
	 * 所属的行业
	 */
	private String industry; 
	
	/**
	 * 信用度，1：很差，2：一般，3：及格，4：良好，5：优秀
	 */
	private int credit;
	
	/**
	 * 总公司的地址
	 */
	private String address;
	
	/**
	 * 公司联系电话 (多个用逗号隔开)
	 */
	private String mobilePhone;
	
	/**
	 * 公司总机的电话(多个用逗号隔开)
	 */
	private int phone;
	
	/**
	 * 公司客服，一对多的关系
	 */
	private Set<UserBean> customeService;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	//@Column(name="company_create_time")
	public Date getCompanyCreateTime() {
		return companyCreateTime;
	}

	public void setCompanyCreateTime(Date companyCreateTime) {
		this.companyCreateTime = companyCreateTime;
	}

	public String getBoss() {
		return boss;
	}

	public void setBoss(String boss) {
		this.boss = boss;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public int getEmployee() {
		return employee;
	}

	public void setEmployee(int employee) {
		this.employee = employee;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	//@Column(name="mobile_phone")
	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public int getPhone() {
		return phone;
	}

	public void setPhone(int phone) {
		this.phone = phone;
	}

	//@OneToMany(targetEntity=UserBean.class,fetch = FetchType.EAGER,cascade=CascadeType.ALL)
	//@Column(name="custome_service")	
	public Set<UserBean> getCustomeService() {
		return customeService;
	}

	public void setCustomeService(Set<UserBean> customeService) {
		this.customeService = customeService;
	}
}
