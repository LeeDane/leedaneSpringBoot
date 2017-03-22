package com.cn.leedane.utils;

import java.util.Random;

/**
 * 随机数工具类
 * @author LeeDane
 * 2016年7月12日 上午10:31:02
 * Version 1.0
 */
public class RandomUtil {
	
	/**
	 * 获得指定返回的随机整数(包括左右)
	 * @param start
	 * @param end
	 * @return
	 */
	public static int getRandomByRange(int start, int end){
		Random random = new Random();
		return random.nextInt(end - start +1) + start;
	}
	
	/**
	 * 获得随机数，包括0的整数,也包括end数
	 * @param end
	 * @return
	 */
	public static int getRandom(int end){
		return getRandomByRange(0, end);
	}

	public static void main(String[] args) {
		for(int i = 0; i < 100; i++)
			System.out.println(RandomUtil.getRandomByRange(0, 5));
	}
}
