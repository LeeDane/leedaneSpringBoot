package com.cn.leedane.service.impl.mall;

import com.cn.leedane.mall.model.ProductPromotionLinkBean;
import com.cn.leedane.mall.taobao.api.AlimamaShareLink;
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
import com.cn.leedane.utils.ResponseMap;
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
	
	/*@Override
	public Map<String, Object> search(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("S_TaobaoServiceImpl-->search():jo="+jo);
		ResponseMap message = new ResponseMap();
		
		long current = JsonUtil.getLongValue(jo, "current", 0);
		long rows = JsonUtil.getLongValue(jo, "rows", 10);
		
		TaobaoClient client = new DefaultTaobaoClient("http://gw.api.taobao.com/router/rest", "24712175", "a34e8aaca4b223fc8382a6b88205821b");
		TbkItemGetRequest req = new TbkItemGetRequest();
		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url,seller_id,volume,nick");
		req.setQ("小米");
		//req.setCat("16,18"); //后台类目ID，用,分割，最大10个，该ID可以通过taobao.itemcats.get接口获取到
		//req.setItemloc("杭州"); //所在地
		req.setSort("tk_rate_des");//排序_des（降序），排序_asc（升序），销量（total_sales），淘客佣金比率（tk_rate）， 累计推广量（tk_total_sales），总支出佣金（tk_total_commi）
		//req.setIsTmall(false); //是否商城商品，设置为true表示该商品是属于淘宝商城商品，设置为false或不设置表示不判断这个属性
		//req.setIsOverseas(false); //是否海外商品，设置为true表示该商品是属于海外商品，设置为false或不设置表示不判断这个属性
		//req.setStartPrice(10L); //折扣价范围下限，单位：元
		//req.setEndPrice(10L); //折扣价范围上限，单位：元
		//req.setStartTkRate(123L); //淘客佣金比率上限，如：1234表示12.34%
		//req.setEndTkRate(123L); //淘客佣金比率下限，如：1234表示12.34%
		req.setPlatform(1L); //链接形式：1：PC，2：无线，默认：１
		req.setPageNo(current); //第几页，默认：１
		req.setPageSize(rows);
		TbkItemGetResponse rsp;
		
		boolean result = false;
		try {
			rsp = client.execute(req);
			System.out.println(rsp.getBody());
			result = true;
		} catch (ApiException e) {
			e.printStackTrace();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.淘宝api请求失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.淘宝api请求失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user != null ? user.getAccount(): "","对淘宝的商品发起查询", "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "search()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}*/

	/*@Override
	public Map<String, Object> search(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("S_TaobaoServiceImpl-->search():jo="+jo);
		ResponseMap message = new ResponseMap();
		
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
					JSONObject object = pageList.getJSONObject(i);
					taobaoProductBean.setAuctionId(object.getLong("auctionId"));
					taobaoProductBean.setCashBack(object.getDouble("tkCommFee"));
					taobaoProductBean.setCashBackRatio(object.getDouble("tkRate"));
					taobaoProductBean.setImg(object.getString("pictUrl"));
					taobaoProductBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
					taobaoProductBean.setPrice(object.getDouble("zkPrice"));
					taobaoProductBean.setTitle(object.getString("title"));
					taobaoProductBean.setShopTitle(object.getString("shopTitle"));
					taobaoProductBean.setCouponAmount(object.getInt("couponAmount"));
					taobaoProductBean.setCouponLeftCount(object.getLong("couponLeftCount"));
					taobaoItems.add(taobaoProductBean);
				}
				
			}
			message.put("total", total);
			resultMap.put("list", taobaoItems);
			result = true;
			message.put("isSuccess", true);
			message.put("message", resultMap);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		} catch (IOException  e) {
			e.printStackTrace();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.淘宝api请求失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.淘宝api请求失败.value);
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user != null ? user.getAccount(): "","对淘宝的商品发起查询", "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "search()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}*/

	@Override
	public Map<String, Object> search(JSONObject jo, UserBean user,
									  HttpRequestInfoBean request) {

		logger.info("S_TaobaoServiceImpl-->search():jo="+jo);
		ResponseMap message = new ResponseMap();

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
					JSONObject object = pageList.getJSONObject(i);
					taobaoProductBean.setAuctionId(object.getLong("auctionId"));
					taobaoProductBean.setCashBack(object.getDouble("tkCommFee"));
					taobaoProductBean.setCashBackRatio(object.getDouble("tkRate"));
					taobaoProductBean.setImg(object.getString("pictUrl"));
					taobaoProductBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
					taobaoProductBean.setPrice(object.getDouble("zkPrice"));
					taobaoProductBean.setTitle(object.getString("title"));
					taobaoProductBean.setShopTitle(object.getString("shopTitle"));
					taobaoProductBean.setCouponAmount(object.getInt("couponAmount"));
					taobaoProductBean.setCouponLeftCount(object.getLong("couponLeftCount"));
					taobaoItems.add(taobaoProductBean);
				}

			}
			message.put("total", total);
			resultMap.put("list", taobaoItems);
			result = true;
			message.put("isSuccess", true);
			message.put("message", resultMap);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		} catch (IOException  e) {
			e.printStackTrace();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.淘宝api请求失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.淘宝api请求失败.value);
		}

		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user != null ? user.getAccount(): "","对淘宝的商品发起查询", "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "search()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> buildShare(String taobaoId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_TaobaoServiceImpl-->buildShare():taobaoId="+taobaoId);
		ResponseMap message = new ResponseMap();
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
				message.put("message", promotionLinkBean);
				message.put("isSuccess", true);
			}else{
				message.put("message", "该商品暂时没有佣金，无法生成共享链接！");
			}
			
		} catch (Exception  e) {
			e.printStackTrace();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.淘宝api请求失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.淘宝api请求失败.value);
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"对淘宝的商品ID为", taobaoId, "生成分享链接。", "结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "buildShare()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}


	/*@Override
	public Map<String, Object> transform(JSONObject jo, UserBean user,
												HttpRequestInfoBean request) throws ApiException, WriterException {

		logger.info("S_TaobaoServiceImpl-->transform(): jo="+jo);
		ResponseMap message = new ResponseMap();

		//标题，不能为空，不然无法转发淘口令
		String title = JsonUtil.getStringValue(jo, "title");
		if(StringUtil.isNull(title))
			throw new ParameterUnspecificationException("param title must not null");

		//主图地址，可以为空
		String img = JsonUtil.getStringValue(jo, "img");

		String longLink = JsonUtil.getStringValue(jo, "productUrl");
		if(StringUtil.isNull(longLink))
			throw new ParameterUnspecificationException("param productUrl must not null");

		String longCouponLink = JsonUtil.getStringValue(jo, "couponUrl"); //获取优惠券的地址信息，可以为空
		boolean hasCoupon = StringUtil.isNotNull(longCouponLink);

		//封装查询请求参数
		String[] links = new String[hasCoupon ? 2: 1];
		links[0] = longLink;
		if(hasCoupon)
			links[1] = longCouponLink;

		SpreadApi.ShortLinkResult shortLinkResult = SpreadApi.toShort(links);
		ProductPromotionLinkBean promotionLinkBean = new ProductPromotionLinkBean();
		promotionLinkBean.setClickUrl(longLink);
		promotionLinkBean.setCouponLink(longCouponLink);

		if(shortLinkResult.getTotal() > 0){
			if("OK".equalsIgnoreCase(shortLinkResult.getResults().get(0).getErrCode()))
				promotionLinkBean.setShortLinkUrl(shortLinkResult.getResults().get(0).getContent());
			if(hasCoupon){
				if("OK".equalsIgnoreCase(shortLinkResult.getResults().get(1).getErrCode()))
					promotionLinkBean.setCouponShortLinkUrl(shortLinkResult.getResults().get(1).getContent());
			}

		}else{
			message.put("message", "无法生成共享链接！");
			return message.getMap();
		}

		//获取淘口令
		promotionLinkBean.setTaoToken(TpwdApi.toTpwd(promotionLinkBean.getShortLinkUrl(), img, title));
		if(hasCoupon)
			promotionLinkBean.setCouponLinkTaoToken(TpwdApi.toTpwd(promotionLinkBean.getCouponShortLinkUrl(), img, title));

		promotionLinkBean.setQrCodeUrl(ZXingCodeHandler.createQRCode(promotionLinkBean.getShortLinkUrl(), 100));
		if(hasCoupon)
			promotionLinkBean.setQrCouponCodeUrl(ZXingCodeHandler.createQRCode(promotionLinkBean.getCouponShortLinkUrl(), 100));
		message.put("message", promotionLinkBean);
		message.put("isSuccess", true);
		return message.getMap();
	}*/
}
