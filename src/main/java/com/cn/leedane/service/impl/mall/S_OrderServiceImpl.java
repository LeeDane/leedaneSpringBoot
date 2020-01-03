package com.cn.leedane.service.impl.mall;

import com.cn.leedane.exception.CompleteOrderDeleteException;
import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.mall.S_OrderHandler;
import com.cn.leedane.mall.taobao.api.DetailSimpleApi;
import com.cn.leedane.mapper.mall.S_OrderMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_OrderBean;
import com.cn.leedane.model.mall.S_PlatformProductBean;
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
	public ResponseModel add(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) throws Exception {
		logger.info("S_OrderServiceImpl-->add():jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_OrderBean orderBean = (S_OrderBean) sqlUtil.getBean(jo, S_OrderBean.class);

		//j校验必须的参数
		ParameterUnspecificationUtil.checkNullString(orderBean.getOrderCode(), "order code must not null.");
		ParameterUnspecificationUtil.checkNullString(orderBean.getProductCode(), "product code must not null.");
		ParameterUnspecificationUtil.checkNullString(orderBean.getPlatform(), "platform must not null.");

		if(!MallUtil.inPlatform(orderBean.getPlatform()))
			throw new ParameterUnspecificationException("not support platform .");


		//判断商品是否存在
		S_PlatformProductBean productBean = null;
		if(orderBean.getPlatform().equalsIgnoreCase(EnumUtil.ProductPlatformType.淘宝.value)){
			productBean = DetailSimpleApi.getDetailByMaterial(orderBean.getProductCode());
		}else if(orderBean.getPlatform().equalsIgnoreCase(EnumUtil.ProductPlatformType.京东.value)){
			productBean = com.cn.leedane.mall.jingdong.api.DetailSimpleApi.getDetail(orderBean.getProductCode());
		}else if(orderBean.getPlatform().equalsIgnoreCase(EnumUtil.ProductPlatformType.拼多多.value)){
			productBean = com.cn.leedane.mall.pdd.api.DetailSimpleApi.getDetail(orderBean.getProductCode());
		}else{
		}
		if(productBean == null)
			throw new NullPointerException("该商品找不到了，平台无法处理");

		orderBean.setPrice(MoneyUtil.twoDecimalPlaces(productBean.getPrice()));
		orderBean.setCashBack(MoneyUtil.twoDecimalPlaces(productBean.getCashBack()));
		orderBean.setCashBackRatio(MoneyUtil.twoDecimalPlaces(productBean.getCashBackRatio()));
		if(StringUtil.isNull(orderBean.getTitle()))
			orderBean.setTitle(productBean.getTitle());

		//判断库里是否已经存在相同记录的订单信息
		if(orderHandler.inRecode(orderBean.getPlatform(), orderBean.getOrderCode())){
			//对新修改的记录跟原来的记录做评分，分数低直接返回提示用户重新修改更加完整的信息再提交或者去订单申诉平台处理
			//如果分数高，将直接标记为审核中的状态保存，同时系统将原来的记录删掉，保存完整的记录并统一通过站内信息方式通知对方
		}
		String returnMsg = "已成功添加到订单！";
		Date createTime = new Date();
		orderBean.setStatus(MallOrderType.已提交.value);
		orderBean.setCreateTime(createTime);
		orderBean.setCreateUserId(user.getId());
		boolean result = false;
		try{
			result = orderMapper.save(orderBean) > 0;
			if(result)
				orderHandler.deleteNoDealOrderCache(user.getId());
		}catch(DuplicateKeyException e){ //唯一键约束异常不做处理
			returnMsg = "该记录已经存在，如果不是您的操作，可以到订单反馈中心去申诉";
		}
		ResponseModel responseModel = new ResponseModel();
		if(result)
			responseModel.ok().message(returnMsg);
		else
			responseModel.error().message(returnMsg).code(EnumUtil.ResponseCode.数据库保存失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"添加订单，编号为", orderBean.getOrderCode(), ",商品唯一编号为：", orderBean.getProductCode(), "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}
	
	@Override
	public ResponseModel update(long orderId, JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception {
		logger.info("S_OrderServiceImpl-->update(): orderId = "+ orderId +",jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_OrderBean updateOrderBean = (S_OrderBean) sqlUtil.getUpdateBean(jo, S_OrderBean.class);
		S_OrderBean orderBean = orderMapper.findById(S_OrderBean.class, orderId);
		if(orderBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该订单已不存在.value));

		if(orderBean.getStatus() != MallOrderType.已提交.value || orderBean.getStatus() != MallOrderType.有争议.value)
			throw new UnsupportedOperationException("非《已提交》或《有争议》状态，无法编辑");
		//j校验必须的参数
		ParameterUnspecificationUtil.checkNullString(updateOrderBean.getOrderCode(), "order code must not null.");
		ParameterUnspecificationUtil.checkNullString(updateOrderBean.getProductCode(), "product code must not null.");
		ParameterUnspecificationUtil.checkNullString(orderBean.getPlatform(), "platform must not null.");

		if(!MallUtil.inPlatform(updateOrderBean.getPlatform()))
			throw new ParameterUnspecificationException("not support platform .");

		//判断商品是否存在
		S_PlatformProductBean productBean = null;
		if(updateOrderBean.getPlatform().equalsIgnoreCase(EnumUtil.ProductPlatformType.淘宝.value)){
			productBean = DetailSimpleApi.getDetailByMaterial(updateOrderBean.getProductCode());
		}else if(updateOrderBean.getPlatform().equalsIgnoreCase(EnumUtil.ProductPlatformType.京东.value)){
			productBean = com.cn.leedane.mall.jingdong.api.DetailSimpleApi.getDetail(updateOrderBean.getProductCode());
		}else if(updateOrderBean.getPlatform().equalsIgnoreCase(EnumUtil.ProductPlatformType.拼多多.value)){
			productBean = com.cn.leedane.mall.pdd.api.DetailSimpleApi.getDetail(orderBean.getProductCode());
		}else{
		}
		if(productBean == null)
			throw new NullPointerException("该商品找不到了，平台无法处理");
		orderBean.setPrice(MoneyUtil.twoDecimalPlaces(productBean.getPrice()));
		orderBean.setCashBack(MoneyUtil.twoDecimalPlaces(productBean.getCashBack()));
		orderBean.setCashBackRatio(MoneyUtil.twoDecimalPlaces(productBean.getCashBackRatio()));
		orderBean.setTitle(productBean.getTitle());

		String returnMsg = "已成功修改该订单！";
		orderBean.setModifyTime(new Date());
		orderBean.setModifyUserId(user.getId());
		orderBean.setOrderCode(updateOrderBean.getOrderCode());
		orderBean.setProductCode(updateOrderBean.getProductCode());
//		orderBean.setTitle(updateOrderBean.getTitle());
//		orderBean.setReferrer(updateOrderBean.getReferrer());
		orderBean.setPlatform(updateOrderBean.getPlatform());
		orderBean.setOrderTime(updateOrderBean.getOrderTime());
		orderBean.setPayTime(updateOrderBean.getPayTime());
		orderBean.setOrderDetailId(updateOrderBean.getOrderDetailId());
		boolean result = false;
		try{
			result = orderMapper.update(orderBean) > 0;
			if(result)
				orderHandler.deleteNoDealOrderCache(user.getId());
		}catch(DuplicateKeyException e){ //唯一键约束异常不做处理
			result = true;
		}
		ResponseModel responseModel = new ResponseModel();
		if(result)
			responseModel.ok().message(returnMsg);
		else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value)).code(EnumUtil.ResponseCode.数据库修改失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改订单，订单ID为", orderBean.getId(), "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}
	
	@Override
	public ResponseModel getNoDealOrderNumber(UserBean user, HttpRequestInfoBean request) {
		logger.info("S_OrderServiceImpl-->getNoDealOrderNumber():user="+user.getId());
		return  new ResponseModel().ok().message(orderHandler.getNoDealOrderNumber(user.getId()));
	}

	@Override
	public LayuiTableResponseModel paging(int current, int pageSize, UserBean user, HttpRequestInfoBean request){
		logger.info("S_OrderServiceImpl-->paging():current=" +current +", pageSize="+ pageSize);
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		int start = SqlUtil.getPageStart(current, pageSize, 0);
		rs = orderMapper.paging(user.getId(), ConstantsUtil.STATUS_NORMAL, start, pageSize);
		
		if(CollectionUtil.isNotEmpty(rs)){
			for(Map<String, Object> m: rs){
				int status = StringUtil.changeObjectToInt(m.get("status"));
				m.put("status_text", EnumUtil.getMallOrderType(status));
				m.put("referrer", "<a class='layui-btn layui-btn-primary layui-btn-xs' lay-event='edit' style='height: 25px !important; line-height: 25px !important;'>详情</a>");
			}
		}
//		message.setCode(0);
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取订单列表，current="+ current, ", pageSize=", pageSize).toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
		responseModel.setData(rs).setCount(SqlUtil.getTotalByList(orderMapper.getTotal(DataTableType.商品订单.value, ""))).code(0);
		return responseModel;
//		return message.getMap();
	}
	
	@Override
	public ResponseModel delete(long orderId, UserBean user, HttpRequestInfoBean request){
		logger.info("S_OrderServiceImpl-->delete():orderId=" +orderId);
		S_OrderBean orderBean = orderMapper.findById(S_OrderBean.class, orderId);
		if(orderBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该订单已不存在.value));
		
		//只有自己的订单或者管理员才能做删除操作
		checkMallAdmin(user, user.getId());
		//已完成的订单不做处理
		if(orderBean.getStatus() == MallOrderType.已完成.value)
			throw new CompleteOrderDeleteException();
		
		boolean result = orderMapper.delete(orderBean) > 0;
		ResponseModel responseModel = new ResponseModel();
		if(result){
			responseModel.ok().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请求返回成功码.value));
			orderHandler.deleteNoDealOrderCache(user.getId()); //删除缓存
		}else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value)).code(EnumUtil.ResponseCode.删除失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除订单ID为："+ orderId, "的订单").toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}

}
