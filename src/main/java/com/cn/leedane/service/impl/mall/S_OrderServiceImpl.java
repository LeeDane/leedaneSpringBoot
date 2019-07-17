package com.cn.leedane.service.impl.mall;

import com.cn.leedane.exception.CompleteOrderDeleteException;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.mall.S_OrderHandler;
import com.cn.leedane.mapper.mall.S_OrderMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_OrderBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.S_OrderService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.MallOrderType;
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
 * 订单的service的实现类
 * @author LeeDane
 * 2017年11月7日 下午4:48:03
 * version 1.0
 */
@Service("S_OrderService")
public class S_OrderServiceImpl extends MallRoleCheckService implements S_OrderService<S_OrderBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private S_OrderHandler orderHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private S_OrderMapper orderMapper;
	
	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("S_OrderServiceImpl-->add():jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_OrderBean orderBean = (S_OrderBean) sqlUtil.getBean(jo, S_OrderBean.class);
		ResponseMap message = new ResponseMap();
		if(orderBean.getOrderCode() == null)
			throw new ParameterUnspecificationException("订单编号不能为空！");
		
		if(orderBean.getProductCode() == null)
			throw new ParameterUnspecificationException("商品唯一编号不能为空！");
		
		String returnMsg = "已成功添加到订单！";
		Date createTime = new Date();
		orderBean.setStatus(MallOrderType.待结算佣金.value);
		orderBean.setCreateTime(createTime);
		orderBean.setCreateUserId(user.getId());
		boolean result = false;
		try{
			result = orderMapper.save(orderBean) > 0;
			if(result)
				orderHandler.deleteNoDealOrderCache(user.getId());
		}catch(DuplicateKeyException e){ //唯一键约束异常不做处理
			result = true;
		}
		
		message.put("isSuccess", result);
		if(result){
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"添加订单，编号为", orderBean.getOrderCode(), ",商品唯一编号为：", orderBean.getProductCode(), "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> update(int orderId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("S_OrderServiceImpl-->update(): orderId = "+ orderId +",jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_OrderBean updateOrderBean = (S_OrderBean) sqlUtil.getUpdateBean(jo, S_OrderBean.class);
		
		S_OrderBean orderBean = orderMapper.findById(S_OrderBean.class, orderId);
		if(orderBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该订单已不存在.value));
		
		ResponseMap message = new ResponseMap();
		if(updateOrderBean.getOrderCode() == null)
			throw new ParameterUnspecificationException("订单编号不能为空！");
		
		if(updateOrderBean.getProductCode() == null)
			throw new ParameterUnspecificationException("商品唯一编号不能为空！");
		
		if(orderBean.getStatus() == ConstantsUtil.STATUS_NORMAL)
			throw new CompleteOrderDeleteException();
		
		String returnMsg = "已成功修改该订单！";
		orderBean.setModifyTime(new Date());
		orderBean.setModifyUserId(user.getId());
		orderBean.setOrderCode(updateOrderBean.getOrderCode());
		orderBean.setProductCode(updateOrderBean.getProductCode());
		orderBean.setTitle(updateOrderBean.getTitle());
		orderBean.setReferrer(updateOrderBean.getReferrer());
		orderBean.setPlatform(updateOrderBean.getPlatform());
		boolean result = false;
		try{
			result = orderMapper.update(orderBean) > 0;
			if(result)
				orderHandler.deleteNoDealOrderCache(user.getId());
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
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改订单，订单ID为", orderBean.getId(), "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getNoDealOrderNumber(UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("S_OrderServiceImpl-->getNoDealOrderNumber():user="+user.getId());
		ResponseMap message = new ResponseMap();
		
		message.put("isSuccess", true);
		message.put("message", orderHandler.getNoDealOrderNumber(user.getId()));
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		
		return message.getMap();
	}
	
	
	@Override
	public Map<String, Object> paging(int current, int pageSize, UserBean user, HttpRequestInfoBean request){
		logger.info("S_OrderServiceImpl-->paging():current=" +current +", pageSize="+ pageSize);
		LayuiTableResponseMap message = new LayuiTableResponseMap();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		int start = SqlUtil.getPageStart(current, pageSize, 0);
		rs = orderMapper.paging(user.getId(), ConstantsUtil.STATUS_NORMAL, start, pageSize);
		
		if(CollectionUtil.isNotEmpty(rs)){
			for(Map<String, Object> m: rs){
				int status = StringUtil.changeObjectToInt(m.get("status"));
				m.put("status_text", EnumUtil.getMallOrderType(status));
			}
		}
		message.setCode(0);
		message.setCount(SqlUtil.getTotalByList(orderMapper.getTotal(DataTableType.商品订单.value, "")));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取订单列表，current="+ current, ", pageSize=", pageSize).toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("isSuccess", true);
		message.put("data", rs);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> delete(int orderId, UserBean user, HttpRequestInfoBean request){
		logger.info("S_OrderServiceImpl-->delete():orderId=" +orderId);
		ResponseMap message = new ResponseMap();
		S_OrderBean orderBean = orderMapper.findById(S_OrderBean.class, orderId);
		if(orderBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该订单已不存在.value));
		
		
		//只有自己的订单或者管理员才能做删除操作
		checkMallAdmin(user, user.getId());
					
		if(orderBean.getStatus() == ConstantsUtil.STATUS_NORMAL)
			throw new CompleteOrderDeleteException();
		
		boolean result = orderMapper.delete(orderBean) > 0;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.该成员已被移除出圈子.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			orderHandler.deleteNoDealOrderCache(user.getId()); //删除缓存
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除订单ID为："+ orderId, "的订单").toString(), "delete()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}

}
