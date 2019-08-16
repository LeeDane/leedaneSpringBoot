package com.cn.leedane.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.CategoryMapper;
import com.cn.leedane.model.CategoryBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.SerializeUtil;

/**
 * 分类的处理类
 * @author LeeDane
 * 2017年11月23日 上午11:27:43
 * version 1.0
 */
@Component
public class CategoryHandler {
	@Autowired
	private CategoryMapper categoryMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	/**
	 * 获取正常状态的分类对象
	 * @param categoryId
	 * @return
	 */
	public CategoryBean getNormalCategoryBean(int categoryId){
		CategoryBean category = getCategoryBean(categoryId);
		if(category == null || category.getStatus() != ConstantsUtil.STATUS_NORMAL){
			return null;
		}
		return category;
	}
	
	/**
	 * 获取分类对象(不去判断是否是正常状态的分类)
	 * @param categoryId
	 * @return
	 */
	public CategoryBean getCategoryBean(int categoryId){
		if(categoryId < 1)
			return null;

		String key = getCategoryKey(categoryId);
		Object obj = systemCache.getCache(key);
		CategoryBean categoryBean = null;
		//deleteCategoryBeanCache(categoryId);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					categoryBean =  (CategoryBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), CategoryBean.class);
					if(categoryBean != null){
						systemCache.addCache(key, categoryBean, true);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						categoryBean = categoryMapper.findById(CategoryBean.class, categoryId);
						if(categoryBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(categoryBean));
								systemCache.addCache(key, categoryBean, true);
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
				categoryBean = categoryMapper.findById(CategoryBean.class, categoryId);
				if(categoryBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(categoryBean));
						systemCache.addCache(key, categoryBean, true);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			categoryBean = (CategoryBean)obj;
		}

		return categoryBean;
	}
	
	/**
	 * 根据分类ID删除该分类的cache和redis缓存
	 * @param categoryId
	 * @return
	 */
	public boolean deleteCategoryBeanCache(int categoryId){
		String key = getCategoryKey(categoryId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取分类在redis的key
	 * @param categoryId
	 * @return
	 */
	public static String getCategoryKey(int categoryId){
		return ConstantsUtil.CATEGORY_REDIS + categoryId;
	}

}
