package com.cn.leedane.model;
import java.util.Date;
import java.util.UUID;

import com.cn.leedane.utils.StringUtil;
import org.apache.solr.client.solrj.beans.Field;

import com.cn.leedane.mybatis.table.annotation.Column;

/**
 * 用户实体类
 * @author LeeDane
 * 2015年4月3日 下午3:31:34
 * Version 1.0
 */
public class UserBean extends StatusBean{
	
	private static final long serialVersionUID = 1L;
	//用户状态    0:被禁止使用 1：正常，8、注册未激活,6：未完善信息 ， 4：被禁言 ，2:注销
	
	/**
	 * 非必须，uuid,唯一(如在图像的时候关联filepath表)
	 */
	private String uuid;
	
	/**
	 * 用户类型，0：用户,1:商家,2:管理员，3：商家客服
	 */
	private int type;
	
	/**
	 * 帐号,默认登录的标准以及显示的用户名称(不能为空)
	 */
	@Field
	private String account; 
	
	/**
	 * 中文名
	 */
	@Column("china_name")
	@Field
	private String chinaName;
	
	/**
	 * 密码,需要MD5加密后保存
	 */
	private String password; 
	
	/**
	 * 真实姓名
	 */
	@Column("real_name")
	@Field
	private String realName; 
	
	/**
	 * 性别
	 */
	@Field
	private String sex;
	
	/**
	 * 年龄
	 */
	@Field
	private int age;
	
	/**
	 * 民族
	 */
	@Field
	private String nation;
	
	/**
	 * 婚姻状态
	 */
	private String marry;  
	
	/**
	 * 籍贯
	 */
	@Column("native_place")
	@Field
	private String nativePlace;
	
	/**
	 * 最高学历
	 */
	@Column("education_background")
	@Field
	private String educationBackground; 
	
	/**
	 * 毕业学校
	 */
	@Field
	private String school; 
	
	/**
	 * 公司
	 */
	@Field
	private String company;
	
	/**
	 * 公司地址
	 */
	@Column("company_address")
	@Field
	private String companyAddress;  
	
	/**
	 * 联系地址(默认地址)
	 */
	@Field
	private String address;   
	
	/**
	 * 手机号码，格式：137XXXXXXXXXXX
	 */
	@Column("mobile_phone")
	@Field
	private String mobilePhone;  
	
	/**
	 * 家庭座机电话，格式如020-1234567
	 */
	@Column("home_phone")
	private String homePhone; 
	
	/**
	 * 公司电话号码
	 */
	@Column("company_phone")
	private String companyPhone; 
	
	/**
	 * 生日，格式2014-06-25 00:00:00
	 */
	@Column("birth_day")
	private Date birthDay;  
	
	/**
	 * 保存照片的相对路径(开发时选择绝对路径)
	 */
	@Column("pic_path")
	@Field
	private String picPath;
	
	/**
	 * 照片的base64位(开发时选择绝对路径)
	 */
	@Column("pic_base64")
	private String picBase64;
	
	/**
	 * 邮箱，附加登录的条件，找回密码，验证等凭证
	 */
	@Field
	private String email;
	
	/**
	 * QQ号码
	 */
	@Field
	private String qq;
	
	/**
	 * 身份证号码
	 */
	@Column("id_card")
	private String idCard;
	
	/**
	 * 个人介绍
	 */
	@Column("personal_introduction")
	@Field
	private String personalIntroduction; 
	
	/**
	 * 保存注册的注册码
	 */
	@Column("register_code")
	private String registerCode;
	
	/**
	 * 保存私有码(注册的时候系统随机生成，uuid生成36位的码，作用于系统token校验等的匹配)
	 */
	@Column("secret_code")
	private String secretCode;
	
	/**
	 * 积分
	 */
	private int score;
	
	/**
	 * 注册时间，格式2014-06-25 10:00:00
	 */
	@Column("register_time")
	@Field
	private Date registerTime;
	
	/**
	 * 免登陆验证码
	 */
	@Column("no_login_code")
	private String noLoginCode = "";
	
	/**
	 * 是否被solr索引(冗余字段)
	 */
	@Column("is_solr_index")
	private boolean isSolrIndex;
	
