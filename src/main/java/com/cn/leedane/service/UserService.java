package com.cn.leedane.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;

/**
 * 用户service接口类
 * @author LeeDane
 * 2016年7月12日 上午11:28:22
 * Version 1.0
 */
@Transactional
public interface UserService<T extends IDBean>{
	/**
	 * 基础的保存实体的方法
	 * @param t
	 * @return
	 */
	public boolean save(UserBean t);
	
	/**
	 * 基础更新实体的方法
	 * @param t
	 * @return
	 */
	public boolean update(UserBean t);
	
	/**
	 * 基础删除实体的方法
	 * @param t
	 * @return
	 */
	public boolean delete(UserBean t);
	
	/**
	 * 执行SQL对应字段的List<Map<String,Object>
	 * @param sql sql语句,参数直接写在语句中，存在SQL注入攻击de风险，慎用
	 * @param params ?对应的值
	 * @return
	 */
	public List<Map<String, Object>> executeSQL(String sql, Object ...params);
	
	/**
	 * 基础根据id找到一个实体对象
	 * 不存在延迟加载问题，不采用lazy机制，在内部缓存中进行数据查找，
	 * 如果没有发现数据則将越过二级缓存，直接调用SQL查询数据库。
	 * 如果没有数据就返回null，返回的是真正的实体类
	 * @param id
	 * @return
	 */
	public UserBean findById(int id);
	
	/**
	 * 通过Id找到对应的User
	 */
	//public UserBean loadById(int Uid);
	
	/**
	 * 账号密码登录
	 * @param condition  用户的账号/邮箱
	 * @param password  用户的密码(密码将再次进行MD5加密)
	 * @return
	 */
	public UserBean loginUser(String condition , String password);
	
	/**
	 * 账号密码登录
	 * @param condition  用户的账号/邮箱
	 * @param password  用户的密码(密码不再进行MD5加密)
	 * @return
	 */
	public UserBean loginUserNoComputePSW(String condition , String password);
	
	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	public Map<String,Object> saveUser(UserBean user);
	
	/**
	 * 检查验证码是否正确
	 * @param registerCode 验证码
	 * @return
	 */
	public boolean updateCheckRegisterCode(String registerCode);
	
	/**
	 * 更新用户的type
	 * @param type 类型整数
	 * @return
	 */
	public boolean updateUserState(UserBean user);
	
	/**
	 * 更新表中字段的值(注意值需要提前进行类型转化)
	 * @param name   更新的字段
	 * @param value   更新字段所对应的值
	 * @param table  表名(实体的类名称)
	 * @param id 数据在表table中的ID
	 * @return
	 */
	//public boolean updateField(String name, Object value,String table,int id);
	
	/**
	 * 给用户发送邮件
	 * @param user
	 */
	public void sendEmail(UserBean user);
	
	/**
	 * 找回密码
	 * @param account 用户的账号
	 * @param type  类型(0:邮箱,1:手机)
	 * @param value  对应type，分别是邮箱或者手机号码
	 * @param findPswCode  找密码凭证
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void findPassword(String account,String type,String value,String findPswCode);
	
	/**
	 * 查找相关的多个用户
	 * @param conditions  条件，where语句后面的 ，不包括where
	 * @param params  多个参数，请注意顺序
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String,Object>> find4MoreUser(String conditions, Object ...params);
	
	/**
	 * 获得指定表中数据的总数
	 * @param tableName  表名，注意：不是实体名称，是数据库中的表名称
	 * @param field  字段，为空将默认以ID字段
	 * @param where where控制语句，需要些where，有参数就直接写参数，不用以问号代替
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int total(String tableName, String field, String where);
	
	/**
	 * 统计所有用户的年龄
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> statisticsUserAge();
	
	/**
	 * 统计所有用户的年龄段
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> statisticsUserAgeRang();

	/**
	 * 统计所有用户的注册时间的年份
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> statisticsUserRegisterByYear();
	
	/**
	 * 统计所有用户的注册时间的月份
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> statisticsUserRegisterByMonth();
	
	/**
	 * 统计所有用户的最近一周的注册人数
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> statisticsUserRegisterByNearWeek();
		
	/**
	 * 统计所有用户的最近一个月的注册人数
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> statisticsUserRegisterByNearMonth();
	
	/**
	 * 通过免登陆码获取用户对象
	 * @param account
	 * @param noLoginCode
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UserBean getUserByNoLoginCode(String account, String noLoginCode);

	/**
	 * 根据用户ID获取其头像的信息
	 * {"uid":2, "pic_size":"30x30"} "order":0默认是0, tablename:"t_user"
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public String getHeadBase64StrById(JSONObject jo, UserBean user,
			HttpServletRequest request);
	
	/**
	 * 根据用户ID获取其头像的路径
	 * {"uid":2, "pic_size":"30x30"} "order":0默认是0, tablename:"t_user"
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public String getHeadFilePathStrById(JSONObject jo, UserBean user,
			HttpServletRequest request);
	/**
	 * 根据用户的ID上传头像
	 * {"base64": "jjjhuhfuewudfbnfbbdfs"}
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public boolean uploadHeadBase64StrById(JSONObject jo, UserBean user,
			HttpServletRequest request);

	/**
	 * 检查账号是否被占用
	 * '{'account':'leedane'}'
	 * @param jo
	 * @param request
	 * @param user
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String,Object> checkAccount(JSONObject jo, HttpServletRequest request,
			UserBean user);
	
	/**
	 * 检查手机是否被占用
	 * '{'mobilePhone':'13763059195'}'
	 * @param jo
	 * @param request
	 * @param user
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean checkMobilePhone(JSONObject jo, HttpServletRequest request,
			UserBean user);
	
	/**
	 * 检查邮箱是否被占用
	 * '{'email':'13763059195@qq.com'}'
	 * @param jo
	 * @param request
	 * @param user
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean checkEmail(JSONObject jo, HttpServletRequest request,
			UserBean user);

	/**
	 * 手机注册(直接注册成功，免邮箱验证)
	 * 注意：传递过来的密码需要先经过md5加密
	 * {'validationCode':253432,'mobilePhone':172636634664,'account':'leedane','email':'825711424@qq.com','password':'123'}
	 * @param jo
	 * @param request
	 * @return
	 */
	public UserBean registerByPhone(JSONObject jo, HttpServletRequest request);

