package com.cn.leedane.service.impl.mall;

import com.cn.leedane.handler.mall.S_HomeCarouselHandler;
import com.cn.leedane.handler.mall.S_ProductHandler;
import com.cn.leedane.mapper.mall.S_HomeCarouselMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_HomeCarouselBean;
import com.cn.leedane.model.mall.S_ProductBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.S_HomeCarouselService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 首页轮播商品的service的实现类
 * @author LeeDane
 * 2017年12月26日 下午2:26:08
 * version 1.0
 */
@Service("S_HomeCarouselService")
public class S_HomeCarouselServiceImpl extends MallRoleCheckService implements S_HomeCarouselService<S_HomeCarouselBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private S_HomeCarouselHandler homeCarouselHandler;
	
	@Autowired
	private S_ProductHandler productHandler;
	
	@Autowired
	private S_HomeCarouselMapper homeCarouselMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public Map<String, Object> add(JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_CarouselServiceImpl-->add():json="+json);
		long productId = JsonUtil.getLongValue(json, "product_id");
		int order = JsonUtil.getIntValue(json, "order", 1);
		S_ProductBean productBean = productHandler.getNormalProductBean(productId);
		if(productBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));
		//检查权限
		checMallkAdmin(user);
		
		S_HomeCarouselBean homeCarouselBean = new S_HomeCarouselBean();

		ResponseMap message = new ResponseMap();
		String returnMsg = "轮播已经发布成功！";
		homeCarouselBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		Date createTime = new Date();
		homeCarouselBean.setCreateTime(createTime);
		homeCarouselBean.setCreateUserId(user.getId());
		homeCarouselBean.setOrder(order);
		homeCarouselBean.setProductId(productId);
		homeCarouselBean.setImg(productBean.getMainImgLinks().split(";")[0]);
		boolean result = homeCarouselMapper.save(homeCarouselBean) > 0;
		if(result){
			homeCarouselHandler.deleteCarouselBeansCache();
			message.put("isSuccess", true);
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"发布新的轮播商品:", productBean.getTitle() , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> delete(long carouselId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_CarouselServiceImpl-->delete():carouselId="+carouselId);
		
		//检查权限
		checMallkAdmin(user);
		
		boolean result = homeCarouselMapper.deleteById(S_HomeCarouselBean.class, carouselId) > 0;
		ResponseMap message = new ResponseMap();
		if(result){
			homeCarouselHandler.deleteCarouselBeansCache();
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除轮播商品, 轮播id为：", carouselId, "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> carousel() {
		logger.info("S_CarouselServiceImpl-->carousel()");
		ResponseMap message = new ResponseMap();
		message.put("message", homeCarouselHandler.getCarouselBeans());
		message.put("isSuccess", true);
		return message.getMap();
	}
}
