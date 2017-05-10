package com.cn.leedane.service.impl;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.mapper.FilePathMapper;
import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AppVersionService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

/**
 * App版本service实现类
 * @author LeeDane
 * 2016年7月12日 下午12:20:33
 * Version 1.0
 */
@Service("appVersionService")
public class AppVersionServiceImpl implements AppVersionService<FilePathBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private FilePathMapper filePathMapper;
	
	@Resource
	private OperateLogService<OperateLogBean> operateLogService;
	
	public void setOperateLogService(
			OperateLogService<OperateLogBean> operateLogService) {
		this.operateLogService = operateLogService;
	}
	
	@Override
	public Map<String, Object> getNewest(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("AppVersionServiceImpl-->getNewest():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		String versionName = JsonUtil.getStringValue(jo, "versionName");
		//String versionCode = JsonUtil.getStringValue(jo, "versionCode");
		
		List<Map<String, Object>>  list = new ArrayList<Map<String,Object>>();
		if(StringUtil.isNull(versionName)){
			//直接返回最新的记录
			list = getNewestVersion();
		}else{
			//该版本已经存
			int oldFileId = check(versionName);
			if(oldFileId > 0){
				StringBuffer sql = new StringBuffer();
				sql.append("select f.id, f.file_desc, f.file_version, f.qiniu_path path, lenght");
				sql.append(" from "+DataTableType.文件.value+" f");
				sql.append(" where f.status=? and f.id >? and f.is_upload_qiniu = ? and f.table_name=? order by f.id desc limit 1");
				list = filePathMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, oldFileId, true, ConstantsUtil.UPLOAD_APP_VERSION_TABLE_NAME);
			}else{
				//直接返回最新的记录
				list = getNewestVersion();
			}
		}
		
		
		if(list != null && list.size() ==1){
			message.put("isSuccess", true);
			message.put("message", list);
		}
		return message.getMap();
	}

	/**
	 * 检查数据库是否存在该版本
	 * @param versionName
	 * @return
	 */
	private int check(String versionName){
		String sql = "select id from "+DataTableType.文件.value+" where status = ? and table_name=? and  file_version = ? and is_upload_qiniu = ? limit 1";
		List<Map<String, Object>> list = filePathMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, ConstantsUtil.UPLOAD_APP_VERSION_TABLE_NAME, versionName, true );
		return list != null && list.size() == 1? StringUtil.changeObjectToInt(list.get(0).get("id")) : 0;
	}

	/**
	 * 获取数据库中上传的最新版本
	 * @return
	 */
	public List<Map<String, Object>> getNewestVersion(){
		List<Map<String, Object>>  list = new ArrayList<Map<String,Object>>();
		StringBuffer sql = new StringBuffer();
		sql.append("select f.id, f.file_desc, f.file_version, f.qiniu_path path, f.lenght");
		sql.append(" from "+DataTableType.文件.value+" f");
		sql.append(" where f.status=? and f.is_upload_qiniu = ? and f.table_name=? order by f.id desc limit 1");
		list = filePathMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, true, ConstantsUtil.UPLOAD_APP_VERSION_TABLE_NAME);
		return list;
	}

	@Override
	public Map<String, Object> paging(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("AppVersionServiceImpl-->paging():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		StringBuffer sql = new StringBuffer();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		ResponseMap message = new ResponseMap();
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select f.id, f.file_desc, f.file_version, f.qiniu_path path, f.lenght, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.文件.value+" f");
			sql.append(" where status=? and f.is_upload_qiniu = ? and f.table_name=?");
			sql.append(" order by f.id desc limit 0,?");
			rs = filePathMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, true, ConstantsUtil.UPLOAD_APP_VERSION_TABLE_NAME, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select f.id, f.file_desc, f.file_version, f.qiniu_path path, f.lenght, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.文件.value+" f");
			sql.append(" where status=? and f.is_upload_qiniu = ? and f.table_name=?");
			sql.append(" and f.id < ? order by f.id desc limit 0,? ");
			rs = filePathMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, true, ConstantsUtil.UPLOAD_APP_VERSION_TABLE_NAME, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select f.id, f.file_desc, f.file_version, f.qiniu_path path, f.lenght, date_format(f.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" from "+DataTableType.文件.value+" f");
			sql.append(" where status=? and f.is_upload_qiniu = ? and f.table_name=?");
			sql.append(" and f.id > ? limit 0,?  ");
			rs = filePathMapper.executeSQL(sql.toString() , ConstantsUtil.STATUS_NORMAL, true, ConstantsUtil.UPLOAD_APP_VERSION_TABLE_NAME, firstId, pageSize);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取app版本列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
				
		message.put("isSuccess", true);
		message.put("message", rs);
		logger.info("获得记账位置的数量：" +rs.size());
		return message.getMap();
	}
	
}
