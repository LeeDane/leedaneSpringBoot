package com.cn.leedane.service.impl.manage;

import com.cn.leedane.mapper.mall.PromotionSeatApplyMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_PromotionSeatApplyBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.manage.ManageMallService;
import com.cn.leedane.utils.*;
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

	@Override
	public Map<String, Object> promotionApply(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMallServiceImpl-->promotionApply():jo="+jo);
		ResponseMap message = new ResponseMap();

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
		if(success){
			message.put("isSuccess", true);
			message.put("message", "申请成功，请稍等管理员审核！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			return message.getMap();
		}
		message.put("message", "申请失败，请稍后重试。");
		message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
		return message.getMap();
	}
}
