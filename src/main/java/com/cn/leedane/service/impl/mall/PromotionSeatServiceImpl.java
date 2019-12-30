package com.cn.leedane.service.impl.mall;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.mall.pdd.api.CreateSeatApi;
import com.cn.leedane.mapper.mall.PromotionSeatApplyMapper;
import com.cn.leedane.mapper.mall.PromotionSeatMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_PromotionSeatApplyBean;
import com.cn.leedane.model.mall.S_PromotionSeatBean;
import com.cn.leedane.notice.NoticeException;
import com.cn.leedane.notice.model.Notification;
import com.cn.leedane.notice.send.INoticeFactory;
import com.cn.leedane.notice.send.NoticeFactory;
import com.cn.leedane.notice.send.NotificationNotice;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.PromotionSeatService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 推广位管理的service的实现类
 * @author LeeDane
 * 2019年11月7日 下午4:48:03
 * version 1.0
 */
@Service("promotionSeatService")
public class PromotionSeatServiceImpl extends MallRoleCheckService implements PromotionSeatService<S_PromotionSeatBean> {
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private PromotionSeatMapper promotionSeatMapper;

	@Autowired
	private PromotionSeatApplyMapper promotionSeatApplyMapper;

	@Autowired
	private UserHandler userHandler;

	@Override
	public Map<String, Object> add(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {

		logger.info("PromotionSeatServiceImpl-->add():jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_PromotionSeatBean promotionSeatBean = (S_PromotionSeatBean) sqlUtil.getBean(jo, S_PromotionSeatBean.class);

		//j校验必须的参数
		ParameterUnspecificationUtil.checkPlatform(promotionSeatBean.getPlatform());
		ParameterUnspecificationUtil.checkNullString(promotionSeatBean.getSeatName(), "seat name must not null.");

		ResponseMap message = new ResponseMap();

		//只有商城管理员才能做删除操作
		checkMallAdmin(user);

		String returnMsg = "新增推广位成功！";
		Date createTime = new Date();
		promotionSeatBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		promotionSeatBean.setCreateTime(createTime);
		promotionSeatBean.setCreateUserId(user.getId());
		boolean result = false;
		try{
			result = promotionSeatMapper.save(promotionSeatBean) > 0;
		}catch(DuplicateKeyException e){ //唯一键约束异常不做处理
			returnMsg = "该记录已经存在，请认真核实。";
		}

		message.put("isSuccess", result);
		message.put("message", returnMsg);
		if(result){
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"添加推广位，推广位ID为", promotionSeatBean.getSeatId(), ",推广位唯一编号为：", promotionSeatBean.getId(), "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);

		return message.getMap();
	}

	@Override
	public Map<String, Object> autoCreatePdd(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws PddException {
		logger.info("PromotionSeatServiceImpl-->autoCreatePdd():jo="+jo);
		//只有商城管理员才能做删除操作
		checkMallAdmin(user);
		int number = JsonUtil.getIntValue(jo, "number", 10); //m创建推广位的数量
		int maxId = StringUtil.changeObjectToInt(promotionSeatMapper.getMaxId(EnumUtil.ProductPlatformType.拼多多.value));
		List<String> pIdNameList = new ArrayList<>();
		for(int i = 1; i <= number; i++){
			String value = String.format("%05d", maxId + i);
			pIdNameList.add("tgw"+ value);
		}
		ResponseMap message = new ResponseMap();
		List<S_PromotionSeatBean> promotionSeatBeans = CreateSeatApi.create(pIdNameList);
		if(promotionSeatBeans.size() != pIdNameList.size()){
			message.put("message", "有某些推广位创建失败，本次操作结束。");
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			return message.getMap();
		}

		Date createTime = new Date();
		for(S_PromotionSeatBean promotionSeatBean: promotionSeatBeans){
			promotionSeatBean.setCreateTime(createTime);
			promotionSeatBean.setStatus(ConstantsUtil.STATUS_NORMAL);
			promotionSeatBean.setCreateUserId(user.getId());
			promotionSeatBean.setPlatform(EnumUtil.ProductPlatformType.拼多多.value);
		}

		String returnMsg = "批量新增拼多多推广位成功！";


//		promotionSeatBean.setStatus(ConstantsUtil.STATUS_NORMAL);
//		promotionSeatBean.setCreateTime(createTime);
//		promotionSeatBean.setCreateUserId(user.getId());
		boolean result = false;
		try{
			result = promotionSeatMapper.batchSave(promotionSeatBeans) > 0;
		}catch(DuplicateKeyException e){ //唯一键约束异常不做处理
			returnMsg = "该记录已经存在，请认真核实。";
		}

		message.put("isSuccess", result);
		message.put("message", returnMsg);
		if(result){
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"自动添加拼多多推广位， 结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);

		return message.getMap();
	}

	@Override
	public Map<String, Object> update(long seatId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) throws Exception {

		logger.info("PromotionSeatServiceImpl-->update(): seatId = "+ seatId +",jo="+jo);
		SqlUtil sqlUtil = new SqlUtil();
		S_PromotionSeatBean updatePromotionSeatBean = (S_PromotionSeatBean) sqlUtil.getUpdateBean(jo, S_PromotionSeatBean.class);

		S_PromotionSeatBean promotionSeatBean = promotionSeatMapper.findById(S_PromotionSeatBean.class, seatId);
		if(promotionSeatBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该推广位不存在.value));

		ResponseMap message = new ResponseMap();
		//只有商城管理员才能做删除操作
		checkMallAdmin(user);
		//j校验必须的参数
		//j校验必须的参数
		ParameterUnspecificationUtil.checkPlatform(updatePromotionSeatBean.getPlatform());
		ParameterUnspecificationUtil.checkNullString(updatePromotionSeatBean.getSeatName(), "seat name must not null.");


		promotionSeatBean.setSeatId(updatePromotionSeatBean.getSeatId());
		promotionSeatBean.setSeatName(updatePromotionSeatBean.getSeatName());
		String returnMsg = "推广位修改成功！";
		promotionSeatBean.setModifyTime(new Date());
		promotionSeatBean.setModifyUserId(user.getId());
		promotionSeatBean.setPlatform(updatePromotionSeatBean.getPlatform());
		boolean result = false;
		try{
			result = promotionSeatMapper.update(promotionSeatBean) > 0;
		}catch(DuplicateKeyException e){ //唯一键约束异常不做处理
			result = true;
		}
		message.put("isSuccess", result);
		if(result){
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改推广位，推广位ID为", promotionSeatBean.getId(), "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);

		return message.getMap();
	}

	@Override
	public Map<String, Object> paging(JSONObject json, UserBean user, HttpRequestInfoBean request){
		logger.info("PromotionSeatServiceImpl-->paging():json="+ json);

		String orderField = JsonUtil.getStringValue(json, "field", "id"); //排序字段
		String orderType = JsonUtil.getStringValue(json, "order", "asc"); //排序类型
		int current = JsonUtil.getIntValue(json, "page", 1) -1;
		int pageSize = JsonUtil.getIntValue(json, "limit", 10);
		LayuiTableResponseMap message = new LayuiTableResponseMap();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		int start = SqlUtil.getPageStart(current, pageSize, 0);
		String platform = JsonUtil.getStringValue(json, "platform", null);
		rs = promotionSeatMapper.paging(platform, ConstantsUtil.STATUS_NORMAL, start, pageSize, orderField, orderType);

		if(CollectionUtil.isNotEmpty(rs)){
			for(Map<String, Object> m: rs){
				int userId = StringUtil.changeObjectToInt(m.get("user_id"));
				int createUserId = StringUtil.changeObjectToInt(m.get("create_user_id"));
				m.put("createUser", userHandler.getUserName(createUserId));
				if(userId > 0)
					m.put("allotUser", "<a class='layui-btn layui-btn-danger layui-btn-xs' lay-event='delallot' style='height: 25px !important; line-height: 25px !important;'>"+ userHandler.getUserName(userId) +"</a>");
				else{
					m.put("allotUser", "<a class='layui-btn layui-btn-normal layui-btn-xs' lay-event='allot' style='height: 25px !important; line-height: 25px !important;'>分配</a>");
				}
			}
		}
		message.setCode(0);
		message.setCount(SqlUtil.getTotalByList(promotionSeatMapper.pagingTotal(platform, ConstantsUtil.STATUS_NORMAL)));
		message.put("isSuccess", true);
		message.put("data", rs);
		return message.getMap();
	}

	@Override
	public Map<String, Object> noallot(long seatId, JSONObject json, UserBean user, HttpRequestInfoBean request){
		logger.info("PromotionSeatServiceImpl-->noallot():seatId = "+ seatId +",json="+ json);

		S_PromotionSeatBean promotionSeatBean = promotionSeatMapper.findById(S_PromotionSeatBean.class, seatId);
		if(promotionSeatBean == null)
			throw new NullPointerException("该推广位已经不存在");
		ResponseMap message = new ResponseMap();
		if(promotionSeatBean.getUserId() > 0){
			message.put("message", "该推广位已经分配给用户："+ userHandler.getUserName(promotionSeatBean.getUserId()));
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			return message.getMap();
		}
		String platform = promotionSeatBean.getPlatform();
		List<Map<String, Object>> rs = promotionSeatMapper.noallot(platform, ConstantsUtil.STATUS_NORMAL);
		message.put("isSuccess", true);
		message.put("data", rs);
		return message.getMap();
	}

	@Override
	public Map<String, Object> allotObject(long seatId, long userId, UserBean user, HttpRequestInfoBean request) throws NoticeException {
		logger.info("PromotionSeatServiceImpl-->allotObject():seatId = "+ seatId +", userId="+ userId);

		S_PromotionSeatBean promotionSeatBean = promotionSeatMapper.findById(S_PromotionSeatBean.class, seatId);
		if(promotionSeatBean == null)
			throw new NullPointerException("该推广位已经不存在");
		ResponseMap message = new ResponseMap();
		if(promotionSeatBean.getUserId() > 0){
			message.put("message", "该推广位已经分配给用户："+ userHandler.getUserName(promotionSeatBean.getUserId()));
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			return message.getMap();
		}

		promotionSeatBean.setUserId(userId);
		promotionSeatBean.setAllotTime(new Date());
		promotionSeatBean.setModifyTime(new Date());
		promotionSeatBean.setModifyUserId(user.getId());
		boolean result = promotionSeatMapper.update(promotionSeatBean) > 0;

		if(result){
			//找看看有没有申请记录，有的话修改申请记录状态
			S_PromotionSeatApplyBean applyBean = promotionSeatApplyMapper.getApply(userId, promotionSeatBean.getPlatform());
			if(applyBean != null){
				applyBean.setModifyUserId(user.getId());
				applyBean.setModifyTime(new Date());
				applyBean.setSeatId(promotionSeatBean.getId());
				result = promotionSeatApplyMapper.update(applyBean) > 0;
			}
			//发送通知给用户
			if(result){
				Notification notification = new Notification();
				notification.setContent("您申请"+ applyBean.getPlatform() +"平台的推广位已经被分配，请查看！");
				notification.setFromUserId(user.getId());
				notification.setToUserId(userId);
				notification.setType(EnumUtil.NotificationType.通知);
				INoticeFactory factory = new NoticeFactory();
				result = factory.create(EnumUtil.NoticeType.站内信).send(notification);
			}
			message.put("message", "分配成功");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			message.put("isSuccess", true);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"给推广位ID为："+ promotionSeatBean.getId() +" 分配给用户："+ userId+", 结果是"+ StringUtil.getSuccessOrNoStr(result)).toString(), "allotObject()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> deleteAllot(long seatId, UserBean user, HttpRequestInfoBean request) throws NoticeException {
		logger.info("PromotionSeatServiceImpl-->deleteAllot():seatId = "+ seatId);

		S_PromotionSeatBean promotionSeatBean = promotionSeatMapper.findById(S_PromotionSeatBean.class, seatId);
		if(promotionSeatBean == null)
			throw new NullPointerException("该推广位已经不存在");
		ResponseMap message = new ResponseMap();
		long userId = promotionSeatBean.getUserId();
		if(userId == 0){
			message.put("message", "该推广位暂时还没有分配给任何用户");
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			return message.getMap();
		}

		promotionSeatBean.setUserId(0L);
		promotionSeatBean.setAllotTime(null);
		promotionSeatBean.setModifyTime(new Date());
		promotionSeatBean.setModifyUserId(user.getId());
		boolean result = promotionSeatMapper.update(promotionSeatBean) > 0;

		if(result){
			//找看看有没有申请记录，有的话删除申请记录状态
			S_PromotionSeatApplyBean applyBean = promotionSeatApplyMapper.getApply(userId, promotionSeatBean.getPlatform());
			if(applyBean != null){
				result = promotionSeatApplyMapper.delete(applyBean) > 0;
			}
			//发送通知给用户
			if(result){
				Notification notification = new Notification();
				notification.setContent("您在"+ applyBean.getPlatform() +"平台的推广位已经被取消，请知悉，有需要可以再次申请！");
				notification.setFromUserId(user.getId());
				notification.setToUserId(userId);
				notification.setType(EnumUtil.NotificationType.通知);
				INoticeFactory factory = new NoticeFactory();
				result = factory.create(EnumUtil.NoticeType.站内信).send(notification);
			}
			message.put("message", "解除绑定分配对象成功");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			message.put("isSuccess", true);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除推广位ID为："+ promotionSeatBean.getId() +" 的用户："+ userId+", 结果是"+ StringUtil.getSuccessOrNoStr(result)).toString(), "deleteAllot()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> delete(long seatId, UserBean user, HttpRequestInfoBean request){
		logger.info("PromotionSeatServiceImpl-->delete():seatId=" +seatId);
		ResponseMap message = new ResponseMap();
		S_PromotionSeatBean promotionSeatBean = promotionSeatMapper.findById(S_PromotionSeatBean.class, seatId);
		if(promotionSeatBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该推广位不存在.value));


		//只有商城管理员才能做删除操作
		checkMallAdmin(user, user.getId());

		boolean result = promotionSeatMapper.delete(promotionSeatBean) > 0;
		message.put("isSuccess", result);
		if(result){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.请求返回成功码.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除推广位ID为："+ promotionSeatBean).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

}
