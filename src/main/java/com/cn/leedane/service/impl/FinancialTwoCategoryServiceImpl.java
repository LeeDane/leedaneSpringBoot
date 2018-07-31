package com.cn.leedane.service.impl;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.mapper.FinancialTwoCategoryMapper;
import com.cn.leedane.model.FinancialTwoLevelCategoryBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FinancialTwoCategoryService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.FinancialCategoryUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

/**
 * 记账二级分类service实现类
 * @author LeeDane
 * 2016年12月8日 下午9:31:23
 * Version 1.0
 */

@Service("financialTwoCategoryService")
@Transactional  //此处不再进行创建SqlSession和提交事务，都已交由spring去管理了。
public class FinancialTwoCategoryServiceImpl implements FinancialTwoCategoryService<FinancialTwoLevelCategoryBean> {
	
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private FinancialTwoCategoryMapper financialTwoCategoryMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public List<FinancialTwoLevelCategoryBean> getAllDefault(int userId) {
		logger.info("FinancialTwoCategoryServiceImpl-->getAll():userId=" +userId);
		return financialTwoCategoryMapper.getBeans("select * from t_financial_two_category where create_user_id = ?", userId);
	}

	@Override
	public boolean batchInsert(final List<FinancialTwoLevelCategoryBean> beans) {
		logger.info("FinancialTwoCategoryServiceImpl-->batchInsert():大小=" +beans.size());
		if(CollectionUtil.isNotEmpty(beans)){
			StringBuffer sqlBuffer = new StringBuffer();
			sqlBuffer.append("INSERT INTO t_financial_two_category(status, one_level_id, category_value, icon_name, ");
			sqlBuffer.append(" budget, category_order, is_default, is_system, create_user_id, create_time)");
			sqlBuffer.append(" VALUES (?,?,?,?,?,?,?,?,?,?)");
			return jdbcTemplate.batchUpdate(sqlBuffer.toString(), new BatchPreparedStatementSetter() {
				
				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					FinancialTwoLevelCategoryBean bean = beans.get(i);
					ps.setInt(1, bean.getStatus());
					ps.setInt(2, bean.getOneLevelId());
					ps.setString(3, bean.getCategoryValue());
					ps.setString(4, bean.getIconName());
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
	public Map<String, Object> getOneAndTwoCategorys(JSONObject json,
			UserBean user, HttpServletRequest request) {
		logger.info("FinancialTwoCategoryServiceImpl-->getOneAndTwoCategorys():json=" +json.toString());
		
		ResponseMap message = new ResponseMap();
		
		List<FinancialTwoLevelCategoryBean> twoLevelCategoryBeans = FinancialCategoryUtil.getTwoLevelCategoryBeans();
		List<String> categorysList = new ArrayList<String>();
		if(CollectionUtil.isNotEmpty(twoLevelCategoryBeans)){
			for(FinancialTwoLevelCategoryBean twoLevelCategoryBean: twoLevelCategoryBeans){
				categorysList.add(FinancialCategoryUtil.getValueByOneLevelId(twoLevelCategoryBean.getOneLevelId()) + ">>>" + twoLevelCategoryBean.getCategoryValue());
			}
		}
		message.put("message", categorysList);
		message.put("isSuccess", true);
		return message.getMap();
	}

	@Override
	public Map<String, Object> getAll(JSONObject jo,
			UserBean user, HttpServletRequest request) {
		logger.info("FinancialTwoCategoryServiceImpl-->getAll():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		boolean result = false;
		
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("SELECT id, one_level_id, category_value, icon_name, ");
		sqlBuffer.append(" budget, category_order, is_default, is_system, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time ");
		sqlBuffer.append(" FROM t_financial_two_category");
		sqlBuffer.append(" where status = ? and create_user_id = ? order by id ");
		
		
		List<Map<String, Object>> r = financialTwoCategoryMapper.executeSQL(sqlBuffer.toString(), ConstantsUtil.STATUS_NORMAL, user.getId());
		
		result = r.size() > 0;
		if(!result){
			sqlBuffer = new StringBuffer();
			sqlBuffer.append("SELECT id, one_level_id, category_value, icon_name, ");
			sqlBuffer.append(" budget, category_order, is_default, is_system, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time ");
			sqlBuffer.append(" FROM t_financial_two_category");
			sqlBuffer.append(" where status = ? and is_system = ? order by id ");
			r = financialTwoCategoryMapper.executeSQL(sqlBuffer.toString(), ConstantsUtil.STATUS_NORMAL, true);
			
		}
		message.put("message", r);
		message.put("isSuccess", true);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取全部二级分类列表", StringUtil.getSuccessOrNoStr(result)).toString(), "getAll()", StringUtil.changeBooleanToInt(result), 0);
		return message.getMap();
	}
}