package com.cn.leedane.mall.taobao.other;

import com.taobao.api.ApiException;
import org.apache.log4j.Logger;

import java.io.IOException;
/*import com.taobao.api.request.TbkItemRecommendGetRequest;
import com.taobao.api.response.TbkItemRecommendGetResponse;*/

/**
 * 获取阿里妈妈的推荐商品
 * @author LeeDane
 * 2017年12月24日 下午8:43:25
 * version 1.0
 */
public class AlimamaUser {
	
	private static Logger logger = Logger.getLogger(AlimamaUser.class);
	
	public static String url = "http://gw.api.taobao.com/router/rest";
	
	/**
	 * 
	 * @param taobaoId
	 * @throws IOException 
	 * @throws ApiException 
	 */
	/*public JSONObject doParse(Long taobaoId) throws ApiException{
		//taobaoId = 549049522944L;
		TaobaoClient client = new DefaultTaobaoClient(url, CommUtil.appkey, CommUtil.secret);
		TbkItemRecommendGetRequest req = new TbkItemRecommendGetRequest();
		req.setFields("num_iid,title,pict_url,small_images,reserve_price,zk_final_price,user_type,provcity,item_url");
		req.setNumIid(taobaoId);
		req.setCount(12L);
		req.setPlatform(1L); //链接形式：1：PC，2：无线，默认：１
		TbkItemRecommendGetResponse rsp = client.execute(req);
		logger.info(rsp.getBody());
		return JSONObject.fromObject(rsp.getBody());
	}*/
	
	/*public static void main(String[] args) {
		TaobaoClient client = new DefaultTaobaoClient(url, CommUtil.appkey, CommUtil.secret);
		TbkScNewuserOrderGetRequest req = new TbkScNewuserOrderGetRequest();
		req.setPageSize(20L);
		req.setAdzoneId(116132925L);
		req.setPageNo(1L);
		req.setSiteId(31996610L);
		TbkScNewuserOrderGetResponse rsp;
		try {
			rsp = client.execute(req, "6100612ca2d35093535c170b4f2ee1efad346634ea10019783677380");
			System.out.println(rsp.getBody());
		} catch (ApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}*/
}
