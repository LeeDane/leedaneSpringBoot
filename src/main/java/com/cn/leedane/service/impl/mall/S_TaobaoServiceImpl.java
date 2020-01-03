package com.cn.leedane.service.impl.mall;

import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.mall.taobao.other.AlimamaShareLink;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_PlatformProductBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.S_TaobaoService;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 淘宝商品的service的实现类(已经废弃，请参考MallSearchServiceImpl使用）
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Deprecated
@Service("S_TaobaoService")
public class S_TaobaoServiceImpl extends MallRoleCheckService implements S_TaobaoService<IDBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	@Override
	public ResponseModel search(JSONObject jo, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_TaobaoServiceImpl-->search():jo="+jo);
		ResponseModel responseModel = new ResponseModel();
		long current = JsonUtil.getLongValue(jo, "current", 0);
		long rows = JsonUtil.getLongValue(jo, "rows", 10);
		String keyword = JsonUtil.getStringValue(jo, "keyword"); //搜索关键字
		boolean result = false;
		try {
			Connection.Response res;
			Map<String, String> dataMap = new HashMap<String, String>();
			dataMap.put("q", keyword);
			dataMap.put("t", JsonUtil.getStringValue(jo, "t"));
			dataMap.put("auctionTag", "");
			dataMap.put("dpyhq", "1");
			dataMap.put("perPageSize", rows +"");
			dataMap.put("shopTag", "dpyhq"); //"yxjh"
			dataMap.put("toPage", current +"");

			res = Jsoup.connect("http://pub.alimama.com/items/search.json")
					.data(dataMap)
					.userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.75 Safari/537.36 LBBROWSER")
					.timeout(10000)//网络链接超时的时间
					.method(Method.GET)
					.ignoreContentType(true)
					.execute();
			JSONObject resultJson = JSONObject.fromObject(res.body());
			JSONObject resultData = resultJson.getJSONObject("data");
			Map<String, Object> resultMap = new HashMap<String, Object>();
			JSONObject paginator = resultData.getJSONObject("paginator");
			List<S_PlatformProductBean> taobaoItems = new ArrayList<S_PlatformProductBean>();
			int total = 0;
			if(paginator != null && paginator.containsKey("items") && !paginator.isEmpty()){
				total = paginator.getInt("items");
				JSONArray pageList = resultData.getJSONArray("pageList");
				for(int i = 0; i < pageList.size(); i++){
					S_PlatformProductBean taobaoProductBean = new S_PlatformProductBean();
					JSONObject item = pageList.getJSONObject(i);
					taobaoProductBean.setAuctionId(item.getLong("auctionId"));
					taobaoProductBean.setCashBack(item.getDouble("tkCommFee"));
					taobaoProductBean.setCashBackRatio(item.getDouble("tkRate"));
					taobaoProductBean.setImg(item.getString("pictUrl"));
					taobaoProductBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
					taobaoProductBean.setPrice(item.getDouble("zkPrice"));
					taobaoProductBean.setTitle(item.getString("title"));
					taobaoProductBean.setShopTitle(item.getString("shopTitle"));
					taobaoProductBean.setCouponAmount(item.getInt("couponAmount"));
					taobaoProductBean.setCouponLeftCount(item.getLong("couponLeftCount"));
					taobaoProductBean.setClickId("tb_"+ item.getLong("num_iid"));
					taobaoItems.add(taobaoProductBean);
				}

			}
			resultMap.put("list", taobaoItems);
			result = true;
			responseModel.error().message(resultMap).total(total);
		} catch (IOException  e) {
			e.printStackTrace();
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.淘宝api请求失败.value)).code(EnumUtil.ResponseCode.淘宝api请求失败.value);
		}

		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user != null ? user.getAccount(): "","对淘宝的商品发起查询", "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "search()", ConstantsUtil.STATUS_NORMAL, 0);
		return responseModel;
	}
	
	@Override
	public ResponseModel buildShare(String taobaoId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_TaobaoServiceImpl-->buildShare():taobaoId="+taobaoId);
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
				responseModel.error().message( "该商品暂时没有佣金，无法生成共享链接！");
		} catch (Exception  e) {
			e.printStackTrace();
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.淘宝api请求失败.value)).code(EnumUtil.ResponseCode.淘宝api请求失败.value);
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"对淘宝的商品ID为", taobaoId, "生成分享链接。", "结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "buildShare()", ConstantsUtil.STATUS_NORMAL, 0);
		return responseModel;
	}
}
