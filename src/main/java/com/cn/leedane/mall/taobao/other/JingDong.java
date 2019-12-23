package com.cn.leedane.mall.taobao.other;

import java.io.IOException;

/*
import com.jd.open.api.sdk.DefaultJdClient;
import com.jd.open.api.sdk.JdClient;
import com.jd.open.api.sdk.JdException;
import com.jd.open.api.sdk.request.cps.UnionSearchGoodsKeywordQueryRequest;
import com.jd.open.api.sdk.response.cps.UnionSearchGoodsKeywordQueryResponse;
*/

public class JingDong {
	public static final String code = "ZDpsDm";
	public static final String state = "leedane";
	public static final String accessToken = "fa457e43-bcea-4ecd-98e3-c87f285c57da";
	public static final String appSecret = "8af34162ef524bec9fb9238f91a6b11e";
	public static final String SERVER_URL = "https://api.jd.com/routerjson";
	public static final String appKey = "700EF7DB7996B5F9D801384A4A17A42B";
	
	/*{
		  "access_token": "fa457e43-bcea-4ecd-98e3-c87f285c57da",
		  "code": 0,
		  "expires_in": 31535999,
		  "refresh_token": "0da3c606-3239-4dc2-9ebf-236e0e29e0cb",
		  "time": "1514182457114",
		  "token_type": "bearer",
		  "uid": "1898013009",
		  "user_nick": "最爱雨佳"
		}*/
	/**
	 * 执行解析阿里妈妈的平台获取分享的地址、二维码等信息
	 * @param taobaoId
	 * @throws IOException 
	 */
	public static void doParse(String taobaoId) throws IOException{
		/*JdClient client = new DefaultJdClient(SERVER_URL,accessToken, appKey,appSecret);

		UnionSearchGoodsCategoryQueryRequest request=new UnionSearchGoodsCategoryQueryRequest();

		request.setParentId( 123 );
		request.setGrade( 123 );

		try {
			UnionSearchGoodsCategoryQueryResponse response=client.execute(request);
			System.out.println(response.getMsg());
		} catch (JdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		/*JdClient client=new DefaultJdClient(SERVER_URL,accessToken,appKey,appSecret);

		ServicePromotionContentGetcodeRequest request=new ServicePromotionContentGetcodeRequest();

		request.setReleaseType( "2" );
		//request.setTypeId( "jingdong" );
		request.setSortName( "publishTimeLS" );
		request.setSort( "asc" );
		request.setPageSize( 2 );
		request.setPageIndex( 123 );
		request.setUnionId(860725442L);
		request.setSubUnionId( "jingdong" );
		request.setWebId( "jingdong" );
		//request.setExt1( "jingdong" );
		//request.setProtocol( 123 );
		request.setPositionId( 899490082L );

		
		try {
			ServicePromotionContentGetcodeResponse response=client.execute(request);
			System.out.println(response.getMsg());
		} catch (JdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		

		/*JdClient client=new DefaultJdClient(SERVER_URL,accessToken,appKey,appSecret);
		
		UnionSearchGoodsKeywordQueryRequest request=new UnionSearchGoodsKeywordQueryRequest();
		
		//request.setCat1Id( 123 );
		//request.setCat2Id( 123 );
		//request.setCat3Id( 123 );
		request.setKeyword( "小米" );
		request.setPageIndex( 0 );
		request.setPageSize( 10 );
		//request.setSortName( "pcCommission" );
		//request.setSort( "asc" );
		try {
			UnionSearchGoodsKeywordQueryResponse response=client.execute(request);
			System.out.println(response.getMsg());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	/*public static void main(String[] args) {
		try {
			doParse("ll");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/
}
