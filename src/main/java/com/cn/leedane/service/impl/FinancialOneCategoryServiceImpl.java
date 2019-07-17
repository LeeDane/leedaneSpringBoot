package com.cn.leedane.service.impl;

import com.cn.leedane.mapper.FinancialOneCategoryMapper;
import com.cn.leedane.model.FinancialOneLevelCategoryBean;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FinancialOneCategoryService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.ResponseMap;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 记账一级分类service实现类
 * @author LeeDane
 * 2016年12月8日 下午9:31:23
 * Version 1.0
 */

@Service("financialOneCategoryService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class FinancialOneCategoryServiceImpl implements FinancialOneCategoryService<FinancialOneLevelCategoryBean> {
	
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private FinancialOneCategoryMapper financialOneCategoryMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public List<FinancialOneLevelCategoryBean> getAllDefault(int userId) {
		logger.info("FinancialOneCategoryServiceImpl-->getAll():userId=" +userId);
		return financialOneCategoryMapper.getBeans("select * from t_financial_one_category where create_user_id = ?", userId);
	}

	@Override
	public boolean batchInsert(final List<FinancialOneLevelCategoryBean> beans) {
		logger.info("FinancialOneCategoryServiceImpl-->batchInsert():大小=" +beans.size());
		if(CollectionUtil.isNotEmpty(beans)){
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("INSERT INTO t_financial_one_category(status, category_value, icon_name, ");
			sqlBuffer.append(" model, budget, category_order, is_default, is_system, create_user_id, create_time)");
			sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?)");
			return jdbcTemplate.batchUpdate(sqlBuffer.toString(), new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					FinancialOneLevelCategoryBean bean = beans.get(i);
					ps.setInt(1, bean.getStatus());
					ps.setString(2, bean.getCategoryValue());
					ps.setString(3, bean.getIconName());
					ps.setInt(4, bean.getModel());
					ps.setFloat(5, bean.getBudget());
					ps.setInt(6, bean.getCategoryOrder());
					ps.setBoolean(7, bean.isDefault());
					ps.setBoolean(8, bean.isSystem());
					ps.setInt(9, bean.getCreateUserId());
					ps.setTimestamp(10, DateUtil.getTimestamp(bean.getCreateTime()));
				}
				
				@Override
				public int getBatchSize() {
					return beans.size();
				}
			}).length == beans.size();
		}
		return false;
	}

	@Override
	public Map<String, Object> getAll(JSONObject jo,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("FinancialOneCategoryServiceImpl-->getAll():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		boolean result = false;
		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("SELECT id, category_value, icon_name,");
		sqlBuffer.append(" model, budget, category_order, is_default, is_system, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time ");
		sqlBuffer.append(" FROM t_financial_one_category");
		sqlBuffer.append(" where status = ? and create_user_id = ? ");
		
		List<Map<String, Object>> r = financialOneCategoryMapper.executeSQL(sqlBuffer.toString(), ConstantsUtil.STATUS_NORMAL, user.getId());
		result = r.size() > 0;
		if(!result){
			sqlBuffer = new StringBuffer();
			sqlBuffer.append("SELECT id, category_value, icon_name,");
			sqlBuffer.append(" model, budget, category_order, is_default, is_system, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sqlBuffer.append(" FROM t_financial_one_category");
			sqlBuffer.append(" where status = ? and is_system = ? ");
			
			r = financialOneCategoryMapper.executeSQL(sqlBuffer.toString(), ConstantsUtil.STATUS_NORMAL, true);
		}
		message.put("message", r);
		message.put("isSuccess", true);
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取全部一级分类列表", StringUtil.getSuccessOrNoStr(result)).toString(), "getAll()", StringUtil.changeBooleanToInt(result), 0);
		return message.getMap();
	}
}