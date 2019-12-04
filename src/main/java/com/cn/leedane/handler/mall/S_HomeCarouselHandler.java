package com.cn.leedane.handler.mall;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.mall.S_HomeCarouselMapper;
import com.cn.leedane.model.mall.S_HomeCarouselBean;
import com.cn.leedane.model.mall.S_HomeCarouselBeans;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 首页轮播商品的处理类
 * @author LeeDane
 * 2017年12月26日 下午2:41:59
 * version 1.0
 */
@Component
public class S_HomeCarouselHandler {
	@Autowired
	private S_HomeCarouselMapper homeCarouselMapper;
	
	@Autowired
	private S_ProductHandler productHandler;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;

	/**
	 * 获取轮播商品对象
	 * @return
	 */
	public S_HomeCarouselBeans getCarouselBeans(){
		String key = getCarouselKey();
		Object obj = systemCache.getCache(key);
		S_HomeCarouselBeans carouselBeans = null;
		//deleteCarouselBeansCache();
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					carouselBeans =  (S_HomeCarouselBeans) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), S_HomeCarouselBeans.class);
					if(carouselBeans != null){
						systemCache.addCache(key, carouselBeans);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						carouselBeans = getCarouselBeansByDatabase();
						if(CollectionUtil.isNotEmpty(carouselBeans.getHomeCarouselBeans())){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(carouselBeans));
								systemCache.addCache(key, carouselBeans);
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
				carouselBeans = getCarouselBeansByDatabase();
				if(CollectionUtil.isNotEmpty(carouselBeans.getHomeCarouselBeans())){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(carouselBeans));
						systemCache.addCache(key, carouselBeans);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			carouselBeans = (S_HomeCarouselBeans)obj;
		}
		return carouselBeans;
	}
	
	private S_HomeCarouselBeans getCarouselBeansByDatabase(){
		S_HomeCarouselBeans carouselBeans = new S_HomeCarouselBeans();
		List<S_HomeCarouselBean> list = homeCarouselMapper.carousel(ConstantsUtil.STATUS_NORMAL, 5);
		if(CollectionUtil.isNotEmpty(list)){
			/*List<S_CarouselBean> lCarouselBeans = new ArrayList<S_CarouselBean>();
			for(S_CarouselBean carouselBean: list){
				S_ProductBean productBean = productHandler.getNormalProductBean(carouselBean.getProductId());
				if(productBean != null){
					S_CarouselBean carouselBean2 = new S_CarouselBean();
					lCarouselBeans.add(productBean);
				}
			}*/
			if(list.size() > 0){
				carouselBeans.setHomeCarouselBeans(list);
				carouselBeans.setTotal(list.size());
			}
		}
		return carouselBeans;
	}
	
	/**
	 * 删除首页轮播商品的cache和redis缓存
	 * @return
	 */
	public boolean deleteCarouselBeansCache(){
		String key = getCarouselKey();
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取首页轮播商品在redis的key
	 * @return
	 */
	public static String getCarouselKey(){
		return ConstantsUtil.PRODUCT_REDIS + "_crsl";
	}

}
