package com.cn.leedane.service.impl.shop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.handler.shop.S_ProductHandler;
import com.cn.leedane.mapper.shop.S_ProductMapper;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.shop.S_BigEventBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.shop.S_BigEventService;
import com.cn.leedane.service.shop.S_TaobaoService;
import com.cn.leedane.taobao.api.Alimama;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.PlatformType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

/**
 * 淘宝商品的service的实现类
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Service("S_TaobaoService")
public class S_TaobaoServiceImpl extends AdminRoleCheckService implements S_TaobaoService<IDBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private S_ProductHandler productHandler;
	
	@Autowired
	private S_ProductMapper productMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private S_BigEventService<S_BigEventBean> bigEventService;
	
	/*@Override
	public Map<String, Object> search(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		
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

	@Override
	public Map<String, Object> search(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		
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
			dataMap.put("perPageSize", rows +"");
			dataMap.put("shopTag", "yxjh");
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
			message.put("total", resultData.getJSONObject("paginator").getInt("items"));
			List<TaobaoProductBean> taobaoItems = new ArrayList<TaobaoProductBean>();
			JSONArray pageList = resultData.getJSONArray("pageList");
			for(int i = 0; i < pageList.size(); i++){
				TaobaoProductBean taobaoProductBean = new TaobaoProductBean();
				taobaoProductBean.setAuctionId(pageList.getJSONObject(i).getLong("auctionId"));
				taobaoProductBean.setCashBack(pageList.getJSONObject(i).getDouble("tkCommFee"));
				taobaoProductBean.setCashBackRatio(pageList.getJSONObject(i).getDouble("tkRate"));
				taobaoProductBean.setImg(pageList.getJSONObject(i).getString("pictUrl"));
				taobaoProductBean.setPlatform(EnumUtil.ProductPlatformType.淘宝.value);
				taobaoProductBean.setPrice(pageList.getJSONObject(i).getDouble("zkPrice"));
				taobaoProductBean.setTitle(pageList.getJSONObject(i).getString("title"));
				taobaoProductBean.setShopTitle(pageList.getJSONObject(i).getString("shopTitle"));
				taobaoItems.add(taobaoProductBean);
			}
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
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user != null ? user.getAccount(): "","对淘宝的商品发起查询", "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "search()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}
	
	public class TaobaoProductBean extends IDBean{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private String title; //标题
		private String img; //主图片(取第一张)
		private double cashBackRatio; //比率
		private double cashBack; // 佣金
		private double price; //当前的价格
		private long auctionId; //唯一id
		private String platform; //平台
		private String shopTitle; //商铺名称
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getImg() {
			return img;
		}
		public void setImg(String img) {
			this.img = img;
		}
		public double getCashBackRatio() {
			return cashBackRatio;
		}
		public void setCashBackRatio(double cashBackRatio) {
			this.cashBackRatio = cashBackRatio;
		}
		public double getCashBack() {
			return cashBack;
		}
		public void setCashBack(double cashBack) {
			this.cashBack = cashBack;
		}
		public double getPrice() {
			return price;
		}
		public void setPrice(double price) {
			this.price = price;
		}
		public long getAuctionId() {
			return auctionId;
		}
		public void setAuctionId(long auctionId) {
			this.auctionId = auctionId;
		}
		public String getPlatform() {
			return platform;
		}
		public void setPlatform(String platform) {
			this.platform = platform;
		}
		public String getShopTitle() {
			return shopTitle;
		}
		public void setShopTitle(String shopTitle) {
			this.shopTitle = shopTitle;
		}
		
	}
	
	@Override
	public Map<String, Object> buildShare(String taobaoId, UserBean user,
			HttpServletRequest request) {
		
		logger.info("S_TaobaoServiceImpl-->buildShare():taobaoId="+taobaoId);
		ResponseMap message = new ResponseMap();
		try {
			Alimama alimama = new Alimama();
			JSONObject dataJson = alimama.doParse(taobaoId).getJSONObject("data");
			TaobaoShareLinkBean shareLinkBean = new TaobaoShareLinkBean();
			shareLinkBean.setClickUrl(JsonUtil.getStringValue(dataJson, "clickUrl"));
			shareLinkBean.setCouponLink(JsonUtil.getStringValue(dataJson, "couponLink"));
			shareLinkBean.setCouponLinkTaoToken(JsonUtil.getStringValue(dataJson, "couponLinkTaoToken"));
			shareLinkBean.setCouponShortLinkUrl(JsonUtil.getStringValue(dataJson, "couponShortLinkUrl"));
			shareLinkBean.setQrCodeUrl(JsonUtil.getStringValue(dataJson, "qrCodeUrl"));
			shareLinkBean.setShortLinkUrl(JsonUtil.getStringValue(dataJson, "shortLinkUrl"));
			shareLinkBean.setTaoToken(JsonUtil.getStringValue(dataJson, "taoToken"));
			message.put("isSuccess", true);
			message.put("message", shareLinkBean);
		} catch (Exception  e) {
			e.printStackTrace();
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.淘宝api请求失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.淘宝api请求失败.value);
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"对淘宝的商品ID为", taobaoId, "生成分享链接。", "结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "buildShare()", ConstantsUtil.STATUS_NORMAL, 0);
		return message.getMap();
	}
	
	/**
	 * 淘宝共享链接的实体bean
	 * @author LeeDane
	 * 2017年12月7日 下午12:16:36
	 * version 1.0
	 */
	public class TaobaoShareLinkBean extends IDBean{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		
		private String shortLinkUrl; //短链接
		
		private String couponShortLinkUrl; //领券短链接
		
		private String qrCodeUrl; //二维码图片地址
		
		private String clickUrl; //长链接的地址
		
		private String couponLink; //领券长链接地址
		
		private String taoToken; //淘口令
		
		private String couponLinkTaoToken; //领券淘口令

		public String getShortLinkUrl() {
			return shortLinkUrl;
		}

		public void setShortLinkUrl(String shortLinkUrl) {
			this.shortLinkUrl = shortLinkUrl;
		}

		public String getCouponShortLinkUrl() {
			return couponShortLinkUrl;
		}

		public void setCouponShortLinkUrl(String couponShortLinkUrl) {
			this.couponShortLinkUrl = couponShortLinkUrl;
		}

		public String getQrCodeUrl() {
			return qrCodeUrl;
		}

		public void setQrCodeUrl(String qrCodeUrl) {
			this.qrCodeUrl = qrCodeUrl;
		}

		public String getClickUrl() {
			return clickUrl;
		}

		public void setClickUrl(String clickUrl) {
			this.clickUrl = clickUrl;
		}

		public String getCouponLink() {
			return couponLink;
		}

		public void setCouponLink(String couponLink) {
			this.couponLink = couponLink;
		}

		public String getTaoToken() {
			return taoToken;
		}

		public void setTaoToken(String taoToken) {
			this.taoToken = taoToken;
		}

		public String getCouponLinkTaoToken() {
			return couponLinkTaoToken;
		}

		public void setCouponLinkTaoToken(String couponLinkTaoToken) {
			this.couponLinkTaoToken = couponLinkTaoToken;
		}
		
	}
}
