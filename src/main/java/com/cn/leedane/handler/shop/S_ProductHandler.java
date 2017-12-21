package com.cn.leedane.handler.shop;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.shop.S_ProductMapper;
import com.cn.leedane.model.shop.S_ProductBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 商品的处理类
 * @author LeeDane
 * 2017年11月7日 下午5:19:49
 * version 1.0
 */
@Component
public class S_ProductHandler {
	@Autowired
	private S_ProductMapper productMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private S_ShopHandler shopHandler;
	/**
	 * 获取正常状态的商品对象
	 * @param productId
	 * @return
	 */
	public S_ProductBean getNormalProductBean(int productId){
		S_ProductBean product = getProductBean(productId);
		if(product == null || product.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return product;
	}
	
	/**
	 * 获取商品对象(不去判断是否是正常状态的商品)
	 * @param productId
	 * @return
	 */
	public S_ProductBean getProductBean(int productId){
		String key = getProductKey(productId);
		Object obj = systemCache.getCache(key);
		S_ProductBean productBean = null;
		//deleteProductBeanCache(productId);
		/*for(int i = 0; i < 22; i++)
			deleteProductBeanCache(i);*/
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					productBean =  (S_ProductBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), S_ProductBean.class);
					if(productBean != null){
						systemCache.addCache(key, productBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<S_ProductBean> productBeans = productMapper.getProduct(productId, ConstantsUtil.STATUS_NORMAL);
						if(CollectionUtil.isNotEmpty(productBeans)){
							try {
								productBean = productBeans.get(0);
								productBean.setShop(shopHandler.getNormalShopBean(productBean.getShopId()));
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(productBean));
								systemCache.addCache(key, productBean);
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
				List<S_ProductBean> productBeans = productMapper.getProduct(productId, ConstantsUtil.STATUS_NORMAL);
				if(CollectionUtil.isNotEmpty(productBeans)){
					try {
						productBean = productBeans.get(0);
						productBean.setShop(shopHandler.getNormalShopBean(productBean.getShopId()));
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(productBean));
						systemCache.addCache(key, productBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			productBean = (S_ProductBean)obj;
		}
		
		if(productBean == null || productBean.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return productBean;
	}
	
	/**
	 * 根据商品ID删除该商品的cache和redis缓存
	 * @param productId
	 * @return
	 */
	public boolean deleteProductBeanCache(int productId){
		String key = getProductKey(productId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取商品在redis的key
	 * @param productId
	 * @return
	 */
	public static String getProductKey(int productId){
		return ConstantsUtil.PRODUCT_REDIS + productId;
	}

}
