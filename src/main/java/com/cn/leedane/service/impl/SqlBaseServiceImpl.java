package com.cn.leedane.service.impl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.service.SqlBaseService;

/**
 * App版本service实现类
 * @author LeeDane
 * 2016年7月12日 下午12:20:33
 * Version 1.0
 */
@Service("sqlBaseServicee")
public class SqlBaseServiceImpl implements SqlBaseService<IDBean> {
	Logger logger = Logger.getLogger(getClass());
	
	/*@Autowired
	private BaseMapper<IDBean> baseMapper;
	
	@Autowired
	private LinkManageMapper linkManageMapper;
	
	@Autowired
	private SignInMapper signInMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Override
	public List<Map<String, Object>> executeSQL(String sql, Object... params) {
		return baseMapper.executeSQL(sql, params);
	}
	
	@Override
	public int getTotal(String tableName, String where) {
		return SqlUtil.getTotalByList(baseMapper.getTotal(tableName, where));
	}

	@Override
	public int getTotalByUser(String tableName, int userId) {
		return SqlUtil.getTotalByList(baseMapper.getTotalByUser(tableName, userId));
	}
	
	@Override
	public List<Map<String, Object>> getFromToFriends(int uid) {
		logger.info("SqlBaseServiceImpl-->getFromToFriends():uid="+uid);
		String sql = " select to_user_id id, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status = "+ConstantsUtil.STATUS_NORMAL+") else to_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id =? and status = "+ConstantsUtil.STATUS_NORMAL+" "
				+" UNION " 
				+" select from_user_id id, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status = "+ConstantsUtil.STATUS_NORMAL+") else from_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id = ? and status = "+ConstantsUtil.STATUS_NORMAL;
		return baseMapper.executeSQL(sql, uid, uid);
	}
	
	@Override
	public List<Map<String, Object>> getToFromFriends(int uid) {
		logger.info("SqlBaseServiceImpl-->getToFromFriends():uid="+uid);
		String sql = " select from_user_id id, (case when to_user_remark = '' || to_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = to_user_id and u.status =?) else to_user_remark end ) remark from "+DataTableType.好友.value+" where to_user_id =? and status =?"
				+" UNION " 
				+" select to_user_id id, (case when from_user_remark = '' || from_user_remark = null then (select u.account from "+DataTableType.用户.value+" u where  u.id = from_user_id and u.status =?) else from_user_remark end ) remark from "+DataTableType.好友.value+" where from_user_id = ? and status=?";
		return baseMapper.executeSQL(sql, ConstantsUtil.STATUS_NORMAL, uid, ConstantsUtil.STATUS_NORMAL, ConstantsUtil.STATUS_NORMAL, uid, ConstantsUtil.STATUS_NORMAL);
	}
	
	@Override
	public UserBean findById(int uid) {
		return (UserBean) baseMapper.findById(UserBean.class, uid);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean saveClass(Class clazz) {
		return baseMapper.saveClass(clazz) > 0;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean updateSql(Class clazz, String sql, Object...params) {
		return baseMapper.updateSql(clazz, sql, params) > 0;
	}
	
	@Override
	public List<LinkManageBean> getAllLinks() {
		logger.info("SqlBaseServiceImpl-->getAllLinks()");
		return linkManageMapper.getAllLinks(ConstantsUtil.STATUS_NORMAL);
	}
	
	@Override
	public boolean hasHistorySign(int userId) {	
		return SqlUtil.getBooleanByList(this.signInMapper.hasHistorySign(userId));
	}

	@Override
	public UserBean loginUser(String condition, String password) {	
		logger.info("SqlBaseServiceImpl-->loginUser():condition="+condition+",password="+password);
		return userMapper.loginUser(condition, MD5Util.compute(password));
	}
	
	@Override
	public UserBean findUserBeanByWeixinName(String fromUserName) {
		logger.info("SqlBaseServiceImpl-->findUserBeanByWeixinName():fromUserName="+fromUserName);
		List<UserBean> users = null;
		
		if(StringUtil.isNull(fromUserName)){
			return null;
		}
		
		users = userMapper.getBeans("select * from t_user where wechat_user_name='"+fromUserName+"'");

		return users != null && users.size() > 0 ? users.get(0): null;
	}*/
}
