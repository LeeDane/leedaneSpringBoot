package com.cn.leedane.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.SqlBaseService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;

/**
 * 转发的处理类
 * @author LeeDane
 * 2016年3月19日 下午11:19:24
 * Version 1.0
 */
@Component
public class TransmitHandler {
	
	@Autowired
	private SqlBaseService<IDBean> sqlBaseService;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();

	public int getTransmitNumber(int tableId, String tableName){
		String transmitKey = getTransmitKey(tableId, tableName);
		int transmitNumber;
		//转发
		if(!redisUtil.hasKey(transmitKey)){
			transmitNumber = sqlBaseService.getTotal(DataTableType.转发.value, "where table_id = "+tableId+" and table_name='"+tableName+"'");
			redisUtil.addString(transmitKey, String.valueOf(transmitNumber));
		}else{
			transmitNumber = Integer.parseInt(redisUtil.getString(transmitKey));
		}
		return transmitNumber;
	}
	
	/**
	 * 删除转发后修改转发数量
	 * @param tableId
	 * @param tableName
	 */
	public void deleteTransmit(int tableId, String tableName){
		String transmitKey = getTransmitKey(tableId, tableName);
		int transmitNumber;
		//转发
		if(!redisUtil.hasKey(transmitKey)){
			transmitNumber = sqlBaseService.getTotal(DataTableType.转发.value, "where table_id = "+tableId+" and table_name='"+tableName+"'");
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
	public int getTransmits(int userId){
		return this.sqlBaseService.getTotalByUser(DataTableType.转发.value, userId);
	}
	
	/**
	 * 获取转发在redis的key
	 * @param id
	 * @return
	 */
	public static String getTransmitKey(int tableId, String tableName){
		return ConstantsUtil.TRANSMIT_REDIS +tableName+"_"+tableId;
	}
}
