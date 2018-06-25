package com.cn.leedane.handler.stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.mapper.stock.StockSellMapper;
import com.cn.leedane.model.stock.StockSellBean;
import com.cn.leedane.model.stock.StockSellBeans;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 股票卖出记录的处理类
 * @author LeeDane
 * 2018年6月5日 下午3:56:11
 * version 1.0
 */
@Component
public class StockSellHandler {
	@Autowired
	private StockSellMapper stockSellMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private StockBuyHandler stockBuyHandler;
	
	/**
	 * 获取正常状态的股票卖出记录对象
	 * 不是自己的股票卖出记录将抛出权限异常
	 * @param userId
	 * @param stockId
	 * @param stockBuyId
	 * @param stockSellId
	 * @return
	 */
	public StockSellBean getNormalStockSell(int userId, int stockId, int stockBuyId, int stockSellId){
		StockSellBean stockSell = getStockSell(userId, stockId, stockBuyId, stockSellId);
		
		//不是自己的股票卖出记录将抛出权限异常
		if(stockSell == null || userId < 1
				||  (stockSell.getStatus() != ConstantsUtil.STATUS_NORMAL) 
				|| stockSell.getCreateUserId() != userId
				|| stockSell.getStockBuyId() != stockBuyId){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		}
		return stockSell;
	}
	
	/**
	 * 获取股票卖出记录对象(不去判断是否是正常状态的股票卖出记录)
	 * @param userId
	 * @param stockId
	 * @param stockBuyId
	 * @param stockSellId
	 * @return
	 */
	public StockSellBean getStockSell(int userId, int stockId, int stockBuyId, int stockSellId){
		if(stockSellId < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该股票卖出记录不存在.value));
		
		//校验股票购买记录是否是自己的
		stockBuyHandler.getNormalStockBuy(stockBuyId, userId, stockId);
		
		String key = getStockSellKey(stockSellId);
		Object obj = systemCache.getCache(key);
		StockSellBean stockSellBean = null;
		/*for(int i = 0; i < 22; i++)
			deleteStockSellBeanCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					stockSellBean =  (StockSellBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), StockSellBean.class);
					if(stockSellBean != null){
						systemCache.addCache(key, stockSellBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						stockSellBean = stockSellMapper.findById(StockSellBean.class, stockSellId);
						if(stockSellBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockSellBean));
								systemCache.addCache(key, stockSellBean);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				stockSellBean = stockSellMapper.findById(StockSellBean.class, stockSellId);
				if(stockSellBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockSellBean));
						systemCache.addCache(key, stockSellBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			stockSellBean = (StockSellBean)obj;
		}
		
		if(stockSellBean == null || stockSellBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return stockSellBean;
	}
	
	/**
	 * 获取该用户的所有股票卖出记录对象
	 * @param userId
	 * @return
	 */
	public List<StockSellBean> getStockSells(int stockId, int userId, int stockBuyId){
		//校验对应的股票购买记录是否是自己的
		stockBuyHandler.getNormalStockBuy(stockBuyId, userId, stockId);
		String key = getStockSellsKey(stockBuyId);
		Object obj = systemCache.getCache(key);
		StockSellBeans stockSellBeans = null;
		/*for(int i = 0; i < 22; i++)
			deleteStockSellBeansCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					stockSellBeans =  (StockSellBeans) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), StockSellBeans.class);
					if(stockSellBeans != null){
						systemCache.addCache(key, stockSellBeans);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<StockSellBean> list = stockSellMapper.getStockSells(userId, stockBuyId, ConstantsUtil.STATUS_NORMAL);
						if(CollectionUtil.isNotEmpty(list)){
							try {
								stockSellBeans = new StockSellBeans();
								stockSellBeans.setStockSells(list);
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockSellBeans));
								systemCache.addCache(key, stockSellBeans);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				List<StockSellBean> list = stockSellMapper.getStockSells(userId, stockBuyId, ConstantsUtil.STATUS_NORMAL);
				if(CollectionUtil.isNotEmpty(list)){
					try {
						stockSellBeans = new StockSellBeans();
						stockSellBeans.setStockSells(list);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockSellBeans));
						systemCache.addCache(key, stockSellBeans);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			stockSellBeans = (StockSellBeans)obj;
		}
		
		if(stockSellBeans != null ){
			return stockSellBeans.getStockSells();
		}
		return new ArrayList<StockSellBean>();
	}
	
	
	/**
	 * 根据股票卖出记录ID删除该股票卖出记录的cache和redis缓存
	 * @param stockSellId
	 * @return
	 */
	public boolean deleteStockSellBeanCache(int stockSellId){
		String key = getStockSellKey(stockSellId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 根据用户ID删除该用户所有股票卖出记录的cache和redis缓存
	 * @param stockBuyId
	 * @return
	 */
	public boolean deleteStockSellBeansCache(int stockBuyId){
		String key = getStockSellsKey(stockBuyId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}

	/**
	 * 获取股票卖出记录在redis的key
	 * @param stockSellId
	 * @return
	 */
	public static String getStockSellKey(int stockSellId){
		return ConstantsUtil.STOCK_SELL_REDIS + stockSellId;
	}
	
	/**
	 * 获取所有股票卖出记录在redis的key
	 * @param stockBuyId
	 * @return
	 */
	public static String getStockSellsKey(int stockBuyId){
		return ConstantsUtil.STOCK_SELLS_REDIS + stockBuyId;
	}
	
}
