package com.cn.leedane.service.impl.stock;

import com.cn.leedane.handler.stock.StockBuyHandler;
import com.cn.leedane.handler.stock.StockHandler;
import com.cn.leedane.handler.stock.StockSellHandler;
import com.cn.leedane.mapper.stock.StockBuyMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.stock.StockBuyBean;
import com.cn.leedane.model.stock.StockSellBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.stock.StockBuyService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 股票购买记录的service的实现类
 * @author LeeDane
 * 2018年6月22日 下午5:35:55
 * version 1.0
 */
@Service("stockBuyService")
public class StockBuyServiceImpl implements StockBuyService<StockBuyBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private StockBuyMapper stockBuyMapper;
	
	@Autowired
	private StockHandler stockHandler;
	
	@Autowired
	private StockBuyHandler stockBuyHandler;
	
	@Autowired
	private StockSellHandler stockSellHandler;

	@Override
	public Map<String, Object> add(int stockId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("StockBuyServiceImpl-->add(), stockId="+ stockId +", user=" +user.getAccount());
		SqlUtil sqlUtil = new SqlUtil();
		StockBuyBean stockBuyBean = (StockBuyBean) sqlUtil.getBean(json, StockBuyBean.class);
		ResponseMap message = new ResponseMap();
		stockBuyBean.setCreateTime(new Date());
		stockBuyBean.setCreateUserId(user.getId());
		stockBuyBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		boolean result = stockBuyMapper.save(stockBuyBean) > 0;
		if(result){
			stockBuyHandler.deleteStockBuyBeansCache(stockId); //删除缓存该用户全部股票购买的缓存
			message.put("isSuccess", true);
			message.put("message", "您已经成功添加一条股票购买信息！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"添加股票购买记录，ID为", stockBuyBean.getId(), "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> update(int stockId, int stockBuyId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("StockBuyServiceImpl-->update(), stockId= " + stockId +", stockBuyId="+ stockBuyId +",user=" +user.getAccount());
		int userId = user.getId();
		StockBuyBean oldStockBuyBean = stockBuyHandler.getNormalStockBuy(stockBuyId, userId, stockId);
		ResponseMap message = new ResponseMap();
		
		SqlUtil sqlUtil = new SqlUtil();
		StockBuyBean stockBuyBean = (StockBuyBean) sqlUtil.getUpdateBean(json, oldStockBuyBean);
		//stockBuyBean.setModifyUserId(user.getId());
		//stockBuyBean.setModifyTime(createTime);
		boolean result = stockBuyMapper.update(stockBuyBean) > 0;
		if(result){
			stockBuyHandler.deleteStockBuyBeanCache(stockBuyId); //删除缓存该股票购买的缓存
			stockBuyHandler.deleteStockBuyBeansCache(stockId); //删除缓存该用户全部股票购买的缓存
			message.put("isSuccess", true);
			message.put("message", "您的股票购买记录已经更新成功！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为股票ID为", stockId , "执行修改操作，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> delete(int stockId, int stockBuyId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("StockBuyServiceImpl-->delete(), stockId= " + ", stockBuyId="+ stockBuyId +",user=" +user.getAccount());
		int userId = user.getId();
		StockBuyBean stockBuyBean = stockBuyHandler.getNormalStockBuy(stockBuyId, userId, stockId);
		ResponseMap message = new ResponseMap();
		
		//判断是否有买入记录
		List<StockSellBean> stockSellBeans = stockSellHandler.getStockSells(stockId, userId, stockBuyId);
		if(CollectionUtil.isNotEmpty(stockSellBeans)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先删除该股票购买记录所对应的全部卖出记录.value));
			message.put("responseCode", EnumUtil.ResponseCode.请先删除该股票购买记录所对应的全部卖出记录.value);
			return message.getMap();
		}
		
		boolean result = stockBuyMapper.deleteById(StockBuyBean.class, stockBuyBean.getId()) > 0;
		if(result){
			stockBuyHandler.deleteStockBuyBeanCache(stockBuyId); //删除缓存该股票购买的缓存
			stockBuyHandler.deleteStockBuyBeansCache(stockId); //删除缓存该用户全部股票购买的缓存
			message.put("isSuccess", true);
			message.put("message", "您已成功删除股票购买记录！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除股票为", stockId, "的数据，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, 0);	
		return message.getMap();
	}
}
