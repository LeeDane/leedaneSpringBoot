package com.cn.leedane.service.impl.manage;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.ManageBlackMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.manage.ManageBlackService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 黑名单管理信息的service的实现类
 * @author LeeDane
 * 2020年4月19日 下午3:52:25
 * version 1.0
 */
@Service("manageBlackService")
public class ManageBlackServiceImpl extends AdminRoleCheckService implements ManageBlackService<ManageBlackBean> {
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private ManageBlackMapper manageBlackMapper;

	@Autowired
	private UserHandler userHandler;

	@Override
	public LayuiTableResponseModel blacks(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageBlackServiceImpl-->blacks():jo="+ jsonObject);
		int current = JsonUtil.getIntValue(jsonObject, "page", 0);
		int rows = JsonUtil.getIntValue(jsonObject, "limit", 10);
		int start = SqlUtil.getPageStart(current -1, rows, 0);
		List<Map<String, Object>> rs = manageBlackMapper.blacks(user.getId(), start, rows);
		if(rs !=null && rs.size() > 0){
			long blackUserId;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				Map<String, Object> black = rs.get(i);
				//在非获取指定表下的评论列表的情况下的前面35个字符
				blackUserId = StringUtil.changeObjectToLong(black.get("user_id"));
				black.put("name", userHandler.getUserName(blackUserId, false));
				black.put("link", "<a href='/my/"+blackUserId+"' target='_blank' style='color: green;'>查看个人中心</a>");
			}
		}
		LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
		responseModel.setData(rs).setCount(SqlUtil.getTotalByList(manageBlackMapper.getTotalByUser(EnumUtil.DataTableType.黑名单.value, user.getId()))).code(0);
		return responseModel;
	}

	@Override
	public ResponseModel add(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageBlackServiceImpl-->add():jo="+ jsonObject);
		long blackUserId = JsonUtil.getLongValue(jsonObject, "userId");
		//校验用户是否存在
		userHandler.getUserBean(blackUserId);

		//不能添加自己为黑名单
		if(user.getId() == blackUserId)
			throw new UnauthorizedException("不能添加自己为黑名单！");

		ManageBlackBean blackBean = new ManageBlackBean();
		blackBean.setUserId(blackUserId);
		blackBean.setAuthorization(JsonUtil.getStringValue(jsonObject, "authorization"));
		blackBean.setCreateUserId(user.getId());
		blackBean.setCreateTime(new Date());
		blackBean.setModifyUserId(user.getId());
		blackBean.setModifyTime(new Date());
		blackBean.setStatus(ConstantsUtil.STATUS_NORMAL);

		boolean result = manageBlackMapper.save(blackBean) > 0;
		operateLogService.saveOperateLog(user, request, new Date(), "添加黑名单，用户ID为："+ blackUserId +", 结果是："+ StringUtil.getSuccessOrNoStr(result), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(result)
			return new ResponseModel().ok().message("添加成功");
		else
			return new ResponseModel().error().message("添加失败").code(EnumUtil.ResponseCode.操作失败.value);
	}

	@Override
	public ResponseModel delete(long blackId, JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageBlackServiceImpl-->delete():blackId = "+ blackId +"&jo="+ jsonObject);
		ManageBlackBean blackBean = manageBlackMapper.findById(ManageBlackBean.class, blackId);
		if(blackBean == null){
			throw new NullPointerException("黑名单记录不存在");
		}
		checkAdmin(user, blackBean.getCreateUserId());

		boolean result = manageBlackMapper.deleteById(ManageBlackBean.class, blackId) > 0;
		operateLogService.saveOperateLog(user, request, new Date(), "移除黑名单，用户ID为："+ blackBean.getUserId() +", 结果是："+ StringUtil.getSuccessOrNoStr(result), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(result)
			return new ResponseModel().ok().message("移除成功");
		else
			return new ResponseModel().error().message("移除失败").code(EnumUtil.ResponseCode.操作失败.value);
	}

	@Override
	public ResponseModel authorization(long blackId, JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageBlackServiceImpl-->authorization():blackId = "+ blackId +"&jo="+ jsonObject);
		ManageBlackBean blackBean = manageBlackMapper.findById(ManageBlackBean.class, blackId);
		if(blackBean == null)
			throw new NullPointerException("黑名单记录不存在");

		checkAdmin(user, blackBean.getCreateUserId());

		blackBean.setAuthorization(JsonUtil.getStringValue(jsonObject, "authorization"));
		blackBean.setModifyTime(new Date());
		blackBean.setModifyUserId(user.getId());

		boolean result = manageBlackMapper.update(blackBean) > 0;
		operateLogService.saveOperateLog(user, request, new Date(), "授权黑名单，授权信息为："+ blackBean.getUserId() +", 结果是："+ StringUtil.getSuccessOrNoStr(result), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(result)
			return new ResponseModel().ok().message("授权成功");
		else
			return new ResponseModel().error().message("授权失败").code(EnumUtil.ResponseCode.操作失败.value);
	}
}
