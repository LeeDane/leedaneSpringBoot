package com.cn.leedane.service.impl.stock;

import com.cn.leedane.display.stock.StockBuyDisplay;
import com.cn.leedane.display.stock.StockDisplay;
import com.cn.leedane.display.stock.StockSellDisplay;
import com.cn.leedane.handler.stock.StockBuyHandler;
import com.cn.leedane.handler.stock.StockHandler;
import com.cn.leedane.handler.stock.StockSellHandler;
import com.cn.leedane.mapper.stock.StockMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.stock.StockBean;
import com.cn.leedane.model.stock.StockBuyBean;
import com.cn.leedane.model.stock.StockSellBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.stock.StockService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 股票的service的实现类
 * @author LeeDane
 * 2018年6月22日 下午3:38:15
 * version 1.0
 */
@Service("stockService")
public class StockServiceImpl implements StockService<StockBean>{
	Logger logger = Logger.getLogger(getClass());

	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private StockMapper stockMapper;
	
	@Autowired
	private StockHandler stockHandler;
	
	@Autowired
	private StockBuyHandler stockBuyHandler;
	
	@Autowired
	private StockSellHandler stockSellHandler;
	
	@Override
	public Map<String, Object> init(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("StockServiceImpl-->init():jo="+jo);
		ResponseMap message = new ResponseMap();
		List<StockDisplay> stockDisplays = new ArrayList<StockDisplay>();
		long userId = user.getId();
		List<StockBean> stockBeans = stockHandler.getStocks(userId);
		//处理股票列表的展示
		if(CollectionUtil.isNotEmpty(stockBeans)){
			for(StockBean stockBean:  stockBeans){
				StockDisplay stockDisplay = new StockDisplay();
				stockDisplay.setCode(stockBean.getCode());
				stockDisplay.setName(stockBean.getName());
				stockDisplay.setCreateTime(RelativeDateFormat.format(stockBean.getCreateTime(), "MM月dd日 HH:mm"));
				stockDisplay.setTime(RelativeDateFormat.format(stockBean.getModifyTime(), "MM月dd日 HH:mm"));
				stockDisplay.setModifyDate(DateUtil.DateToString(stockBean.getModifyTime(), "yyyy-MM-dd"));
				stockDisplay.setModifyTime(DateUtil.DateToString(stockBean.getModifyTime(), "HH:mm"));
				stockDisplay.setId(stockBean.getId());
				List<StockBuyBean> stockBuyBeans = stockBuyHandler.getStockBuys(userId, stockBean.getId());
				List<StockBuyDisplay> buyDisplays = new ArrayList<StockBuyDisplay>();
				int stockHolding = 0;
				if(CollectionUtil.isNotEmpty(stockBuyBeans)){
					for(StockBuyBean buyBean: stockBuyBeans){
						StockBuyDisplay buyDisplay = new StockBuyDisplay();
						buyDisplay.setNumber(buyBean.getNumber());
						buyDisplay.setPrice(buyBean.getPrice());
						buyDisplay.setStockId(buyBean.getStockId());
						buyDisplay.setId(buyBean.getId());
						buyDisplay.setCreateTime(RelativeDateFormat.format(buyBean.getCreateTime(), "MM月dd日 HH:mm"));
						buyDisplay.setTime(RelativeDateFormat.format(buyBean.getModifyTime(), "MM月dd日 HH:mm"));
						buyDisplay.setModifyDate(DateUtil.DateToString(buyBean.getModifyTime(), "yyyy-MM-dd"));
						buyDisplay.setModifyTime(DateUtil.DateToString(buyBean.getModifyTime(), "HH:mm"));
						int holding = buyBean.getNumber();
						List<StockSellBean> stockSellBeans = stockSellHandler.getStockSells(stockBean.getId(), userId, buyBean.getId());
						List<StockSellDisplay> sellDisplays = new ArrayList<StockSellDisplay>();
						if(CollectionUtil.isNotEmpty(stockSellBeans)){
							for(StockSellBean sellBean: stockSellBeans){
								StockSellDisplay sellDisplay = new StockSellDisplay();
								sellDisplay.setNumber(sellBean.getNumber());
								sellDisplay.setPrice(sellBean.getPrice());
								sellDisplay.setResidueNumber(sellBean.getResidueNumber());
								sellDisplay.setStockBuyId(buyBean.getId());
								sellDisplay.setStockId(stockBean.getId());
								sellDisplay.setId(sellBean.getId());
								sellDisplay.setCreateTime(RelativeDateFormat.format(sellBean.getCreateTime(), "MM月dd日 HH:mm"));
								sellDisplay.setTime(RelativeDateFormat.format(sellBean.getModifyTime(), "MM月dd日 HH:mm"));
								sellDisplay.setModifyDate(DateUtil.DateToString(sellBean.getModifyTime(), "yyyy-MM-dd"));
								sellDisplay.setModifyTime(DateUtil.DateToString(sellBean.getModifyTime(), "HH:mm"));
								holding -= sellBean.getNumber();
								sellDisplays.add(sellDisplay);
							}
						}
						stockHolding += holding;
						buyDisplay.setHolding(holding);
						buyDisplay.setSellOut(holding < 1);
						buyDisplay.setSells(sellDisplays);
						buyDisplays.add(buyDisplay);
					}
				}
				stockDisplay.setHolding(stockHolding);
				stockDisplay.setBuys(buyDisplays);
				stockDisplays.add(stockDisplay);
			}
		}
		
		message.put("message", stockDisplays);
		message.put("success", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> add(JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("StockServiceImpl-->add(), user=" +user.getAccount());
		SqlUtil sqlUtil = new SqlUtil();
		StockBean stockBean = (StockBean) sqlUtil.getBean(json, StockBean.class);
		ResponseMap message = new ResponseMap();
		stockBean.setCreateTime(new Date());
		stockBean.setCreateUserId(user.getId());
		stockBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		boolean result = stockMapper.save(stockBean) > 0;
		if(result){
			stockHandler.deleteStockBeansCache(user.getId()); //删除缓存该用户全部股票的缓存
			message.put("success", true);
			message.put("message", "您已经成功添加一条股票信息！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"添加股票。ID为", stockBean.getId(), "，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> update(long stockId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("StockServiceImpl-->update(), stockId= " + stockId +",user=" +user.getAccount());
		long userId = user.getId();
		StockBean oldStockBean = stockHandler.getNormalStock(stockId, userId);
		ResponseMap message = new ResponseMap();
		
		SqlUtil sqlUtil = new SqlUtil();
		StockBean stockBean = (StockBean) sqlUtil.getUpdateBean(json, oldStockBean);
		//stockBean.setModifyUserId(user.getId());
		//stockBean.setModifyTime(createTime);
		boolean result = stockMapper.update(stockBean) > 0;
		if(result){
			stockHandler.deleteStockBeanCache(stockId); //删除缓存该股票的缓存
			stockHandler.deleteStockBeansCache(userId); //删除缓存该用户全部股票的缓存
			message.put("success", true);
			message.put("message", "您的股票信息已经更新成功！");
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
	public Map<String, Object> delete(long stockId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("StockServiceImpl-->delete(), stockId= " + ",user=" +user.getAccount());
		long userId = user.getId();
		StockBean stockBean = stockHandler.getNormalStock(stockId, userId);
		ResponseMap message = new ResponseMap();
		
		//判断是否有买入记录
		List<StockBuyBean> stockBuyBeans = stockBuyHandler.getStockBuys(userId, stockId);
		if(CollectionUtil.isNotEmpty(stockBuyBeans)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先删除该股票的全部购买记录.value));
			message.put("responseCode", EnumUtil.ResponseCode.请先删除该股票的全部购买记录.value);
			return message.getMap();
		}
		
		boolean result = stockMapper.deleteById(StockBean.class, stockBean.getId()) > 0;
		if(result){
			stockHandler.deleteStockBeanCache(stockId); //删除缓存该股票的缓存
			stockHandler.deleteStockBeansCache(userId); //删除缓存该用户全部股票的缓存
			message.put("success", true);
			message.put("message", "您已成功删除股票！");
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
