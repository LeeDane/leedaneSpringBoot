package com.cn.leedane.springboot.controller;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mall.github.GithubException;
import com.cn.leedane.mall.jingdong.api.AccessTokenUtilApi;
import com.cn.leedane.mall.pdd.CommUtil;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.shiro.CustomAuthenticationToken;
import com.cn.leedane.utils.*;
import com.google.common.base.Splitter;
import com.jd.open.api.sdk.JdException;
import com.pdd.pop.sdk.http.token.AccessTokenResponse;
import com.taobao.api.ApiException;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.yaml.snakeyaml.util.UriEncoder;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 各个平台oauth2校验工具接口controller
 * @author LeeDane
 * 2019年12月6日 下午6:30:40
 * version 1.0
 */
@RequestMapping("/oauth2")
@Controller
public class Oauth2HtmlController extends BaseController {
	private Logger logger = Logger.getLogger(getClass());
	public static final String secretKey = "11HDddESaAhiHgDz";

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private UserMapper userMapper;

	/**
	 * 跳转到授权登录页面
	 * @param platform
	 * @param oldUrl 需要先encodeURI后再base64处理一遍
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/login/{platform}")
	public String oauth2Login(@PathVariable("platform") String platform, @RequestParam("url")String oldUrl , Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		UserBean user = getUserFromShiro();
		//当前用户已经登录，直接跳转到首页
		if(user != null){
			return "redirect:/index";
		}

		String redirectUrl = "redirect:/404";
		Map<String, Object> stateMap = new HashMap<>();
		Calendar calendar2 = Calendar.getInstance();
		calendar2.add(Calendar.MINUTE, 10); //10分钟过期
		stateMap.put("type", EnumUtil.Oauth2Type.登录.value);
		stateMap.put("end", DateUtil.DateToString(new Date(calendar2.getTimeInMillis())));
		stateMap.put("url", oldUrl);
		String cipherText = CommonUtil.sm4Encrypt(stateMap);
		if(EnumUtil.ProductPlatformType.淘宝.value.equalsIgnoreCase(platform)){
			redirectUrl = "redirect:https://oauth.taobao.com/authorize?response_type=code&client_id="+ com.cn.leedane.mall.taobao.CommUtil.appkey +"&redirect_uri="+ com.cn.leedane.mall.taobao.CommUtil.getRedirectUrl() +"&state="+ cipherText +"&view=web";
		}else if(EnumUtil.ProductPlatformType.京东.value.equalsIgnoreCase(platform)){
			redirectUrl =  "redirect:https://open-oauth.jd.com/oauth2/to_login?app_key="+ com.cn.leedane.mall.jingdong.CommUtil.appkey +"&response_type=code&redirect_uri="+ com.cn.leedane.mall.jingdong.CommUtil.getRedirectUrl() +"&state="+ cipherText +"&scope=snsapi_base";
		}else if(EnumUtil.ProductPlatformType.拼多多.value.equalsIgnoreCase(platform)){
			redirectUrl = "redirect:https://ddjb.pinduoduo.com/open.html?client_id="+ CommUtil.clientId +"&response_type=code&redirect_uri="+ com.cn.leedane.mall.pdd.CommUtil.getRedirectUrl()+"&state="+ cipherText;
		}else if(EnumUtil.ProductPlatformType.github.value.equalsIgnoreCase(platform)){
			redirectUrl =  "redirect:https://github.com/login/oauth/authorize?client_id="+ com.cn.leedane.mall.github.CommUtil.client_id +"&redirect_uri="+ com.cn.leedane.mall.github.CommUtil.getRedirectUrl() +"&state="+ cipherText;
		}
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "打开"+ platform +"授权登录页面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return redirectUrl;
	}


	/**
	 * 淘宝授权服务后的回调地址
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/taobao/verify", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ModelAndView taobao(HttpServletRequest request, Model model) throws ApiException, JdException, PddException, GithubException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return verify(message, EnumUtil.ProductPlatformType.淘宝);
	}


	/**
	 * 京东授权服务后的回调地址
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/jingdong/verify", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ModelAndView jingdong(HttpServletRequest request, Model model) throws JdException, ApiException, PddException, GithubException {
		ResponseMap message = new ResponseMap();
		logger.info("进入授权校验界面0");
		checkParams(message, request);
		logger.info("进入授权校验界面1");
		return verify(message, EnumUtil.ProductPlatformType.京东);
	}

	/**
	 * github授权服务后的回调地址
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/github/verify", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ModelAndView github(HttpServletRequest request, Model model) throws JdException, ApiException, PddException, GithubException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return verify(message, EnumUtil.ProductPlatformType.github);
	}

	/**
	 * 拼多多授权服务后的回调地址
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/pinduoduo/verify", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ModelAndView pinduoduo(HttpServletRequest request, Model model) throws PddException, JdException, ApiException, GithubException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		return verify(message, EnumUtil.ProductPlatformType.拼多多);
	}

	/**
	 * 各个平台公共校验方法
	 * @param message
	 * @return
	 */
	private ModelAndView verify(ResponseMap message, EnumUtil.ProductPlatformType platformType) throws JdException, ApiException, PddException, GithubException {
		logger.info("进入授权校验界面");
		UserBean user = getUserFromMessage(message);
		JSONObject json = getJsonFromMessage(message);
		ModelAndView modelAndView = null;
		String state = JsonUtil.getStringValue(json, "state");
		JSONObject plainObject = CommonUtil.sm4Decrypt(state);
		String code = JsonUtil.getStringValue(json, "code");
		ParameterUnspecificationUtil.checkNullString(state, "平台返回的地址没有state信息，请重新授权或联系管理员处理。");
		ParameterUnspecificationUtil.checkNullString(code, "平台返回的地址没有code信息，请重新授权或联系管理员处理。");
		logger.debug("进入授权校验界面参数校验通过");
		Date endTime = DateUtil.stringToDate(plainObject.optString("end"));
		String redirectUrl = plainObject.optString("url"); //成功后跳转回去的链接
		logger.debug("进入授权校验界面， ："+ redirectUrl);
		//将链接从base64位转化回来
		redirectUrl = new String(Base64Util.decode(redirectUrl.toCharArray()));
		if(endTime.getTime() < System.currentTimeMillis()){
			logger.debug("本次授权已经过期啦："+ plainObject.toString());
			modelAndView = new ModelAndView("redirect:/lg?errorcode="+ EnumUtil.ResponseCode.页面授权已经过期.value +"&ref="+ redirectUrl +"&t="+ UUID.randomUUID().toString());
			modelAndView.addObject("message","本次授权链接已经过期，请重新授权。");
			modelAndView.addObject("isSuccess","false");
			return modelAndView;
		}
		//登录验证
		if(plainObject.optInt("type") == EnumUtil.Oauth2Type.登录.value){
			logger.debug("进入授权校验界面， 进入登录类型");
			if(user != null){
				modelAndView = new ModelAndView("redirect:" + redirectUrl);
				return modelAndView;
			}
			logger.debug("进入授权校验界面， 进入登录类型， 用户为空");
			//获取access_token,从其中获取唯一绑定的id
			long oauth2Id = 0L;
			String openId = null;
			String name = null;
			if(platformType == EnumUtil.ProductPlatformType.淘宝){
				JSONObject accessTokenObject = com.cn.leedane.mall.taobao.api.AccessTokenUtilApi.getAccessToken(code, state);
				oauth2Id = accessTokenObject.optLong("taobao_user_id");
				openId = accessTokenObject.optString("taobao_open_uid");
				name = UriEncoder.decode(accessTokenObject.optString("taobao_user_nick"));//需要解码中文
			}else if(platformType == EnumUtil.ProductPlatformType.京东){
				JSONObject accessTokenObject = com.cn.leedane.mall.jingdong.api.AccessTokenUtilApi.getAccessToken(code);
				oauth2Id = accessTokenObject.optLong("uid");
				openId = accessTokenObject.optString("open_id");
			}else if(platformType == EnumUtil.ProductPlatformType.拼多多){
				AccessTokenResponse accessTokenObject = com.cn.leedane.mall.pdd.api.AccessTokenUtilApi.getAccessToken(code);
				oauth2Id = StringUtil.changeObjectToLong(accessTokenObject.getOwnerId());
				openId = accessTokenObject.getOwnerName();
				name =  accessTokenObject.getOwnerName();
			}else if(platformType == EnumUtil.ProductPlatformType.github){
				JSONObject accessTokenObject = com.cn.leedane.mall.github.api.AccessTokenUtilApi.getAccessToken(code);
				oauth2Id = accessTokenObject.optLong("id");
				openId = accessTokenObject.optString("node_id");
				name = accessTokenObject.optString("login");
			}

			//跟库中用户做比较，找到用户信息
			user = userMapper.loginUserByOauth2Id(oauth2Id, openId, platformType.value);
			if(user != null) {
				userHandler.loginUser(user);
				modelAndView = new ModelAndView("redirect:" + redirectUrl);
				return modelAndView;
			}else{
				//没有找到用户，说明是第一次，跳转到引导用户注册页面
				modelAndView = new ModelAndView("redirect:/oauth2/bindFirst");//跳转到第一次授权绑定页面
				Map<String, Object> stateMap = new HashMap<>();
				stateMap.put("oauth2Id", oauth2Id);
				stateMap.put("openId", openId);
				stateMap.put("name", StringUtil.changeNotNull(name));
				stateMap.put("platform", platformType.value);
				Calendar calendar2 = Calendar.getInstance();
				calendar2.add(Calendar.MINUTE, 10); //10分钟过期
				stateMap.put("end", DateUtil.DateToString(new Date(calendar2.getTimeInMillis())));
				String cipherText = CommonUtil.sm4Encrypt(stateMap);
				modelAndView.addObject("params", cipherText);
				return modelAndView;
			}
		}else if(plainObject.optInt("type") == EnumUtil.Oauth2Type.绑定.value){
			logger.debug("进入授权校验界面， 进入绑定类型");
			if(user == null){
				modelAndView = new ModelAndView("redirect:/lg?errorcode="+ EnumUtil.ResponseCode.请先登录.value +"&ref="+ redirectUrl +"&t="+ UUID.randomUUID().toString());
				return modelAndView;
			}

			//获取access_token,从其中获取唯一绑定的id


			//跟当前用户绑定

			//返回成功信息

		}
		return null;
	}

	/**
	 * 空指针异常
	 * @param model
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	@RequestMapping("/bindFirst")
	public String bindFirstOauth2(Model model, HttpServletRequest request) throws UnsupportedEncodingException{
		String params = request.getParameter("params");
		model.addAttribute("params", params);
		model.addAttribute("publicKey", RSAKeyUtil.getInstance().getPublicKey());
		return loginRoleCheck("bind-first-oauth2", false, model, request);
	}
}