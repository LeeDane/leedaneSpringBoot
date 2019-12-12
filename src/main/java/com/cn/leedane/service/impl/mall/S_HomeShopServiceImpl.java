package com.cn.leedane.service.impl.mall;

import com.cn.leedane.handler.mall.S_HomeShopHandler;
import com.cn.leedane.handler.mall.S_ShopHandler;
import com.cn.leedane.mapper.mall.S_HomeShopMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_HomeShopBean;
import com.cn.leedane.model.mall.S_ShopBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.S_HomeShopService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

/**
 * 首页商店的service的实现类
 * @author LeeDane
 * 2018年1月11日 下午2:19:23
 * version 1.0
 */
@Service("S_HomeShopService")
public class S_HomeShopServiceImpl extends MallRoleCheckService implements S_HomeShopService<S_HomeShopBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private S_HomeShopHandler homeShopHandler;
	
	@Autowired
	private S_ShopHandler shopHandler;
	
	@Autowired
	private S_HomeShopMapper homeShopMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public Map<String, Object> add(JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_HomeShopServiceImpl-->add():json="+json);
		long shopId = JsonUtil.getLongValue(json, "shop_id");
		int order = JsonUtil.getIntValue(json, "shop_order", 1);
		S_ShopBean shopBean = shopHandler.getNormalShopBean(shopId);
		if(shopBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商店不存在或已被删除.value));
		//检查权限
		checkMallAdmin(user);
		
		S_HomeShopBean homeShopBean = new S_HomeShopBean();

		ResponseMap message = new ResponseMap();
		String returnMsg = "店铺已经发布成功！";
		homeShopBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		Date createTime = new Date();
		homeShopBean.setCreateTime(createTime);
		homeShopBean.setCreateUserId(user.getId());
		homeShopBean.setShopOrder(order);
		homeShopBean.setShopId(shopId);
		boolean result = homeShopMapper.save(homeShopBean) > 0;
		if(result){
			homeShopHandler.deleteShopBeansCache();
			message.put("isSuccess", true);
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"发布新的店铺:", shopBean.getName() , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> delete(long shopId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_HomeShopServiceImpl-->delete():shopId="+shopId);

		//检查权限
		checkMallAdmin(user);

		boolean result = homeShopMapper.deleteById(S_HomeShopBean.class, shopId) > 0;
		ResponseMap message = new ResponseMap();
		if(result){
			homeShopHandler.deleteShopBeansCache();
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除商店, 店铺id为：", shopId, "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> shops() {
		logger.info("S_HomeShopServiceImpl-->shops()");
		ResponseMap message = new ResponseMap();
		message.put("message", homeShopHandler.getShopBeans());
		message.put("isSuccess", true);
		return message.getMap();
	}
}
