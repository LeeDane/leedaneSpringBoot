package com.cn.leedane.service.impl.mall;

import com.cn.leedane.handler.mall.S_ProductHandler;
import com.cn.leedane.mall.jingdong.api.DetailBigFieldApi;
import com.cn.leedane.mall.model.SearchProductRequest;
import com.cn.leedane.mall.model.SearchProductResult;
import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.mall.pdd.api.DetailSimpleApi;
import com.cn.leedane.mall.taobao.api.RecommendProductApi;
import com.cn.leedane.mall.taobao.other.AlimamaShareLink;
import com.cn.leedane.mall.taobao.api.SearchMaterialApi;
import com.cn.leedane.mall.taobao.api.SearchProductApi;
import com.cn.leedane.mapper.mall.S_ProductMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.MallSearchService;
import com.cn.leedane.service.mall.S_TaobaoService;
import com.cn.leedane.utils.*;
import com.suning.api.exception.SuningApiException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 淘宝商品的service的实现类
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Service("mallSearchService")
public class MallSearchServiceImpl extends MallRoleCheckService implements MallSearchService<IDBean>{
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
	public ResponseModel product(JSONObject jo, UserBean user, HttpRequestInfoBean request) throws Exception {
		logger.info("MallSearchServiceImpl-->product():jo="+jo);
		long start = System.currentTimeMillis();
		long current = JsonUtil.getLongValue(jo, "current", 0);
		long rows = JsonUtil.getLongValue(jo, "rows", 10);
		String keyword = JsonUtil.getStringValue(jo, "keyword"); //搜索关键字
		String platform = JsonUtil.getStringValue(jo, "platform", EnumUtil.ProductPlatformType.淘宝.value);
		String sort = JsonUtil.getStringValue(jo, "sort");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		SearchProductRequest productRequest = new SearchProductRequest();
		productRequest.setKeyword(keyword);
		productRequest.setPageSize(rows);
		productRequest.setPageNo(current);
		productRequest.setSort(sort);

		SearchProductResult productResult = null;
		if(EnumUtil.ProductPlatformType.淘宝.value.equalsIgnoreCase(platform) || EnumUtil.ProductPlatformType.淘宝.value.equalsIgnoreCase(platform)){
			productResult = SearchProductApi.searchProduct(productRequest);
		}else if(EnumUtil.ProductPlatformType.京东.value.equalsIgnoreCase(platform)){
			productResult = com.cn.leedane.mall.jingdong.api.SearchProductApi.searchProduct(productRequest);
		}else if(EnumUtil.ProductPlatformType.拼多多.value.equalsIgnoreCase(platform)){
			//由于拼多多不支持链接查询，但是支持商品ID查询
			if(StringUtil.isLink(productRequest.getKeyword())){
				String productId = CommonUtil.parseLinkParams(productRequest.getKeyword(), "goods_id");
				if(StringUtil.isNotNull(productId))
					productRequest.setKeyword(productId);
			}

			productResult = com.cn.leedane.mall.pdd.api.SearchProductApi.searchProduct(productRequest);
		}else if(EnumUtil.ProductPlatformType.苏宁.value.equalsIgnoreCase(platform)){
			productResult = com.cn.leedane.mall.suning.api.SearchProductApi.searchProduct(productRequest);
		}
		if(productResult == null)
			return new ResponseModel().error().message("返回结果为空。").code(EnumUtil.ResponseCode.请求返回成功码.value);
		resultMap.put("list", productResult.getTaobaoItems());

		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user != null ? user.getAccount(): "","对淘宝的商品发起查询", "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "search()", ConstantsUtil.STATUS_NORMAL, 0);
		return new ResponseModel().ok().message(resultMap).total(productResult.getTotal());
	}
	@Override
	public ResponseModel buildShare(String taobaoId, UserBean user, HttpRequestInfoBean request) {
		logger.info("MallSearchServiceImpl-->buildShare():taobaoId="+taobaoId);
		ResponseModel responseModel = new ResponseModel();
		try {
			AlimamaShareLink alimama = new AlimamaShareLink();
			JSONObject dataJson = alimama.doParse(taobaoId).getJSONObject("data");
			if(dataJson != null){
				ProductPromotionLinkBean promotionLinkBean = new ProductPromotionLinkBean();
				promotionLinkBean.setClickUrl(JsonUtil.getStringValue(dataJson, "clickUrl"));
				promotionLinkBean.setCouponLink(JsonUtil.getStringValue(dataJson, "couponLink"));
				promotionLinkBean.setCouponLinkTaoToken(JsonUtil.getStringValue(dataJson, "couponLinkTaoToken"));
				promotionLinkBean.setCouponShortLinkUrl(JsonUtil.getStringValue(dataJson, "couponShortLinkUrl"));
				promotionLinkBean.setQrCodeUrl(JsonUtil.getStringValue(dataJson, "qrCodeUrl"));
				promotionLinkBean.setShortLinkUrl(JsonUtil.getStringValue(dataJson, "shortLinkUrl"));
				promotionLinkBean.setTaoToken(JsonUtil.getStringValue(dataJson, "taoToken"));
				responseModel.ok().message(promotionLinkBean);
			}else
				responseModel.error().message("该商品暂时没有佣金，无法生成共享链接！");
		} catch (Exception  e) {
			e.printStackTrace();
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.淘宝api请求失败.value)).code(EnumUtil.ResponseCode.淘宝api请求失败.value);
		}

		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"对淘宝的商品ID为", taobaoId, "生成分享链接。", "结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "buildShare()", ConstantsUtil.STATUS_NORMAL, 0);
		return responseModel;
	}

	@Override
	public ResponseModel productRecommend(String itemId, JSONObject jo, UserBean user, HttpRequestInfoBean request) throws ApiException, PddException, SuningApiException {
		logger.info("MallSearchServiceImpl-->productRecommend(): itemId="+ itemId +", jo="+jo);
		ResponseMap message = new ResponseMap();

		long count = JsonUtil.getLongValue(jo, "count", 12);
		if(itemId.startsWith("tb_")){
			long productIdTemp = StringUtil.changeObjectToLong(itemId.substring(4, itemId.length()));
			return new ResponseModel().ok().message(com.cn.leedane.mall.taobao.api.RecommendProductApi.recommend(productIdTemp, count).getItems());
		}else if(itemId.startsWith("jd_")){
			return new ResponseModel().ok().message( "京东没有推荐商品的api");
		}else if(itemId.startsWith("pdd_")){
			return new ResponseModel().ok().message("拼多多没有推荐商品的api");
		}else if(itemId.startsWith("sn_")){
			String productIdTemp = itemId.substring(3, itemId.length());
			String commodityCode = productIdTemp.split("-")[0];
			String supplierCode = productIdTemp.split("-")[0];
			return new ResponseModel().ok().message(com.cn.leedane.mall.suning.api.RecommendProductApi.recommend(commodityCode, supplierCode, count).getItems());
		}
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user != null ? user.getAccount(): "","对淘宝的商品发起查询", "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "search()", ConstantsUtil.STATUS_NORMAL, 0);
		return new ResponseModel().error();
	}

	@Override
	public ResponseModel bigfield(String productId, JSONObject json, UserBean user, HttpRequestInfoBean request) throws Exception {
		logger.info("MallSearchServiceImpl-->bigfield():productId="+productId);
		ResponseMap message = new ResponseMap();
		if(productId.startsWith("tb_")){
			//return taobaoService.transform(json, user, request);
		}else if(productId.startsWith("jd_")){
			String productIdTemp = productId.substring(3, productId.length());
			return new ResponseModel().ok().message(DetailBigFieldApi.get(productIdTemp));
		}else if(productId.startsWith("pdd_")){
			String productIdTemp = productId.substring(4, productId.length());
			S_PlatformProductBean platformProductBean = DetailSimpleApi.getDetail(productIdTemp);
			return new ResponseModel().ok().message(platformProductBean == null? "商品不存在": platformProductBean.getDetail());
		}else if(productId.startsWith("sn")){
			String productIdTemp = productId.substring(3, productId.length());
			return new ResponseModel().ok().message(productIdTemp);
		}
		return new ResponseModel().error();
	}

	@Override
	public ResponseModel shop(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		return null;
	}

	@Override
	public ResponseModel activity(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		return null;
	}
}
