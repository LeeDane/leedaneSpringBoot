package com.cn.leedane.service.impl;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.mapper.Oauth2Mapper;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.Oauth2Service;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.shiro.CustomAuthenticationToken;
import com.cn.leedane.utils.*;
import com.google.zxing.WriterException;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.annotation.Resource;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Date;
import java.util.Map;

/**
 * 淘宝商品的service的实现类
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Service("oauth2Service")
public class Oauth2ServiceImpl implements Oauth2Service<Oauth2Bean> {
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private Oauth2Mapper oauth2Mapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	protected UserService<UserBean> userService;

	@Autowired
	protected UserMapper userMapper;

	@Autowired
	private UserHandler userHandler;

	@Override
	public Map<String, Object> bindFirst(JSONObject json, UserBean user,
										 HttpRequestInfoBean request) throws WriterException, JdException, ApiException, PddException {
		logger.info("Oauth2ServiceImpl-->bindFirst():json="+json);
		ResponseMap message = new ResponseMap();
		//
		String params = JsonUtil.getStringValue(json, "iparams");
		String account = JsonUtil.getStringValue(json, "account");
		String password = JsonUtil.getStringValue(json, "password");
		String confirmPassword = JsonUtil.getStringValue(json, "confirmPassword");
		String phone = JsonUtil.getStringValue(json, "mobilePhone");
		ParameterUnspecificationUtil.checkNullString(params, "params must not null.");
		ParameterUnspecificationUtil.checkNullString(account, "account must not null.");
		ParameterUnspecificationUtil.checkNullString(password, "password must not null.");


		//解析获取明文
		JSONObject plainObject = CommonUtil.sm4Decrypt(params);
		Date endTime = DateUtil.stringToDate(plainObject.optString("end"));
		if(endTime.getTime() < System.currentTimeMillis()){
			message.put("message","本页面授权链接已经过期，请重新授权。");
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			return message.getMap();
		}

		//解密密码
		try {
			byte[] decodedData1 = RSACoder.decryptByPrivateKey(password, RSAKeyUtil.getInstance().getPrivateKey());
			password = new String(decodedData1, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		//先尝试登录用户，登录失败再注册用户
		if(StringUtil.isNull(confirmPassword) && StringUtil.isNull(phone)){
			user = userMapper.loginUser(account, MD5Util.compute(password));
			if(user == null){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户名或者密码不匹配.value));
				message.put("responseCode", EnumUtil.ResponseCode.用户名或者密码不匹配.value);
				return message.getMap();
			}

			//进行授权记录保存
			Oauth2Bean oauth2Bean = new Oauth2Bean();
			oauth2Bean.setStatus(ConstantsUtil.STATUS_NORMAL);
			oauth2Bean.setCreateUserId(user.getId());
			oauth2Bean.setCreateTime(new Date());
			oauth2Bean.setOauth2Id(JsonUtil.getLongValue(plainObject, "oauth2Id"));
			oauth2Bean.setOpenId(JsonUtil.getStringValue(plainObject, "openId"));
			oauth2Bean.setPlatform(JsonUtil.getStringValue(plainObject, "platform"));
			oauth2Bean.setName(JsonUtil.getStringValue(plainObject, "name"));

			int res = 0;
			try{
				res = oauth2Mapper.save(oauth2Bean);
			}catch (DuplicateKeyException e){
				message.put("message","该用户在"+ JsonUtil.getStringValue(plainObject, "platform") +"平台已经绑定过，请切换账号或直接登录后解绑再操作。");
				message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
				return message.getMap();
			}
			if(res > 0){
				//登录用户loginUser
				userHandler.loginUser(user);
				message.put("message","绑定账号成功，请登录！");
				message.put("isSuccess", true);
				return message.getMap();
			}

			message.put("message", "绑定失败，请稍后重试！");
			message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
			return message.getMap();
		}

		ParameterUnspecificationUtil.checkNullString(confirmPassword, "confirmPassword must not null.");
		ParameterUnspecificationUtil.checkNullString(phone, "phone must not null.");
		message.putAll(userService.registerByPhoneNoValidate(json, request));
		boolean success = StringUtil.changeObjectToBoolean(message.get("isSuccess"));
		if(success) {
			//获取用户信息
			user = userMapper.loginUser(account, MD5Util.compute(password));
			if(user != null){
				Oauth2Bean oauth2Bean = new Oauth2Bean();
				oauth2Bean.setStatus(ConstantsUtil.STATUS_NORMAL);
				oauth2Bean.setCreateUserId(user.getId());
				oauth2Bean.setCreateTime(new Date());
				oauth2Bean.setOauth2Id(JsonUtil.getLongValue(plainObject, "oauth2Id"));
				oauth2Bean.setOpenId(JsonUtil.getStringValue(plainObject, "openId"));
				oauth2Bean.setPlatform(JsonUtil.getStringValue(plainObject, "platform"));
				oauth2Bean.setName(JsonUtil.getStringValue(plainObject, "name"));
				int res = 0;
				try{
					res = oauth2Mapper.save(oauth2Bean);
				}catch (DuplicateKeyException e){
					message.put("message","该用户在"+ JsonUtil.getStringValue(plainObject, "platform") +"平台已经绑定过，请切换账号或直接登录后解绑再操作。");
					message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
					return message.getMap();
				}
				if(res > 0){
					//登录用户loginUser
					userHandler.loginUser(user);
					message.put("message","绑定账号成功，请登录！");
					message.put("isSuccess", true);
					return message.getMap();
				}
			}else{
				message.put("message","用户注册后无法读取用户信息，请稍后重试！");
				message.put("responseCode", EnumUtil.ResponseCode.操作失败.value);
				return message.getMap();
			}
		}
		return message.getMap();
	}

/*
	public static void main(String[] args) throws UnsupportedEncodingException {
		String s = "";

		System.out.println(UriEncoder.decode(s));
		byte[] b = s.getBytes();//编码
		String sa = new String(b, "GBK");//解码:用什么字符集编码就用什么字符集解码
		System.out.println(sa);

	}*/
}
