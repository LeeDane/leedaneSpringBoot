package com.cn.leedane.service.impl.manage;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.mapper.mall.PromotionSeatApplyMapper;
import com.cn.leedane.mapper.mall.ReferrerMapper;
import com.cn.leedane.mapper.mall.ReferrerRecordMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_PromotionSeatApplyBean;
import com.cn.leedane.model.mall.S_ReferrerBean;
import com.cn.leedane.model.mall.S_ReferrerRecordBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.manage.ManageMallService;
import com.cn.leedane.utils.*;
import com.qiniu.util.Json;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 我的商城设置的service的实现类
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Service("manageMallService")
public class ManageMallServiceImpl implements ManageMallService<IDBean> {
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private PromotionSeatApplyMapper promotionSeatApplyMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private ReferrerMapper referrerMapper;

	@Autowired
	private ReferrerRecordMapper referrerRecordMapper;
	@Override
	public ResponseModel promotionApply(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMallServiceImpl-->promotionApply():jo="+jo);
		String platform = JsonUtil.getStringValue(jo, "platform");
		ParameterUnspecificationUtil.checkNullString(platform, "platform must not null.");

		S_PromotionSeatApplyBean promotionSeatApplyBean = new S_PromotionSeatApplyBean();
		promotionSeatApplyBean.setPlatform(platform);
		promotionSeatApplyBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		promotionSeatApplyBean.setCreateUserId(user.getId());
		promotionSeatApplyBean.setCreateTime(new Date());
		promotionSeatApplyBean.setModifyUserId(user.getId());
		promotionSeatApplyBean.setModifyTime(new Date());
		boolean success = promotionSeatApplyMapper.save(promotionSeatApplyBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"申请", platform, "推广位， 结果是：", StringUtil.getSuccessOrNoStr(success)).toString(), "promotionApply()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success)
			return new ResponseModel().ok().message("申请成功，请稍等管理员审核！");

		return new ResponseModel().error().message("申请失败，请稍后重试。");
	}

	@Override
	public ResponseModel buildReferrerCode(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMallServiceImpl-->buildReferrerCode():jo="+jo);
		//先查找是否存在邀请记录
		S_ReferrerBean referrerBean = referrerMapper.findReferrerCode(user.getId());
		if(referrerBean != null){
			return new ResponseModel().error().message("您已经有推荐码："+ referrerBean.getCode()).code(EnumUtil.ResponseCode.您已经有推荐码.value);
		}

		String code = ShareCodeUtil.toSerialCode(user.getId());
		if(StringUtil.isNull(code))
			return new ResponseModel().error().message("系统无法生成邀请码，请稍后重试！");
		referrerBean = new S_ReferrerBean();
		referrerBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		referrerBean.setCreateUserId(user.getId());
		referrerBean.setCreateTime(new Date());
		referrerBean.setModifyUserId(user.getId());
		referrerBean.setModifyTime(new Date());
		referrerBean.setCode(code);
		boolean success = referrerMapper.save(referrerBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"生成商城推广码：", code, "， 结果是：", StringUtil.getSuccessOrNoStr(success)).toString(), "buildReferrerCode()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success)
			return new ResponseModel().ok().message("您的推荐码已经生成！");

		return new ResponseModel().error().message("推荐码保存生成失败，请稍后重试！").code(EnumUtil.ResponseCode.数据库保存失败.value);
	}

	@Override
	public ResponseModel bindReferrer(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMallServiceImpl-->bindReferrer():jo="+jo);
		String code = JsonUtil.getStringValue(jo, "code");
		//检验是否存在推荐码
		ParameterUnspecificationUtil.checkNullString(code, "referrer code must not null.");
		//先查找是否存在邀请记录
		S_ReferrerRecordBean recordBean = referrerRecordMapper.findReferrer(user.getId());
		if(recordBean != null){
			return new ResponseModel().error().message("您已经绑定过推荐人:"+ userHandler.getUserName(recordBean.getUserId())).code(EnumUtil.ResponseCode.已经绑定过推荐人.value);
		}

		S_ReferrerBean referrerBean = referrerMapper.findReferrerByCode(code);
		if(referrerBean == null){
			return new ResponseModel().error().message("推荐码无效！");
		}

		long userId = referrerBean.getCreateUserId();
		UserBean toUser = userMapper.findById(UserBean.class, userId);
		if(toUser == null){
			return new ResponseModel().error().message("该推荐人不存在！");
		}

		if(userId == user.getId()){
			return new ResponseModel().error().message("推荐人不能是您自己");
		}
		recordBean = new S_ReferrerRecordBean();
		recordBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		recordBean.setCreateUserId(user.getId());
		recordBean.setCreateTime(new Date());
		recordBean.setModifyUserId(user.getId());
		recordBean.setModifyTime(new Date());
		recordBean.setCode(code);
		recordBean.setUserId(userId);
		boolean success = referrerRecordMapper.save(recordBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"绑定推荐人:"+ toUser.getAccount() +"，推荐码是：", code, "， 结果是：", StringUtil.getSuccessOrNoStr(success)).toString(), "bindReferrer()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success)
			return new ResponseModel().ok().message("您已经成功绑定推荐人："+ toUser.getAccount());

		return new ResponseModel().error().message("绑定推荐人失败，请稍后重试！").code(EnumUtil.ResponseCode.数据库保存失败.value);
	}
}
