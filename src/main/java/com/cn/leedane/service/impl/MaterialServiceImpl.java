package com.cn.leedane.service.impl;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.mapper.MaterialMapper;
import com.cn.leedane.model.MaterialBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.MaterialService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.Base64ImageUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 素材service实现类
 * @author LeeDane
 * 2017年5月22日 上午10:11:51
 * version 1.0
 */
@Service("materialService")
public class MaterialServiceImpl extends AdminRoleCheckService implements MaterialService<MaterialBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private MaterialMapper materialMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public boolean isExist(UserBean user, String path) {
		return SqlUtil.getBooleanByList(materialMapper.isExist(user.getId(), path));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> save(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("MaterialServiceImpl-->save():JSONObject="+jo.toString());
		
		ResponseMap message = new ResponseMap();
		
		//获取数组列表
		JSONArray array = jo.getJSONArray("materials");
		List<Map<String, Object>> data = (List<Map<String, Object>>)JSONArray.toList(array, Map.class);
		if(CollectionUtil.isNotEmpty(data)){
			for(int i = 0; i < data.size(); i++){
				//获取文件的路径
				String path = StringUtil.changeNotNull(data.get(i).get("path"));
				if(Base64ImageUtil.isSupportType(path)){
					data.get(i).put("material_type", "图像");
				}else{
					data.get(i).put("material_type", "文件");
				}
				data.get(i).put("create_user_id", user.getId());
				data.get(i).put("create_time", new Timestamp(new Date().getTime()));
				data.get(i).put("width", 0);
				data.get(i).put("height", 0);
				data.get(i).put("status", ConstantsUtil.STATUS_NORMAL);
			}
			materialMapper.insertByBatch(data);
			message.put("isSuccess", true);
			message.put("message", "添加素材成功");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数信息不符合规范.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数信息不符合规范.value);
		}
		
		//保存操作日志
		String subject = user.getAccount() + "操作批量加入素材";
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "save", 1 , 0);
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> delete(int materialId, UserBean user,
			HttpServletRequest request) {
		logger.info("MaterialServiceImpl-->delete():materialId="+materialId);
		
		ResponseMap message = new ResponseMap();
		MaterialBean materialBean = materialMapper.findById(MaterialBean.class, materialId);
		
		//检验是否是管理员或者创建者权限
		checkAdmin(user, materialBean.getCreateUserId());
		
		boolean result = materialMapper.deleteById(MaterialBean.class, materialId) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库删除数据失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库删除数据失败.value);
		}
		
		//保存操作日志
		String subject = user.getAccount() + "删除素材ID为："+materialId+"的数据"+StringUtil.getSuccessOrNoStr(result);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "delete()", StringUtil.changeBooleanToInt(result) , 0);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getMaterialByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request) {	
		logger.info("MaterialServiceImpl-->getMaterialByLimit():JSONObject="+jo.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(jo, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jo, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(jo, "total", 0); //总数
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		String type = JsonUtil.getStringValue(jo, "type", "图像"); //类型，是图像还是文件
		List<Map<String, Object>> rs = materialMapper.getMaterialByLimit(user.getId(), start, pageSize, type, ConstantsUtil.STATUS_NORMAL);
		message.put("total", SqlUtil.getTotalByList(materialMapper.executeSQL("select count(*) ct from "+DataTableType.素材.value+" where material_type=? and status = ? and create_user_id = ?", type, ConstantsUtil.STATUS_NORMAL, user.getId())));
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取素材列表").toString(), "getMaterialByLimit()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}
}
