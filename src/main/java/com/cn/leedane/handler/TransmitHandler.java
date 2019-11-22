package com.cn.leedane.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.TransmitMapper;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.SqlUtil;

/**
 * 转发的处理类
 * @author LeeDane
 * 2016年3月19日 下午11:19:24
 * Version 1.0
 */
@Component
public class TransmitHandler {
	
	@Autowired
	private TransmitMapper transmitMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();

	/**
	 * 添加转发
	 * @param tableName
	 * @param tableId
	 */
	public void addTransmit(String tableName, long tableId){
		String key = TransmitHandler.getTransmitKey(tableName, tableId);
		int count = 0;
		//还没有添加到redis中
		if(!redisUtil.hasKey(key)){
			//获取数据库中所有转发的数量
			count = SqlUtil.getTotalByList(transmitMapper.getTotal(DataTableType.转发.value, "where table_id = "+tableId+" and table_name='"+tableName+"' and status="+ ConstantsUtil.STATUS_NORMAL)) + 1;
		}else{
			count = Integer.parseInt(redisUtil.getString(key)) + 1;
		}
		redisUtil.addString(key, String.valueOf(count));
	}
	/**
	 * 获取转发总数
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public int getTransmitNumber(String tableName, long tableId){
		String transmitKey = getTransmitKey(tableName, tableId);
		int transmitNumber;
		//转发
		if(!redisUtil.hasKey(transmitKey)){
			transmitNumber = SqlUtil.getTotalByList(transmitMapper.getTotal(DataTableType.转发.value, "where table_id = "+tableId+" and table_name='"+tableName+"' and status="+ ConstantsUtil.STATUS_NORMAL));
			redisUtil.addString(transmitKey, String.valueOf(transmitNumber));
		}else{
			transmitNumber = Integer.parseInt(redisUtil.getString(transmitKey));
		}
		return transmitNumber < 1 ? 0 : transmitNumber;
	}
	
	/**
	 * 删除转发后修改转发数量
	 * @param tableName
	 * @param tableId
	 */
	public void deleteTransmit(String tableName, long tableId){
		String transmitKey = getTransmitKey(tableName, tableId);
		int transmitNumber;
		//转发
		if(!redisUtil.hasKey(transmitKey)){
			transmitNumber = SqlUtil.getTotalByList(transmitMapper.getTotal(DataTableType.转发.value, "where table_id = "+tableId+" and table_name='"+tableName+"' and status="+ ConstantsUtil.STATUS_NORMAL));
			redisUtil.addString(transmitKey, String.valueOf(transmitNumber));
		}else{
			transmitNumber = Integer.parseInt(redisUtil.getString(transmitKey));
			transmitNumber = transmitNumber -1;//转发总数减去1
			redisUtil.addString(transmitKey, String.valueOf(transmitNumber));
		}
	}
	
	/**
	 * 获取该用户的总转发数
	 * @param userId
	 * @return
	 */
	public int getTransmits(long userId){
		return SqlUtil.getTotalByList(transmitMapper.getTotalByUser(DataTableType.转发.value, userId));
	}

	/**
	 * 获取转发在redis的key
	 * @param tableName
	 * @param tableId
	 * @return
	 */
	public static String getTransmitKey(String tableName, long tableId){
		return ConstantsUtil.TRANSMIT_REDIS +tableName+"_"+tableId;
	}
}
