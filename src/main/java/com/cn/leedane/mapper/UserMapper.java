package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.UserBean;

/**
 * 用户mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:20:48
 * Version 1.0
 */
public interface UserMapper  extends BaseMapper<UserBean>{
	
	/**
	 * 执行HQL获得实体
	 * @param beanName  实体名称如：xxxBean
	 * @param where where语句，参数需直接填写在字符串中
	 * @return
	 */
	//public List<UserBean> executeHQL(String beanName, String where);
	
	/**
	 * 用户登录
	 * @param condition  用户的账号/邮箱
	 * @param password  用户的密码
	 * @return
	 */
	public UserBean loginUser(@Param("condition") String condition , @Param("password") String password);
	
	/**
	 * 通过手机号码登录账号
	 * @param mobilePhone
	 * @return
	 */
	public UserBean loginUserByPhone(@Param("mobilePhone")String mobilePhone);
	
	/**
	 * 用户注册
	 * @param user
	 * @return
	 */
	//public boolean saveUser(UserBean user);
	
	/**
	 * 判断用户账号是否已经存在,false：表示存在，true:表示不存在
	 * @return
	 */
	//public boolean isUniqueUser(UserBean user);
	
	/**
	 * 查找相关的一个用户
	 * @param id 有id根据id查找
	 * @param condition 没id根据条件查找
	 * @return
	 */
	public UserBean find4OneUser(int id,String condition);
	
	/**
	 * 检查验证码是否正确
	 * @param registerCode 验证码
	 * @return
	 */
	public UserBean checkRegisterCode(@Param("registerCode")String registerCode);
	
	/**
	 * 更新用户的type
	 * @param type 类型整数
	 * @return
	 */
	//public boolean updateUserState(UserBean user);
	
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
	 * 查找相关的多个用户
	 * @param conditions  条件，where语句后面的 ，不包括where
	 * @param params  多个参数，请注意顺序
	 * @return
	 */
	public List<Map<String,Object>> find4MoreUser(String conditions,Object ...params);
	
	/**
	 * 获得表中数据的总数
	 * @param tableName  表名，注意：不是实体名称，是数据库中的表名称
	 * @param field  字段，为空将默认以ID字段
	 * @param where where控制语句，需要些where，有参数就直接写参数，不用以问号代替
	 * @return
	 */
	public int total(String tableName, String field, String where);
	
	/**
	 * 统计所有用户的年龄
	 * @return
	 */
	public List<Map<String, Object>> statisticsUserAge();
	
	/**
	 * 统计所有用户的年龄段
	 * @return
	 */
	public List<Map<String, Object>> statisticsUserAgeRang();
	
	/**
	 * 统计所有用户的注册时间的年份
	 * @return
	 */
	public List<Map<String, Object>> statisticsUserRegisterByYear();
	
	/**
	 * 统计所有用户的注册时间的月份
	 * @return
	 */
	public List<Map<String, Object>> statisticsUserRegisterByMonth();
	
	/**
	 * 统计所有用户的最近一周的注册人数
	 * @return
	 */
	public List<Map<String, Object>> statisticsUserRegisterByNearWeek();
	
	/**
	 * 统计所有用户的最近一个月的注册人数
	 * @return
	 */
	public List<Map<String, Object>> statisticsUserRegisterByNearMonth();
	
	
	/**
	 * 获取所有用户
	 * @param status
	 * @return
	 */
	public List<UserBean> getAllUsers(@Param("status") int status);
	
	/**
	 * 通过免登陆码获取用户对象
	 * @param account
	 * @param noLoginCode
	 * @return
	 */
	public UserBean getUserByNoLoginCode(@Param("account")String account, @Param("noLoginCode")String noLoginCode, @Param("status")int status);
	
	/**
	 * 通过微信号登录
	 * @param FromUserName
	 * @return
	 */
	public UserBean loginByWeChat(@Param("fromUserName")String FromUserName, @Param("status")int status);
	
	/**
	 * 摇一摇随机获取用户
	 * @return
	 */
	public UserBean shakeSearch(@Param("createUserId")int createUserId, @Param("status")int status);
}
