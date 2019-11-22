package com.cn.leedane.service.impl.stock;

import com.cn.leedane.handler.stock.StockBuyHandler;
import com.cn.leedane.handler.stock.StockHandler;
import com.cn.leedane.handler.stock.StockSellHandler;
import com.cn.leedane.mapper.stock.StockSellMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.stock.StockSellBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.stock.StockSellService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 股票卖出记录的service的实现类
 * @author LeeDane
 * 2018年6月22日 下午6:06:24
 * version 1.0
 */
@Service("stockSellService")
public class StockSellServiceImpl implements StockSellService<StockSellBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private StockSellMapper stockSellMapper;
	
	@Autowired
	private StockHandler stockHandler;
	
	@Autowired
	private StockBuyHandler stockBuyHandler;
	
	@Autowired
	private StockSellHandler stockSellHandler;

	@Override
	public Map<String, Object> add(long stockId, long stockBuyId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("StockBuyServiceImpl-->add(), stockId="+ stockId +", stockBuyId="+ stockBuyId +", user=" +user.getAccount());
		long userId = user.getId();
		SqlUtil sqlUtil = new SqlUtil();
		StockSellBean stockSellBean = (StockSellBean) sqlUtil.getBean(json, StockSellBean.class);
		ResponseMap message = new ResponseMap();
		
		//判断是否超额卖出
		//获取买入数量
		int buyNumber = stockBuyHandler.getNormalStockBuy(stockBuyId, user.getId(), stockId).getNumber();
		int sellNumber = 0;
		List<StockSellBean> stockSellBeans = stockSellHandler.getStockSells(stockId, userId, stockBuyId);
		for(StockSellBean sellBean: stockSellBeans)
			sellNumber += sellBean.getNumber();
		if(buyNumber - sellNumber - stockSellBean.getNumber() < 0){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请核实是否超额卖出.value));
			message.put("responseCode", EnumUtil.ResponseCode.请核实是否超额卖出.value);
			return message.getMap();
		}
		
		stockSellBean.setCreateTime(new Date());
		stockSellBean.setCreateUserId(user.getId());
		stockSellBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		boolean result = stockSellMapper.save(stockSellBean) > 0;
		if(result){
			stockSellHandler.deleteStockSellBeansCache(stockBuyId);
			message.put("isSuccess", true);
			message.put("message", "您已经成功添加一条股票卖出信息！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"添加股票卖出记录，ID为", stockSellBean.getId(), "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> update(long stockId, long stockBuyId, long stockSellId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("StockBuyServiceImpl-->update(), stockId= " + stockId +", stockBuyId="+ stockBuyId +", stockSellId="+ stockSellId +",user=" +user.getAccount());
		long userId = user.getId();
		StockSellBean oldStockSellBean = stockSellHandler.getNormalStockSell(userId, stockId, stockBuyId, stockSellId);
		ResponseMap message = new ResponseMap();
		SqlUtil sqlUtil = new SqlUtil();
		StockSellBean stockSellBean = (StockSellBean) sqlUtil.getUpdateBean(json, oldStockSellBean);
		
		//判断是否超额卖出
		//获取买入数量
		int buyNumber = stockBuyHandler.getNormalStockBuy(stockBuyId, user.getId(), stockId).getNumber();
		int sellNumber = 0;
		List<StockSellBean> stockSellBeans = stockSellHandler.getStockSells(stockId, userId, stockBuyId);
		for(StockSellBean sellBean: stockSellBeans){
			//排除就的数据
			if(sellBean.getId() == oldStockSellBean.getId())
				continue;
			sellNumber += sellBean.getNumber();
		}
		if(buyNumber - sellNumber - stockSellBean.getNumber() < 0){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请核实是否超额卖出.value));
			message.put("responseCode", EnumUtil.ResponseCode.请核实是否超额卖出.value);
			return message.getMap();
		}
		//stockSellBean.setModifyUserId(user.getId());
		//stockSellBean.setModifyTime(createTime);
		boolean result = stockSellMapper.update(stockSellBean) > 0;
		if(result){
			stockSellHandler.deleteStockSellBeanCache(stockSellId); //删除缓存该股票卖出的缓存
			stockSellHandler.deleteStockSellBeansCache(stockBuyId); //删除缓存该用户全部股票卖出的缓存
			message.put("isSuccess", true);
			message.put("message", "您的股票卖出记录已经更新成功！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为股票ID为", stockId , "执行修改操作，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> delete(long stockId, long stockBuyId, long stockSellId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("StockBuyServiceImpl-->delete(), stockId= " + ", stockBuyId="+ stockBuyId +", stockSellId="+ stockSellId +",user=" +user.getAccount());
		long userId = user.getId();
		StockSellBean stockSellBean = stockSellHandler.getNormalStockSell(userId, stockId, stockBuyId, stockSellId);
		ResponseMap message = new ResponseMap();
		
		boolean result = stockSellMapper.deleteById(StockSellBean.class, stockSellBean.getId()) > 0;
		if(result){
			stockSellHandler.deleteStockSellBeanCache(stockSellId); //删除缓存该股票卖出的缓存
			stockSellHandler.deleteStockSellBeansCache(stockBuyId); //删除缓存该用户全部股票卖出的缓存
			message.put("isSuccess", true);
			message.put("message", "您已成功删除股票卖出记录！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除股票为", stockId, "的数据，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
}
