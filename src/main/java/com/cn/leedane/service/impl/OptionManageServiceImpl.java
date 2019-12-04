package com.cn.leedane.service.impl;

import com.cn.leedane.exception.OperateException;
import com.cn.leedane.handler.InitCacheData;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.OptionManageMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.OptionManageService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 选项的管理(增删改查)
 * @author LeeDane
 * 2019年12月2日 下午18:32:28
 * version 1.0
 */
@Service("optionManageService")
public class OptionManageServiceImpl implements OptionManageService<OptionBean> {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private OptionManageMapper optionManageMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private InitCacheData initCacheData;

	@Override
    public Map<String, Object> add(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("OptionManageServiceImpl-->add():jo="+jo.toString());
		ResponseMap message = new ResponseMap();
		SqlUtil sqlUtil = new SqlUtil();
		OptionBean optionBean = (OptionBean) sqlUtil.getBean(jo, OptionBean.class);
		if(optionBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.资源不存在.value));

		ParameterUnspecificationUtil.checkNullString(optionBean.getOptionKey(), "option-key must not null.");
		ParameterUnspecificationUtil.checkNullString(optionBean.getOptionValue(), "option-value must not null.");

		//校验是否存在
		if(SqlUtil.getTotalByList(optionManageMapper.isExist(optionBean.getOptionKey(), optionBean.getVersion())) > 0){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.添加的记录已经存在.value));
			message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
			return message.getMap();
		}
		optionBean.setCreateUserId(user.getId());
		optionBean.setCreateTime(new Date());
        boolean result = optionManageMapper.save(optionBean) > 0;
		
		if(!result){
			throw new OperateException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
		}

		//重新更新缓存
		initCacheData.loadOptionTable();

        message.put("isSuccess", true);
		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
    }
    
    @Override
    public Map<String, Object> update(JSONObject jo, UserBean user, HttpRequestInfoBean request){
    	logger.info("OptionManageServiceImpl-->update():jo="+jo.toString());
		ResponseMap message = new ResponseMap();

		long optionId = JsonUtil.getLongValue(jo, "id", 0);
		OptionBean oldOptionBean = null;
		if(optionId < 1 || (oldOptionBean = optionManageMapper.findById(OptionBean.class, optionId)) == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
		
    	SqlUtil sqlUtil = new SqlUtil();
		OptionBean optionBean = (OptionBean) sqlUtil.getUpdateBean(jo, oldOptionBean);
		if(optionBean == null){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.根据请求参数构建实体对象失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.根据请求参数构建实体对象失败.value);
			return message.getMap();
		}

		ParameterUnspecificationUtil.checkNullString(optionBean.getOptionKey(), "option-key must not null.");
		ParameterUnspecificationUtil.checkNullString(optionBean.getOptionValue(), "option-value must not null.");

		optionBean.setModifyUserId(user.getId());
		optionBean.setModifyTime(new Date());
        boolean result = optionManageMapper.update(optionBean) > 0;
        if(result){
			//重新更新缓存
			initCacheData.loadOptionTable();

        	message.put("isSuccess", true);
    		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改成功.value));
    		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
        }else{
    		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改失败.value));
    		message.put("responseCode", EnumUtil.ResponseCode.修改失败.value);
        }
		return message.getMap();
    }
    
    @Override
    public Map<String, Object> delete(long optionId, HttpRequestInfoBean request){
    	logger.info("OptionManageServiceImpl-->delete():optionId="+optionId);
		ResponseMap message = new ResponseMap();
		
		OptionBean optionBean = null;
		if(optionId < 1 || (optionBean = optionManageMapper.findById(OptionBean.class, optionId)) == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
        
        boolean result = optionManageMapper.deleteById(OptionBean.class, optionId) > 0;
        if(result){
			//重新更新缓存
			initCacheData.loadOptionTable();

        	message.put("isSuccess", true);
    		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
    		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
        }else{
    		message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
    		message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
        }
		return message.getMap();
    }
    
    @Override
	public Map<String, Object> paging(JSONObject jsonObject, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("OptionManageServiceImpl-->paging():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(jsonObject, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jsonObject, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(jsonObject, "total", 0); //总数
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<Map<String, Object>> rs = optionManageMapper.paging(start, pageSize, ConstantsUtil.STATUS_NORMAL);

		if(CollectionUtil.isNotEmpty(rs)){
			int createUserId = 0;			
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
				rs.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
			}
		}
		message.put("total", SqlUtil.getTotalByList(optionManageMapper.getTotal(DataTableType.选项配置.value, null)));
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取option列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		return message.getMap();
	}

	@Override
	public Map<String, Object> deletes(String optionIds, UserBean user,
			HttpRequestInfoBean request){
		logger.info("OptionManageServiceImpl-->deletes():optionIds="+ optionIds);
		ResponseMap message = new ResponseMap();
		ParameterUnspecificationUtil.checkNullString(optionIds, "optionIds must not null.");
		String[] pmidArray = optionIds.split(",");
		
		boolean result = false;
		for(int i = 0; i < pmidArray.length; i++){
			
			OptionBean optionBean = optionManageMapper.findById(OptionBean.class, StringUtil.changeObjectToInt(pmidArray[i]));
			if(optionBean == null)
				throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.资源不存在.value));
			
			result = optionManageMapper.deleteById(OptionBean.class, optionBean.getId()) > 0;
		}
		
		if(result){
			//保存操作日志
			operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"批量删除选项,optionIds="+ optionIds).toString(), "deletes()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);

			//重新更新缓存
			initCacheData.loadOptionTable();

			message.put("isSuccess", result);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			throw new Error();
		}
		return message.getMap();
	}
}
