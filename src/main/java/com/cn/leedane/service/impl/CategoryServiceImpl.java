package com.cn.leedane.service.impl;

import com.cn.leedane.handler.CategoryHandler;
import com.cn.leedane.mapper.CategoryMapper;
import com.cn.leedane.model.CategoryBean;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.CategoryService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
/**
 * 分类管理service的实现类
 * @author LeeDane
 * 2017年6月28日 下午4:52:39
 * version 1.0
 */
@Service("categoryServiceImpl")
public class CategoryServiceImpl implements CategoryService<CategoryBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CategoryMapper categoryMapper;
	
	@Autowired
	private CategoryHandler categoryHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public ResponseModel add(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("CategoryServiceImpl-->add():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		String text = JsonUtil.getStringValue(jo, "text");
		long pid = JsonUtil.getLongValue(jo, "pid", 0);
		ParameterUnspecificationUtil.checkNullString(text, "text must not null.");
		CategoryBean bean = new CategoryBean();
		bean.setCreateTime(new Date());
		bean.setCreateUserId(user.getId());
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		bean.setText(text);
		bean.setPid(pid);
		if(pid < 1)
			bean.setSystem(true);
		boolean result = categoryMapper.save(bean) > 0;
		ResponseModel responseModel = new ResponseModel();
		if(result)
			responseModel.ok().message(bean);
		else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value)).code(EnumUtil.ResponseCode.数据库保存失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"添加节点， 节点名称是", text,", 父节点id是：", pid).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}
	
	@Override
	public ResponseModel children(boolean isAdmin, long pid, UserBean user, HttpRequestInfoBean request){
		logger.info("CategoryServiceImpl-->children():pid=" +pid +", user=" +user.getAccount());
		pid = pid < 1 ? 0: pid;
		List<Map<String, Object>> rs = categoryMapper.children(pid, user.getId());
		if(CollectionUtil.isNotEmpty(rs)){
			for(Map<String, Object> mp: rs){
				/*if(!isAdmin){
					boolean isSystem = StringUtil.changeObjectToBoolean(mp.get("is_system"));
					if(isSystem)
						mp.put("selectable", false);
				}*/
				
				//获取其自己点
				int nodes = StringUtil.changeObjectToInt(mp.get("nodes"));
				if(nodes > 0){
					mp.put("nodes", new ArrayList<Map<String, Object>>());
				}else{
					mp.remove("nodes");
				}
			}
		}
		return new ResponseModel().ok().message(rs);
	}
	
	@Override
	public ResponseModel mallCategory(int pid){
		logger.info("CategoryServiceImpl-->mallCategory():pid=" +pid);
		pid = pid < 1 ? 0: pid;
		List<Map<String, Object>> rs = categoryMapper.getParentCategorys(pid);
		List<String[]> relations = new ArrayList<String[]>();
		if(CollectionUtil.isNotEmpty(rs)){
			String categorys = StringUtil.changeNotNull(rs.get(0).get("categorys"));
			if(StringUtil.isNotNull(categorys)){
				String[] cates = categorys.split(",");
				//倒叙摆放为了展示方便
				for(int i = cates.length - 1; i >= 0; i--){
					int ct = Integer.parseInt(cates[i]);
					CategoryBean categoryBean = categoryHandler.getNormalCategoryBean(ct);
					String[] relation = new String[3];
					relation[0] = ct + "";
					relation[1] = categoryBean != null? categoryBean.getText(): "";
					relation[2] = categoryBean != null? "/mall/category/"+ ct: "";
					relations.add(relation);
				}
			}
		}
		return new ResponseModel().ok().message(relations);
	}
	
	@Override
	public ResponseModel update(boolean isAdmin, long cid, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("CategoryServiceImpl-->delete():cid=" +cid + ", json="+ json +", user=" +user.getAccount());
		String text = JsonUtil.getStringValue(json, "text");
		ParameterUnspecificationUtil.checkNullString(text, "text must not null.");
		if(!isAdmin){
			//判断是否是属于自己的分类
			if(!SqlUtil.getBooleanByList(categoryMapper.canDelete(cid, user.getId())))
				throw new UnauthorizedException();
		}
		
		boolean result = categoryMapper.updateSql(CategoryBean.class, " set text = ? where id = ?", text, cid) > 0;
		ResponseModel responseModel = new ResponseModel();
		if(result)
			responseModel.ok().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改成功.value));
		else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改失败.value)).code(EnumUtil.ResponseCode.修改失败.value);

		return responseModel;
	}
	
	@Override
	public ResponseModel delete(boolean isAdmin, long cid, UserBean user, HttpRequestInfoBean request) {
		logger.info("CategoryServiceImpl-->delete():cid=" +cid +", user=" +user.getAccount());
		if(!isAdmin){
			//判断是否是属于自己的分类
			if(!SqlUtil.getBooleanByList(categoryMapper.canDelete(cid, user.getId())))
				throw new UnauthorizedException();
		}
		
		boolean result = categoryMapper.deleteById(CategoryBean.class, cid) > 0;
		ResponseModel responseModel = new ResponseModel();
		if(result)
			responseModel.ok().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
		else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value)).code(EnumUtil.ResponseCode.删除失败.value);
		return responseModel;
	}
}
