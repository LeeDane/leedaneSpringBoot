package com.cn.leedane.test;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import org.junit.Test;

import com.cn.leedane.wechat.util.HttpRequestUtil;
import com.cn.leedane.wechat.util.WeixinUtil;

/**
 * 微信的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:31:31
 * Version 1.0
 */
public class WeixinTest extends BaseTest {
	
	@Test
	public void getMediaId() throws KeyManagementException, NoSuchAlgorithmException, NoSuchProviderException, IOException{
		String accessToken =  HttpRequestUtil.getAccessTokenFromWeixin();	
		System.out.println("access_token:--->" + accessToken);
		System.out.println(WeixinUtil.upload("F:/13.jpg", "rcalM3eO_-RNsaeHUfmuLcRG6mq8hGxG3NbRxZUlulvJZ5GWYzC_eaT0zaKhNDjTC4OFYmfwSj7pJFgtFenBqXfEdiRXYfLtu2LK3H_UvQE", "image"));
	}
}