	/**
	 * 通过手机号码登录
	 * {'validationCode':253432,'mobilePhone':172636634664}
	 * @param jo
	 * @param request
	 * @return
	 */
	public UserBean loginByPhone(JSONObject jo, HttpServletRequest request);
	/**
	 * 获取手机注册的验证码
	 * {''mobilePhone':172636634664'}
	 * @param jo
	 * @param request
	 * @return 
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getPhoneRegisterCode(JSONObject jo,
			HttpServletRequest request);
	
	/**
	 * 获取手机登录的验证码
	 * {''mobilePhone':172636634664'}
	 * @param mobilePhone
	 * @param request
	 * @return 
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getPhoneLoginCode(String mobilePhone, HttpServletRequest request);
	
	/**
	 * 绑定微信号
	 * @param FromUserName
	 * @return
	 */
	public UserBean bindByWeChat(String FromUserName, String account, String password);
	
	/**
	 * 通过微信号登录
	 * @param FromUserName
	 * @return
	 */
	public UserBean loginByWeChat(String FromUserName);

	/**
	 * 微信账号解绑
	 * @param FromUserName
	 * @return
	 */
	public boolean wechatUnBind(String FromUserName);
	
	/**
	 * 获得系统全部的用户ID
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<Map<String, Object>> getAllUserId();
	
	/**
	 * 通过用户名称寻找用户ID
	 * @param username
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public int getUserIdByName(String username);

	/**
	 * 获取用户的基本数据(评论数，转发数，积分)
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> getUserInfoData(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 通过手机号码注册(不需要校验)
	 * @param jo
	 * @param request
	 * @return
	 */
	public Map<String, Object> registerByPhoneNoValidate(JSONObject jo, HttpServletRequest request);

	/**
	 * 搜索用户
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> search(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * web端搜索用户(跟app搜索区别，所以另外写一个)
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> webSearch(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 摇一摇搜索用户
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> shakeSearch(JSONObject jo, UserBean user, HttpServletRequest request);

	
	/**
	 * 更新用户的基本信息
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> updateUserBase(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 管理员更新用户的基本信息
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> adminUpdateUserBase(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 更新登录密码
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> updatePassword(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 管理员重置用户登录密码
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> adminResetPassword(JSONObject jo, UserBean user, HttpServletRequest request);

	/**
	 * 通过微信用户名找到leedane系统绑定的用户对象
	 * @param fromUserName
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public UserBean findUserBeanByWeixinName(String fromUserName);

	/**
	 * 根据用户名称去查找用户的信息
	 * @param jo
	 * @param user
	 * @param request
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public Map<String, Object> searchUserByUserIdOrAccount(JSONObject jo, UserBean user, HttpServletRequest request);
	
	/**
	 * 获取所有用户
	 * @param status
	 * @return
	 */
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public List<UserBean> getAllUsers(int status);

	/**
	 * 扫码登陆验证
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> scanLogin(JSONObject json, UserBean user, HttpServletRequest request);
	
	/**
	 * 取消扫码登陆
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> cancelScanLogin(JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 删除（注销）用户
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> deleteUser(JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 发送信息//type: 1为通知，2为邮件，3为私信，4为短信
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> sendMessage(JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 添加用户(只能是管理员操作)
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> addUser(JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 上传用户头像链接
	 * @param json
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> uploadUserHeadImageLink(JSONObject json, UserBean user, HttpServletRequest request);

	/**
	 * 初始化设置
	 * @param user
	 * @param request
	 * @return
	 */
	public Map<String, Object> initSetting(UserBean user, HttpServletRequest request);

	/**
	 * 获取在线的用户列表
	 * @param jsonFromMessage
	 * @param userFromMessage
	 * @param request
	 * @return
	 */
	public Map<String, Object> actives(
			JSONObject json, UserBean user,
			HttpServletRequest request);
}
