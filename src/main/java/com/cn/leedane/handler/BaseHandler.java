package com.cn.leedane.handler;

import java.io.IOException;

/**
 * 基础处理类
 */
public interface BaseHandler {
	/**
	 * 添加
	 * @param key
	 * @param value
	 * @return
	 */
	boolean addCache(String key, Object value) throws IOException;

	/**
	 * 获取
	 * @param params
	 * @return
	 */
	Object get(Object ... params);

	/**
	 * 删除
	 * @param params
	 * @return
	 */
	boolean delete(Object ... params);

	/**
	 * 获取key
	 * @param params
	 * @return
	 */
	String getKey(Object ... params);
}
