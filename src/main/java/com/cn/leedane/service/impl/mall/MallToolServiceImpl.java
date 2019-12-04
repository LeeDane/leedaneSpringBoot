package com.cn.leedane.service.impl.mall;

import com.cn.leedane.handler.mall.S_ProductHandler;
import com.cn.leedane.mall.taobao.api.PromotionApi;
import com.cn.leedane.mapper.mall.S_ProductMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.MallToolService;
import com.cn.leedane.service.mall.S_TaobaoService;
import com.cn.leedane.utils.*;
import com.google.zxing.WriterException;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 淘宝商品的service的实现类
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Service("mallToolService")
public class MallToolServiceImpl extends MallRoleCheckService implements MallToolService<IDBean> {
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private S_ProductHandler productHandler;
	
	@Autowired
	private S_ProductMapper productMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private S_TaobaoService<IDBean> taobaoService;

	@Override
	public Map<String, Object> transform(String productId, JSONObject json, UserBean user,
										 HttpRequestInfoBean request) throws WriterException, JdException, ApiException {
		logger.info("MallToolServiceImpl-->transform():productId="+productId);
		ResponseMap message = new ResponseMap();
		if(productId.startsWith("tb_")){
			//标题，不能为空，不然无法转发淘口令
			String title = JsonUtil.getStringValue(json, "title");
			ParameterUnspecificationUtil.checkNullString(title, "param title must not null.");
			String longLink = JsonUtil.getStringValue(json, "productUrl");
			ParameterUnspecificationUtil.checkNullString(longLink, "param productUrl must not null.");
			try{
				message.put("message", PromotionApi.getPromotion(title, JsonUtil.getStringValue(json, "img"), longLink,
						JsonUtil.getStringValue(json, "couponUrl")));
				message.put("isSuccess", true);
			}catch (ApiException e){
				message.put("message", "无法生成共享链接！");
			}finally {
				return message.getMap();
			}
		}else if(productId.startsWith("pdd_")){
			String productIdTemp = productId.substring(4, productId.length());
			message.put("message", com.cn.leedane.mall.pdd.api.PromotionApi.getPromotion(StringUtil.changeObjectToLong(productIdTemp), JsonUtil.getStringValue(json,"title")));
			message.put("isSuccess", true);
			return message.getMap();
		}else if(productId.startsWith("jd_")){
			message.put("message", EnumUtil.ResponseCode.请求返回成功码.value);
			message.put("isSuccess", true);
			return message.getMap();
		}

		return message.getMap();
	}


	@Override
	public Map<String, Object> parseUrlGetId(JSONObject json, UserBean user,
										 HttpRequestInfoBean request) throws JdException, ApiException {
		String url = JsonUtil.getStringValue(json, "url");
		logger.info("MallToolServiceImpl-->parseUrlGetId():url=" + url);
		ParameterUnspecificationUtil.checkNullString(url, "url must not null.");
		ResponseMap message = new ResponseMap();
		String id = null;

		//先获取链接中的id=字段
		id = CommonUtil.parseLinkParams(url, "id", "goods_id");

		//获取不到ID=的匹配字段，中解析地址中的.com/xxxxxx.html中的xxxxxx
		if(StringUtil.isNull(id))
			id = CommonUtil.parseLinkId(url);

		if(StringUtil.isNotNull(id)){
			message.put("message", id);
			message.put("isSuccess", true);
		}else{
			message.put("responseCode", EnumUtil.ResponseCode.系统无法解析出地址栏中的id.value);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.系统无法解析出地址栏中的id.value));
			message.put("isSuccess", false);
		}

		return message.getMap();
	}
}