	/**
	 * 是否是管理员(冗余字段)
	 */
	//@Column("is_admin")
	//private boolean isAdmin;
	
	/**
	 * 所绑定的微信用户的名称
	 */
	@Column("wechat_user_name")
	private String wechatUserName;

	/**
	 * 是否被添加到es索引中
	 */
	@Field
	@Column("es_index")
	private boolean esIndex;
	
	/**
	 * 用户拥有的角色(一对多的关系)
	 */
	//private Set<RolesBean> roles = new LinkedHashSet<RolesBean>();
	
	/**
	 * 扩展字段1
	 */
	private String str1; 
	
	/**
	 * 扩展字段2
	 */
	private String str2;
	
	public UserBean(){
		
	}
	
	public UserBean(int id, int age, String account){
		this.id = id;
		this.account = account;
		this.age = age;
		this.registerTime = new Date();
		this.score = 0;
	}
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getQq() {
		return qq;
	}
	public void setQq(String qq) {
		this.qq = qq;
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
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public String getChinaName() {
		return chinaName;
	}
	public void setChinaName(String chinaName) {
		this.chinaName = chinaName;
	}
	
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	
	public String getHomePhone() {
		return homePhone;
	}
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}
	
	public Date getBirthDay() {
		return birthDay;
	}
	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}
	
	public String getPicPath() {
		return picPath;
	}
	public void setPicPath(String picPath) {
		this.picPath = picPath;
	}
	
	public String getIdCard() {
		return idCard;
	}
	public void setIdCard(String idCard) {
		this.idCard = idCard;
	}
	
	public String getRegisterCode() {
		return registerCode;
	}
	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}
	
	public Date getRegisterTime() {
		return registerTime;
	}
	public void setRegisterTime(Date registerTime) {
		this.registerTime = registerTime;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getMarry() {
		return marry;
	}
	public void setMarry(String marry) {
		this.marry = marry;
	}
	
	public String getNativePlace() {
		return nativePlace;
	}
	public void setNativePlace(String nativePlace) {
		this.nativePlace = nativePlace;
	}
	
	public String getEducationBackground() {
		return educationBackground;
	}
	public void setEducationBackground(String educationBackground) {
		this.educationBackground = educationBackground;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getCompanyAddress() {
		return companyAddress;
	}
	public void setCompanyAddress(String companyAddress) {
		this.companyAddress = companyAddress;
	}
	
	public String getCompanyPhone() {
		return companyPhone;
	}
	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}
	
	public String getPersonalIntroduction() {
		return personalIntroduction;
	}
	public void setPersonalIntroduction(String personalIntroduction) {
		this.personalIntroduction = personalIntroduction;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	
	public String getPicBase64() {
		return picBase64;
	}
	public void setPicBase64(String picBase64) {
		this.picBase64 = picBase64;
	}
	
	public String getNoLoginCode() {
		return noLoginCode;
	}
	public void setNoLoginCode(String noLoginCode) {
		this.noLoginCode = noLoginCode;
	}
	
	public boolean isSolrIndex() {
		return isSolrIndex;
	}
	public void setSolrIndex(boolean isSolrIndex) {
		this.isSolrIndex = isSolrIndex;
	}
	
	/*public boolean isAdmin() {
		return isAdmin;
	}
	
	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}*/
	
	public String getWechatUserName() {
		return wechatUserName;
	}
	public void setWechatUserName(String wechatUserName) {
		this.wechatUserName = wechatUserName;
	}

	public String getSecretCode() {
		if(StringUtil.isNull(secretCode))
			secretCode = "adduser" + UUID.randomUUID().toString();
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}
	
	/**
	 * 对用户的修CRUD操作不会影响到roles，所以选择CascadeType.REFRESH
	 * @return
	 * 2015年7月22日 下午5:46:51
	 */
	/*@OneToMany(cascade=CascadeType.ALL)
	@JoinTable(
            name="T_USER_ROLE",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id")
    )

	public Set<RolesBean> getRoles() {
		return roles;
	}
	public void setRoles(Set<RolesBean> roles) {
		this.roles = roles;
	}	*/

	public boolean isEsIndex() {
		return esIndex;
	}

	public void setEsIndex(boolean esIndex) {
		this.esIndex = esIndex;
	}
}