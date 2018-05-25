package com.cn.leedane.handler.mall;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.mall.S_HomeItemMapper;
import com.cn.leedane.mapper.mall.S_HomeItemProductMapper;
import com.cn.leedane.model.KeyValueBean;
import com.cn.leedane.model.mall.S_HomeItemBean;
import com.cn.leedane.model.mall.S_HomeItemBeans;
import com.cn.leedane.model.mall.S_HomeItemProductBean;
import com.cn.leedane.model.mall.S_HomeItemProductShowBean;
import com.cn.leedane.model.mall.S_HomeItemShowBean;
import com.cn.leedane.model.mall.S_ProductBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 商城首页分类相关的处理类
 * @author LeeDane
 * 2017年12月28日 上午10:32:39
 * version 1.0
 */
@Component
public class S_HomeItemHandler {

	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private S_HomeItemMapper homeItemMapper;
	
	@Autowired
	private S_ProductHandler productHandler;
	
	@Autowired
	private S_HomeItemProductMapper homeItemProductMapper;

	
	/**
	 * 获取要展示的商品分类列表
	 * @return
	 */
	public List<S_HomeItemBean> showCategoryList(){
		String key = getShowCategoryListKey();
		Object obj = systemCache.getCache(key);
		S_HomeItemBeans homeCategoryBeans = null;
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					homeCategoryBeans =  (S_HomeItemBeans) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), S_HomeItemBeans.class);
					if(homeCategoryBeans != null){
						systemCache.addCache(key, homeCategoryBeans);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<S_HomeItemBean> categoryBeansList = homeItemMapper.showCategoryList();
						if(CollectionUtil.isNotEmpty(categoryBeansList)){
							try {
								homeCategoryBeans = new S_HomeItemBeans();
								homeCategoryBeans.setHomeItemBeans(categoryBeansList);
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(homeCategoryBeans));
								systemCache.addCache(key, homeCategoryBeans);
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
				List<S_HomeItemBean> categoryBeansList = homeItemMapper.showCategoryList();
				if(CollectionUtil.isNotEmpty(categoryBeansList)){
					try {
						homeCategoryBeans = new S_HomeItemBeans();
						homeCategoryBeans.setHomeItemBeans(categoryBeansList);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(homeCategoryBeans));
						systemCache.addCache(key, homeCategoryBeans);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			homeCategoryBeans = (S_HomeItemBeans)obj;
		}
		
		return homeCategoryBeans == null ? new ArrayList<S_HomeItemBean>() :homeCategoryBeans.getHomeItemBeans();
	}
	
	/**
	 * 根据商城首页分类列表的cache和redis缓存
	 * @return
	 */
	public boolean deleteCategoryListCache(){
		String key = getShowCategoryListKey();
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取首页分类列表在redis的key
	 * @return
	 */
	public static String getShowCategoryListKey(){
		return ConstantsUtil.MALL_CATEGORY_REDIS + "show";
	}

	
	/**
	 * 获取分类的对象
	 * @param itemId
	 * @return
	 */
	public S_HomeItemShowBean getCategory(int itemId) {
		String key = getItemShowKey(itemId);
		Object obj = systemCache.getCache(key);
		S_HomeItemShowBean homeItemShowBean = null;
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					homeItemShowBean =  (S_HomeItemShowBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), S_HomeItemShowBean.class);
					if(homeItemShowBean != null){
						systemCache.addCache(key, homeItemShowBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						homeItemShowBean = getHomeItemShowBean(itemId);
						if(homeItemShowBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(homeItemShowBean));
								systemCache.addCache(key, homeItemShowBean);
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
				homeItemShowBean = getHomeItemShowBean(itemId);
				if(homeItemShowBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(homeItemShowBean));
						systemCache.addCache(key, homeItemShowBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			homeItemShowBean = (S_HomeItemShowBean)obj;
		}
		
		return homeItemShowBean;
	}
	
	private S_HomeItemShowBean getHomeItemShowBean(int itemId){
		S_HomeItemShowBean itemShowBean = null;
		List<S_HomeItemBean> homeItemBeans = homeItemMapper.findItemById(itemId);
		if(CollectionUtil.isNotEmpty(homeItemBeans)){
			S_HomeItemBean homeItemBean = homeItemBeans.get(0);
			itemShowBean = new S_HomeItemShowBean();
			itemShowBean.setCategoryId(homeItemBean.getCategoryId());
			itemShowBean.setItemId(homeItemBean.getId());
			itemShowBean.setCategoryText(homeItemBean.getCategoryText());
			itemShowBean.setNumber(homeItemBean.getNumber());
			itemShowBean.setOrder(homeItemBean.getCategoryOrder());
			if(StringUtil.isNotNull(homeItemBean.getChildren())){
				JSONArray json = JSONArray.fromObject(homeItemBean.getChildren());
				List<KeyValueBean> childrens = new ArrayList<KeyValueBean>();
				KeyValueBean keyValueBean = null;
				for(int i = 0; i < json.size(); i++){
					keyValueBean = new KeyValueBean();
					keyValueBean.setKey(json.getJSONObject(i).getString("key"));
					keyValueBean.setValue(json.getJSONObject(i).getString("value"));
					childrens.add(keyValueBean);
				}
				
				itemShowBean.setChildrens(childrens);
			}
			
			//处理商品
			List<S_HomeItemProductBean> productBeans = homeItemProductMapper.getProducts(itemId, homeItemBean.getNumber());
			if(CollectionUtil.isNotEmpty(productBeans)){
				List<S_HomeItemProductShowBean> pList = new ArrayList<S_HomeItemProductShowBean>();
				for(S_HomeItemProductBean bean: productBeans){
					S_HomeItemProductShowBean bean2 = new S_HomeItemProductShowBean();
					bean2.setCreateTime(bean.getCreateTime());
					bean2.setCreateUserId(bean.getCreateUserId());
					bean2.setId(bean.getId());
					bean2.setItemId(itemId);
					bean2.setProductBean(productHandler.getNormalProductBean(bean.getProductId()));
					pList.add(bean2);
				}
				itemShowBean.setProductBeans(pList);
			}
		}
		return itemShowBean;		
	}
	
	/**
	 * 根据商城首页分类项的cache和redis缓存
	 * @param itemId
	 * @return
	 */
	public boolean deleteItemShowCache(int itemId){
		String key = getItemShowKey(itemId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取首页分类项在redis的key
	 * @param itemId
	 * @return
	 */
	public static String getItemShowKey(int itemId){
		return ConstantsUtil.MALL_CATEGORY_REDIS + itemId;
	}

}
