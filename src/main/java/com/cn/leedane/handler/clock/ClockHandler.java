package com.cn.leedane.handler.clock;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.display.clock.ClockDisplay;
import com.cn.leedane.display.clock.ClockDisplayGroup;
import com.cn.leedane.display.clock.ClockDisplayGroups;
import com.cn.leedane.display.clock.ClockDisplays;
import com.cn.leedane.mapper.CategoryMapper;
import com.cn.leedane.mapper.clock.ClockMapper;
import com.cn.leedane.model.baby.BabyBean;
import com.cn.leedane.model.baby.BabyBeans;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.SerializeUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 任务提醒的处理类
 * @author LeeDane
 * 2018年8月7日 上午11:39:34
 * version 1.0
 */
@Component
public class ClockHandler {
	@Autowired
	private ClockMapper clockMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private SystemCache systemCache;
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	@Value("${constant.clock.system.category.id}")
    public int CLOCK_SYSTEM_CATEGORY_ID;
	
	
//	/**
//	 * 获取正常状态的任务对象
//	 * @param bookId
//	 * @return
//	 */
//	public ClockBean getNormalClock(int clockId){
//		ClockBean clock = getClock(clockId);
//		
//		//不是自己的任务或者是不属于自己的任务将抛出权限异常
//		if(clock == null ||  (clock.getStatus() != ConstantsUtil.STATUS_NORMAL)){
//			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.您要操作的书籍不存在.value));
//		}
//		return stock;
//	}
//	
//	/**
//	 * 获取书籍对象(不去判断是否是正常状态的书籍)
//	 * @param bookId
//	 * @return
//	 */
//	public BookBean getBook(int bookId){
//		if(bookId < 1)
//			return null;
//		String key = getBookKey(bookId);
//		Object obj = systemCache.getCache(key);
//		BookBean bookBean = null;
//		/*for(int i = 0; i < 22; i++)
//			deleteStockBeanCache(i);*/
//		if(obj == ""){
//			if(redisUtil.hasKey(key)){
//				try {
//					bookBean =  (BookBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), BookBean.class);
//					if(bookBean != null){
//						systemCache.addCache(key, bookBean);
//					}else{
//						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
//						redisUtil.delete(key);
//						bookBean = bookMapper.findById(BookBean.class, bookId);
//						if(bookBean != null){
//							try {
//								redisUtil.addSerialize(key, SerializeUtil.serializeObject(bookBean));
//								systemCache.addCache(key, bookBean);
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				} catch (ClassNotFoundException e) {
//					e.printStackTrace();
//				}catch (IOException e) {
//					e.printStackTrace();
//				}
//			}else{//redis没有的处理
//				bookBean = bookMapper.findById(BookBean.class, bookId);
//				if(bookBean != null){
//					try {
//						redisUtil.addSerialize(key, SerializeUtil.serializeObject(bookBean));
//						systemCache.addCache(key, bookBean);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}else{
//			bookBean = (BookBean)obj;
//		}
//		return bookBean;
//	}
	
	/**
	 * 获取该用户的所有进行中的任务对象
	 * @param userId
	 * @param week
	 * @return
	 */
	public List<ClockDisplay> dateClocks(int userId, Date systemTime, int week){
		String key = getClocksKey(userId);
		//Object obj = systemCache.getCache(key);
		ClockDisplays clockDisplays = null;
		//if(obj == ""){
			
			if(redisUtil.hasKey(key)){
				try {
					clockDisplays =  (ClockDisplays) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), ClockDisplays.class);
//					if(clockDisplays != null){
//						systemCache.addCache(key, clockDisplays);
//					}else{
//						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
//						redisUtil.delete(key);
//						List<ClockDisplay> list = clockMapper.dateClocks(userId, week +"", DateUtil.DateToString(systemTime, "yyyy-MM-dd"));
//						if(CollectionUtil.isNotEmpty(list)){
//							try {
//								clockDisplays = new ClockDisplays();
//								clockDisplays.setClockDisplays(list);
//								redisUtil.addSerialize(key, SerializeUtil.serializeObject(clockDisplays));
//								systemCache.addCache(key, clockDisplays);
//								redisUtil.expire(key, seconds);
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				//计算当天剩余的秒数
				int seconds = DateUtil.leftSeconds(systemTime, DateUtil.stringToDate(DateUtil.DateToString(systemTime, "yyyy-MM-dd") +" 23:59:59","yyyy-MM-dd HH:mm:ss"));
				if(seconds < 1)
					seconds = 0;
				List<ClockDisplay> list = clockMapper.dateClocks(userId, week +"", DateUtil.DateToString(systemTime, "yyyy-MM-dd"));
				if(CollectionUtil.isNotEmpty(list)){
					try {
						clockDisplays = new ClockDisplays();
						clockDisplays.setClockDisplays(list);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(clockDisplays));
						systemCache.addCache(key, clockDisplays);
						redisUtil.expire(key, seconds);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
//		}else{
//			clockDisplays = (ClockDisplays)obj;
//		}
		
		if(clockDisplays != null ){
			return clockDisplays.getClockDisplays();
		}
		return new ArrayList<ClockDisplay>();
	}
	
	/**
	 * 获取系统的分类任务(每两个小时自动刷新缓存)
	 * @return
	 */
	public List<ClockDisplayGroup> systemClocks(){
		String key = getSystemClocksKey();
		//Object obj = systemCache.getCache(key);
		ClockDisplayGroups clockDisplayGroups = null;
		/*for(int i = 0; i < 22; i++)
			deleteBabyBeansCache(i);*/
		//if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					clockDisplayGroups =  (ClockDisplayGroups) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), ClockDisplayGroups.class);
					if(clockDisplayGroups == null){
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						List<ClockDisplay> list = clockMapper.systemClocks();
						if(CollectionUtil.isNotEmpty(list)){
							try {
								List<ClockDisplayGroup> groups = new ArrayList<ClockDisplayGroup>();
								Map<Integer, List<ClockDisplay>> map = new HashMap<Integer, List<ClockDisplay>>();
								//获取总的子分类
								List<Map<String, Object>> caList = categoryMapper.children(CLOCK_SYSTEM_CATEGORY_ID, 0);
								List<ClockDisplay> clockDisplays = clockMapper.systemClocks();
								if(CollectionUtil.isNotEmpty(caList) && CollectionUtil.isNotEmpty(clockDisplays)){
									for(Map<String, Object> caMap: caList){
										map.put(StringUtil.changeObjectToInt(caMap.get("id")), new ArrayList<ClockDisplay>());
									}
									
									List<ClockDisplay> tempClockDisplays;
									for(ClockDisplay clockDisplay: clockDisplays){
										Integer categoryId = Integer.valueOf(clockDisplay.getCategoryId());
										if(map.containsKey(categoryId)){
											tempClockDisplays = map.get(categoryId);
										}else{
											tempClockDisplays = new ArrayList<ClockDisplay>();
										}
										tempClockDisplays.add(clockDisplay);
										map.put(categoryId, tempClockDisplays);
									}
									
									for(Map<String, Object> caMap: caList){
										ClockDisplayGroup group = new ClockDisplayGroup();
										group.setName(StringUtil.changeNotNull(caMap.get("text")));
										group.setClockDisplays(map.get(Integer.valueOf(StringUtil.changeObjectToInt(caMap.get("id")))));
										groups.add(group);
									}
								}
								clockDisplayGroups = new ClockDisplayGroups();
								clockDisplayGroups.setClockDisplayGroups(groups);
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(clockDisplayGroups));
								redisUtil.expire(key, 60 * 60 * 2);//缓存两个小时
//								systemCache.addCache(key, clockDisplayGroups);
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
				List<ClockDisplay> list = clockMapper.systemClocks();
				if(CollectionUtil.isNotEmpty(list)){
					try {
						List<ClockDisplayGroup> groups = new ArrayList<ClockDisplayGroup>();
						Map<Integer, List<ClockDisplay>> map = new HashMap<Integer, List<ClockDisplay>>();
						//获取总的子分类
						List<Map<String, Object>> caList = categoryMapper.children(CLOCK_SYSTEM_CATEGORY_ID, 0);
						List<ClockDisplay> clockDisplays = clockMapper.systemClocks();
						if(CollectionUtil.isNotEmpty(caList) && CollectionUtil.isNotEmpty(clockDisplays)){
							for(Map<String, Object> caMap: caList){
								map.put(StringUtil.changeObjectToInt(caMap.get("id")), new ArrayList<ClockDisplay>());
							}
							
							List<ClockDisplay> tempClockDisplays;
							for(ClockDisplay clockDisplay: clockDisplays){
								Integer categoryId = Integer.valueOf(clockDisplay.getCategoryId());
								if(map.containsKey(categoryId)){
									tempClockDisplays = map.get(categoryId);
								}else{
									tempClockDisplays = new ArrayList<ClockDisplay>();
								}
								tempClockDisplays.add(clockDisplay);
								map.put(categoryId, tempClockDisplays);
							}
							
							for(Map<String, Object> caMap: caList){
								ClockDisplayGroup group = new ClockDisplayGroup();
								group.setName(StringUtil.changeNotNull(caMap.get("text")));
								group.setClockDisplays(map.get(Integer.valueOf(StringUtil.changeObjectToInt(caMap.get("id")))));
								groups.add(group);
							}
						}
						clockDisplayGroups = new ClockDisplayGroups();
						clockDisplayGroups.setClockDisplayGroups(groups);
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(clockDisplayGroups));
						redisUtil.expire(key, 60 * 60 * 2);//缓存两个小时
//						systemCache.addCache(key, clockDisplayGroups);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
//		}else{
//			clockDisplayGroups = (ClockDisplayGroups)obj;
//		}
		
		if(clockDisplayGroups != null ){
			return clockDisplayGroups.getClockDisplayGroups();
		}
		return new ArrayList<ClockDisplayGroup>();
	}
	
	/**
	 * 删除用户的所有进行中的任务列表的cache和redis缓存
	 * @return
	 */
	public boolean deleteDateClocksCache(int userId){
		String key = getClocksKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 删除系统任务列表的cache和redis缓存
	 * @return
	 */
	public boolean deleteSystemClocksCache(){
		String key = getSystemClocksKey();
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}

	
	/**
	 * 获取该用户所有进行中的任务列表在redis的key
	 * @return
	 */
	public static String getClocksKey(int userId){
		return ConstantsUtil.CLOCK_ONGOING_REDIS + userId;
	}
	
	/**
	 * 获取系统任务列表在redis的key
	 * @return
	 */
	public static String getSystemClocksKey(){
		return ConstantsUtil.CLOCK_SYSTEMS;
	}
	
}
