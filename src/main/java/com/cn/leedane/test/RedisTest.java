package com.cn.leedane.test;

import org.junit.Test;

import com.cn.leedane.redis.util.RedisUtil;

/**
 * Redis相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:28:51
 * Version 1.0
 */
public class RedisTest extends BaseTest {
	
	@Test
	public void addSet() throws Exception{
		logger.info(RedisUtil.getInstance().addString("mood_1", "hello mood 1"));
		logger.info(RedisUtil.getInstance().getString("mood_1"));
		RedisUtil.getInstance().clearAll();
	}
	
	@Test
	public void String() throws Exception{
		RedisUtil redisUtil = RedisUtil.getInstance();
		//redisUtil.addString("comment_t_mood_1104", "12");
		/*if(redisUtil.hasKey("mood_2")){
			logger.info("已经存在key：mood_2");
		}else{
			logger.info("还没有存在key：mood_2");
		}*/
		/*redisUtil.addString("comment_t_mood_1103", "0");
		redisUtil.addString("comment_t_mood_1104", "0");
		redisUtil.addString("comment_t_mood_1099", "0");*/
		/*logger.info(redisUtil.addString("mood_2", "hello mood 3"));
		logger.info(redisUtil.getString("mood_2"));
		
		if(redisUtil.hasKey("mood_2")){
			logger.info("已经存在key：mood_2");
		}else{
			logger.info("还没有存在key：mood_2");
		}*/
		//redisUtil.delete("mood_img_t_mood_leedane4fa288ac-6664-469e-a1ce-a1070fce82de_30x30");
		/*for(int i = 1080; i < 1200; i++){
			redisUtil.delete(ConstantsUtil.COMMENT_REDIS +"t_mood_" +i);
			redisUtil.delete(ConstantsUtil.TRANSMIT_REDIS +"t_mood_" +i);
			redisUtil.delete(ConstantsUtil.ZAN_REDIS +"t_mood_" +i);
			redisUtil.delete(ConstantsUtil.ZAN_USER_REDIS +"t_mood_" +i);
			//redisUtil.delete(ConstantsUtil.MOOD_REDIS +i);
		}*/
		//logger.info(redisUtil.getString("comment_t_mood_1102"));
		//redisUtil.delete("mood_t_mood_Andy90f776d9-be7e-4c79-8ea6-eed50b2b29e3");
		//Map<String>
		//redisUtil.addMap("test_user_1", map)
		/*String key = "test_zdd_1";*/
		/*redisUtil.delete("setAAA");
		Map<Double, String> scoreMembers = new HashMap<Double, String>();
		scoreMembers.put((double) 1, "leedane1");
		scoreMembers.put((double) 2, "leedane2");
		scoreMembers.put((double) 3, "leedane3");
		scoreMembers.put((double) 4, "leedane4");
		scoreMembers.put((double) 5, "leedane5");
		scoreMembers.put((double) 6, "leedane16");
		scoreMembers.put((double) 7, "leedane7");
		scoreMembers.put((double) 8, "leedane8");
		scoreMembers.put((double) 9, "leedane9");
		scoreMembers.put((double) 10, "leedane10");
		scoreMembers.put((double) 11, "leedane11");
		redisUtil.zadd("setAAA", scoreMembers);
		
		Set<String> set = redisUtil.getLimit(1, "setAAA", 2, 5);
		for(String s: set){
			logger.info(s);
		}*/
		redisUtil.clearAll();
	}
}
