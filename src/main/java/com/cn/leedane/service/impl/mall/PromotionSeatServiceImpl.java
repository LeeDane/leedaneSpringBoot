package com.cn.leedane.service.impl.mall;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.mall.PromotionSeatMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_PromotionSeatBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.PromotionSeatService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 推广位管理的service的实现类
 * @author LeeDane
 * 2019年11月7日 下午4:48:03
 * version 1.0
 */
@Service("promotionSeatService")
public class PromotionSeatServiceImpl extends MallRoleCheckService implements PromotionSeatService<S_PromotionSeatBean> {
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private PromotionSeatMapper promotionSeatMapper;

	@Autowired
	private UserHandler userHandler;
	
	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("PromotionSeatServiceImpl-->add():jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_PromotionSeatBean promotionSeatBean = (S_PromotionSeatBean) sqlUtil.getBean(jo, S_PromotionSeatBean.class);

		//j校验必须的参数
		ParameterUnspecificationUtil.checkPlatform(promotionSeatBean.getPlatform());
		ParameterUnspecificationUtil.checkNullString(promotionSeatBean.getSeatName(), "seat name must not null.");

		ResponseMap message = new ResponseMap();

		//只有商城管理员才能做删除操作
		checkMallAdmin(user);

		String returnMsg = "新增推广位成功！";
		Date createTime = new Date();
		promotionSeatBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		promotionSeatBean.setCreateTime(createTime);
		promotionSeatBean.setCreateUserId(user.getId());
		boolean result = false;
		try{
			result = promotionSeatMapper.save(promotionSeatBean) > 0;
		}catch(DuplicateKeyException e){ //唯一键约束异常不做处理
			returnMsg = "该记录已经存在，请认真核实。";
		}
		
		message.put("isSuccess", result);
		message.put("message", returnMsg);
		if(result){
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"添加推广位，推广位ID为", promotionSeatBean.getSeatId(), ",推广位唯一编号为：", promotionSeatBean.getId(), "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> update(long promotionSeatId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) throws Exception {
		
		logger.info("PromotionSeatServiceImpl-->update(): promotionSeatId = "+ promotionSeatId +",jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_PromotionSeatBean updatePromotionSeatBean = (S_PromotionSeatBean) sqlUtil.getUpdateBean(jo, S_PromotionSeatBean.class);

		S_PromotionSeatBean promotionSeatBean = promotionSeatMapper.findById(S_PromotionSeatBean.class, promotionSeatId);
		if(promotionSeatBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该推广位不存在.value));

		ResponseMap message = new ResponseMap();
		//只有商城管理员才能做删除操作
		checkMallAdmin(user);
		//j校验必须的参数
		//j校验必须的参数
		ParameterUnspecificationUtil.checkPlatform(updatePromotionSeatBean.getPlatform());
		ParameterUnspecificationUtil.checkNullString(updatePromotionSeatBean.getSeatName(), "seat name must not null.");


		promotionSeatBean.setSeatId(updatePromotionSeatBean.getSeatId());
		promotionSeatBean.setSeatName(updatePromotionSeatBean.getSeatName());
		String returnMsg = "推广位修改成功！";
		promotionSeatBean.setModifyTime(new Date());
		promotionSeatBean.setModifyUserId(user.getId());
		promotionSeatBean.setPlatform(updatePromotionSeatBean.getPlatform());
		boolean result = false;
		try{
			result = promotionSeatMapper.update(updatePromotionSeatBean) > 0;
		}catch(DuplicateKeyException e){ //唯一键约束异常不做处理
			result = true;
		}
		message.put("isSuccess", result);
		if(result){
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改推广位，推广位ID为", promotionSeatBean.getId(), "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> paging(int current, int pageSize, JSONObject json, UserBean user, HttpRequestInfoBean request){
		logger.info("PromotionSeatServiceImpl-->paging():current=" +current +", pageSize="+ pageSize+", json="+ json);
		LayuiTableResponseMap message = new LayuiTableResponseMap();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		int start = SqlUtil.getPageStart(current, pageSize, 0);
		String platform = JsonUtil.getStringValue(json, "platform", null);
		rs = promotionSeatMapper.paging(platform, ConstantsUtil.STATUS_NORMAL, start, pageSize);
		
		if(CollectionUtil.isNotEmpty(rs)){
			for(Map<String, Object> m: rs){
				int userId = StringUtil.changeObjectToInt(m.get("user_id"));
				int createUserId = StringUtil.changeObjectToInt(m.get("create_user_id"));
				m.put("createUser", userHandler.getUserName(createUserId));
				if(userId > 0)
					m.put("allotUser", userHandler.getUserName(userId));
			}
		}
		message.setCode(0);
		message.setCount(SqlUtil.getTotalByList(promotionSeatMapper.getTotal(DataTableType.推广位管理.value, "")));
		message.put("isSuccess", true);
		message.put("data", rs);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> delete(long promotionSeatId, UserBean user, HttpRequestInfoBean request){
		logger.info("PromotionSeatServiceImpl-->delete():promotionSeatId=" +promotionSeatId);
		ResponseMap message = new ResponseMap();
		S_PromotionSeatBean promotionSeatBean = promotionSeatMapper.findById(S_PromotionSeatBean.class, promotionSeatId);
		if(promotionSeatBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该推广位不存在.value));
		
		
		//只有商城管理员才能做删除操作
		checkMallAdmin(user, user.getId());
		
		boolean result = promotionSeatMapper.delete(promotionSeatBean) > 0;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请求返回成功码.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除推广位ID为："+ promotionSeatBean).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

}
