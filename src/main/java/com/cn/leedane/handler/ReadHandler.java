package com.cn.leedane.handler;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.ReadMapper;
import com.cn.leedane.model.EventAllBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 阅读的处理类
 * @author LeeDane
 * 2016年3月19日 下午11:19:24
 * Version 1.0
 */
@Component
public class ReadHandler {
	
	@Autowired
	private ReadMapper readMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();

	@Autowired
	private SystemCache systemCache;

	/**
	 * 获取阅读总数
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public int getReadNumber(String tableName, long tableId){
		String readKey = getReadKey(tableName, tableId);
		int readNumber;
		Object obj = systemCache.getCache(readKey);
		if(obj == ""){
			//阅读
			if(!redisUtil.hasKey(readKey)){
				readNumber = SqlUtil.getTotalByList(readMapper.getTotal(DataTableType.阅读.value, "where table_id = "+tableId+" and table_name='"+tableName+"' and status="+ ConstantsUtil.STATUS_NORMAL));
				redisUtil.addString(readKey, String.valueOf(readNumber));
				systemCache.addCache(readKey, String.valueOf(readNumber));
			}else{
				readNumber = Integer.parseInt(redisUtil.getString(readKey));
				systemCache.addCache(readKey, String.valueOf(readNumber));
			}
		}else{
			readNumber = StringUtil.changeObjectToInt(obj) ;
		}
		return readNumber < 1 ? 1: readNumber + 1;
	}

	/**
	 * 删除缓存
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public boolean delete(String tableName, long tableId){
		String readKey = getReadKey(tableName, tableId);
		systemCache.removeCache(readKey);
		return redisUtil.delete(readKey);
	}
	/**
	 * 获取阅读在redis的key
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public static String getReadKey(String tableName, long tableId){
		return ConstantsUtil.READ_REDIS +tableName+"_"+tableId;
	}
}
