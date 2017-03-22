package com.cn.leedane.utils;

import com.cn.leedane.mapper.FinancialMapper;
import com.cn.leedane.springboot.SpringUtil;

/**
 * 服务器web页面请求数据默认imei数字自增长控制
 * @author LeeDane
 * 2016年12月12日 上午11:48:23
 * Version 1.0
 */
public class FinancialWebImeiUtil {

	public static final String DEFAULT_IMEI = "201601010156651";
	
	private static FinancialWebImeiUtil imeiUtil;
	
	private static int imei_local_id = 1;
	
	private FinancialWebImeiUtil(){
		FinancialMapper financialMapper = (FinancialMapper) SpringUtil.getBean("financialMapper");
		imei_local_id = 1 + SqlUtil.getTotalByList(financialMapper.executeSQL("select local_id ct from t_financial where imei=?", DEFAULT_IMEI));
		System.out.println("加载imei_local_id完成，初始化值为："+ imei_local_id);
	}
	
	public static synchronized FinancialWebImeiUtil getInstance(){
		if(imeiUtil == null){
			synchronized (FinancialWebImeiUtil.class) {
				if(imeiUtil == null)
					imeiUtil = new FinancialWebImeiUtil();
			}
		}
		return imeiUtil;
	}
	
	public synchronized int getLocalId(){
		return imei_local_id;
	}
	
	public synchronized boolean addLocalId(){
		imei_local_id = getLocalId() + 1;
		return true;
	}
}
