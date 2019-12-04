package com.cn.leedane.handler.stock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.mapper.stock.StockMapper;
import com.cn.leedane.model.stock.StockBean;
import com.cn.leedane.model.stock.StockBeans;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 股票的处理类
 * @author LeeDane
 * 2018年6月21日 下午2:45:47
 * version 1.0
 */
@Component
public class StockHandler {
	@Autowired
	private StockMapper stockMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	
	/**
	 * 获取正常状态的股票对象
	 * 不是自己的股票将抛出权限异常
	 * @param stockId
	 * @return
	 */
	public StockBean getNormalStock(long stockId, long userId){
		StockBean stock = getStock(stockId);
		
		//不是自己的股票将抛出权限异常
		if(stock == null || userId < 1
				||  (stock.getStatus() != ConstantsUtil.STATUS_NORMAL) 
				|| stock.getCreateUserId() != userId){
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		}
		return stock;
	}
	
	/**
	 * 获取股票对象(不去判断是否是正常状态的股票)
	 * @param stockId
	 * @return
	 */
	public StockBean getStock(long stockId){
		if(stockId < 1)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该股票不存在.value));
		String key = getStockKey(stockId);
		Object obj = systemCache.getCache(key);
		StockBean stockBean = null;
		/*for(int i = 0; i < 22; i++)
			deleteStockBeanCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					stockBean =  (StockBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), StockBean.class);
					if(stockBean != null){
						systemCache.addCache(key, stockBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						stockBean = stockMapper.findById(StockBean.class, stockId);
						if(stockBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockBean));
								systemCache.addCache(key, stockBean);
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
				stockBean = stockMapper.findById(StockBean.class, stockId);
				if(stockBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockBean));
						systemCache.addCache(key, stockBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			stockBean = (StockBean)obj;
		}
		
		if(stockBean == null || stockBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return stockBean;
	}
	
	/**
	 * 获取该用户的所有股票对象
	 * @param userId
	 * @return
	 */
	public List<StockBean> getStocks(long userId){
		String key = getStocksKey(userId);
		Object obj = systemCache.getCache(key);
		StockBeans stockBeans = null;
		/*for(int i = 0; i < 22; i++)
			deleteStockBeansCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					stockBeans =  (StockBeans) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), StockBeans.class);
					if(stockBeans != null){
						systemCache.addCache(key, stockBeans);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<StockBean> list = stockMapper.getStocks(userId, ConstantsUtil.STATUS_NORMAL);
						if(CollectionUtil.isNotEmpty(list)){
							try {
								stockBeans = new StockBeans();
								stockBeans.setStocks(list);
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockBeans));
								systemCache.addCache(key, stockBeans);
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
				List<StockBean> list = stockMapper.getStocks(userId, ConstantsUtil.STATUS_NORMAL);
				if(CollectionUtil.isNotEmpty(list)){
					try {
						stockBeans = new StockBeans();
						stockBeans.setStocks(list);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(stockBeans));
						systemCache.addCache(key, stockBeans);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			stockBeans = (StockBeans)obj;
		}
		
		if(stockBeans != null ){
			return stockBeans.getStocks();
		}
		return new ArrayList<StockBean>();
	}
	
	
	/**
	 * 根据股票ID删除该股票的cache和redis缓存
	 * @param stockId
	 * @return
	 */
	public boolean deleteStockBeanCache(long stockId){
		String key = getStockKey(stockId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 根据用户ID删除该用户所有股票的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deleteStockBeansCache(long userId){
		String key = getStocksKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}

	/**
	 * 获取股票在redis的key
	 * @param stockId
	 * @return
	 */
	public static String getStockKey(long stockId){
		return ConstantsUtil.STOCK_REDIS + stockId;
	}
	
	/**
	 * 获取所有股票在redis的key
	 * @param userId
	 * @return
	 */
	public static String getStocksKey(long userId){
		return ConstantsUtil.STOCKS_REDIS + userId;
	}
	
}
