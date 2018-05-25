package com.cn.leedane.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 集合工具类
 * @author LeeDane
 * 2016年11月2日 下午3:10:05
 * Version 1.0
 */
public class CollectionUtil {

	/**
	 * 判断List集合是否为空
	 * @param list
	 * @return
	 */
	public static boolean isEmpty(List<?> list){
		return list == null || list .size() == 0;
	}
	
	/**
	 * 判断List集合是否不为空
	 * @param list
	 * @return
	 */
	public static boolean isNotEmpty(List<?> list){
		return !isEmpty(list);
	}
	
	/** 
     * 将list中的集合转换成Map中的key，value为数量默认为1 
     * @param list 
     * @return 
     */  
    private static Map<String, Integer> list2Map(List<String> list){  
        Map<String, Integer> map=new HashMap<String, Integer>();  
        for(String key:list){                      //循环获得的List集合  
            if (list.contains(key)) {              //判断这个集合中是否存在该字符串  
                map.put(key, map.get(key) == null ? 1 : map.get(key)+1);  
            }                                       //将集中获得的字符串放在map的key键上  
        }                                          //并计算其value是否有值，如有则+1操作  
        return map;  
    } 
	
    /**
     * 获取前面几个关键字
     * @param keyWordsList
     * @param keyLength
     * @param keyNumber
     * @return
     */
	public static List<String> getTopKeyword(List<String> keyWordsList, int keyLength, int keyNumber){
        Map<String, Integer> map=list2Map(keyWordsList);//list转map并计次数  
        //使用Collections的比较方法进行对map中value的排序  
        ArrayList<Entry<String, Integer>> list = new ArrayList<Entry<String,Integer>>(map.entrySet());  
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {  
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {  
                return (o2.getValue() - o1.getValue());  
            }  
        });  
        if (list.size() < keyNumber) 
        	keyNumber = list.size();//排序后的长度，以免获得到null的字符
        List<String> keyWords = new ArrayList<String>(); //设置将要输出的关键字数组空间
        Entry<String, Integer> entry;
        int count = 0;
        for(int i = 0; i < list.size(); i++) {//循环排序后的数组 
        	entry = list.get(i);
            if (keyLength <= entry.getKey().length() && count < keyNumber) {//判断个数  
            	keyWords.add(entry.getKey());//设置关键字进入数组  
            	count ++;
            }  
        }  
        return keyWords;  
	}
}
