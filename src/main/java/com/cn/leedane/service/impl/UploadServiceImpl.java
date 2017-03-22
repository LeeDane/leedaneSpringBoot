package com.cn.leedane.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.mapper.UploadMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UploadBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UploadService;
/**
 * 断点续传service的实现类
 * @author LeeDane
 * 2016年7月12日 下午2:15:55
 * Version 1.0
 */
@Service("uploadService")
public class UploadServiceImpl implements UploadService<UploadBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private UploadMapper uploadMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public boolean addUpload(UploadBean upload, UserBean user, HttpServletRequest request) throws Exception {
		logger.info("UploadServiceImpl-->addUpload():upload=" +upload.toString() +", user=" +user.getAccount());
		
		if(SqlUtil.getBooleanByList(uploadMapper.hasUpload(upload.getTableName(), upload.getTableUuid(), upload.getfOrder(), upload.getSerialNumber(), user.getId()))){
			return true;
		}
		return uploadMapper.save(upload) > 0;
	}

	@Override
	public boolean cancel(UploadBean upload, UserBean user, HttpServletRequest request) {
		logger.info("UploadServiceImpl-->cancel():upload=" +upload.toString() +", user=" +user.getAccount());
		try {
			boolean result = uploadMapper.deleteSql(EnumUtil.getBeanClass(EnumUtil.getTableCNName(DataTableType.上传.value)), " where table_uuid = ? and table_name = ? and f_order = ? and create_user_id = ? and serial_number= ? ", upload.getTableUuid(), upload.getTableName(), upload.getfOrder(), user.getId(), upload.getSerialNumber()) > 0;
				if(result){
					File file = new File(upload.getPath());
					if(file.exists()){
						file.delete();
						return true;
					}
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Map<String, Object>> getOneUpload(String tableUuid, String tableName, int order, UserBean user,
			HttpServletRequest request) {
		logger.info("UploadServiceImpl-->getOneUpload():tableUuid=" +tableUuid + ",tableName="+tableName+",order="+ order+", user=" +user.getAccount());
			
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		
		sql.append("select l.id, l.path, l.serial_number");
		sql.append(" from "+DataTableType.上传.value+" l inner join "+DataTableType.用户.value+" u on u.id = l.create_user_id where l.create_user_id = ? and l.status=? ");
		sql.append(" and l.table_name=? and l.table_uuid=? and l.f_order=? ");
		sql.append(" order by l.serial_number");
		rs = uploadMapper.executeSQL(sql.toString(), user.getId(), ConstantsUtil.STATUS_NORMAL, tableName, tableUuid, order);
		return rs;
	}
	
}
