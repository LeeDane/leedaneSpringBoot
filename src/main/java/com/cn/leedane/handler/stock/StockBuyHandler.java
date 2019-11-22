package com.cn.leedane.handler.stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.mapper.stock.StockBuyMapper;
import com.cn.leedane.model.stock.StockBean;
import com.cn.leedane.model.stock.StockBuyBean;
import com.cn.leedane.model.stock.StockBuyBeans;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 股票购买的处理类
 * @author LeeDane
 * 2018年6月21日 下午3:06:56
 * version 1.0
 */
@Component
public class StockBuyHandler {
	@Autowired
	private StockBuyMapper stockBuyMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private StockHandler stockHandler;
	
	/**
	 * 获取正常状态的股票购买对象
	 * 不是自己的股票购买将抛出权限异常
	 * @param stockBuyId
	 * @param userId
	 * @param stockId
	 * @return
	 */
	public StockBuyBean getNormalStockBuy(long stockBuyId, long userId, long stockId){
		//校验对应的股票是否是自己的
		StockBean stockBean = stockHandler.getNormalStock(stockId, userId);
		StockBuyBean stockBuy = getStockBuy(userId, stockBean.getId(), stockBuyId);
		
		//不是自己的股票购买将抛出权限异常
		if(stockBuy == null || userId < 1
				||  (stockBuy.getStatus() != ConstantsUtil.STATUS_NORMAL) 
				|| stockBuy.getCreateUserId() != userId
				|| stockBuy.getStockId() != stockId){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		}
		return stockBuy;
	}
	
	/**
	 * 获取股票购买对象(不去判断是否是正常状态的股票购买)
	 * @param userId
	 * @param stockBuyId
	 * @param stockId
	 * @return
	 */
	public StockBuyBean getStockBuy(long userId, long stockId, long stockBuyId){
		if(stockBuyId < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该股票购买记录不存在.value));
		
		//校验股票是否是自己的股票
		stockHandler.getNormalStock(stockId, userId);
		
		String key = getStockBuyKey(stockBuyId);
		Object obj = systemCache.getCache(key);
		StockBuyBean stockBuyBean = null;
		/*for(int i = 0; i < 22; i++)
			deleteStockBuyBeanCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					stockBuyBean =  (StockBuyBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), StockBuyBean.class);
					if(stockBuyBean != null){
						systemCache.addCache(key, stockBuyBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						stockBuyBean = stockBuyMapper.findById(StockBuyBean.class, stockBuyId);
						if(stockBuyBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockBuyBean));
								systemCache.addCache(key, stockBuyBean);
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
				stockBuyBean = stockBuyMapper.findById(StockBuyBean.class, stockBuyId);
				if(stockBuyBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockBuyBean));
						systemCache.addCache(key, stockBuyBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			stockBuyBean = (StockBuyBean)obj;
		}
		
		if(stockBuyBean == null || stockBuyBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return stockBuyBean;
	}
	
	/**
	 * 获取该股票的所有股票购买记录对象
	 * @param userId
	 * @param stockId
	 * @return
	 */
	public List<StockBuyBean> getStockBuys(long userId, long stockId){
		//校验对应的股票是否是自己的
		StockBean stockBean = stockHandler.getNormalStock(stockId, userId);
		
		String key = getStockBuysKey(stockBean.getId());
		Object obj = systemCache.getCache(key);
		StockBuyBeans stockBuyBeans = null;
		/*for(int i = 0; i < 22; i++)
			deleteStockBuyBeansCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					stockBuyBeans =  (StockBuyBeans) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), StockBuyBeans.class);
					if(stockBuyBeans != null){
						systemCache.addCache(key, stockBuyBeans);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<StockBuyBean> list = stockBuyMapper.getStockBuys(userId, stockId, ConstantsUtil.STATUS_NORMAL);
						if(CollectionUtil.isNotEmpty(list)){
							try {
								stockBuyBeans = new StockBuyBeans();
								stockBuyBeans.setStockBuys(list);
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockBuyBeans));
								systemCache.addCache(key, stockBuyBeans);
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
				List<StockBuyBean> list = stockBuyMapper.getStockBuys(userId, stockId, ConstantsUtil.STATUS_NORMAL);
				if(CollectionUtil.isNotEmpty(list)){
					try {
						stockBuyBeans = new StockBuyBeans();
						stockBuyBeans.setStockBuys(list);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockBuyBeans));
						systemCache.addCache(key, stockBuyBeans);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			stockBuyBeans = (StockBuyBeans)obj;
		}
		
		if(stockBuyBeans != null ){
			return stockBuyBeans.getStockBuys();
		}
		return new ArrayList<StockBuyBean>();
	}
	
	
	/**
	 * 根据股票购买ID删除该股票购买的cache和redis缓存
	 * @param stockBuyId
	 * @return
	 */
	public boolean deleteStockBuyBeanCache(long stockBuyId){
		String key = getStockBuyKey(stockBuyId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 根据股票ID删除该股票的所有购买记录的cache和redis缓存
	 * @param stockId
	 * @return
	 */
	public boolean deleteStockBuyBeansCache(long stockId){
		String key = getStockBuysKey(stockId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}

	/**
	 * 获取股票购买在redis的key
	 * @param stockBuyId
	 * @return
	 */
	public static String getStockBuyKey(long stockBuyId){
		return ConstantsUtil.STOCK_BUY_REDIS + stockBuyId;
	}
	
	/**
	 * 获取所有股票购买在redis的key
	 * @param stockId
	 * @return
	 */
	public static String getStockBuysKey(long stockId){
		return ConstantsUtil.STOCK_BUYS_REDIS + stockId;
	}
	
}
