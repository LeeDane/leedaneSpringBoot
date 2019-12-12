package com.cn.leedane.controller;

import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mall.pdd.PddException;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.Oauth2Bean;
import com.cn.leedane.service.Oauth2Service;
import com.cn.leedane.utils.ResponseMap;
import com.google.zxing.WriterException;
import com.jd.open.api.sdk.JdException;
import com.taobao.api.ApiException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * 各个平台oauth2校验工具接口controller
 * @author LeeDane
 * 2019年12月6日 下午6:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = "/oauth2")
public class Oauth2Controller extends BaseController {
	private Logger logger = Logger.getLogger(getClass());
	@Autowired
	private UserHandler userHandler;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private Oauth2Service<Oauth2Bean> oauth2Service;

	/**
	 * 第一次授权登录绑定
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/bind/first", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> bind(HttpServletRequest request, Model model) throws ApiException, JdException, PddException, WriterException {
		ResponseMap message = new ResponseMap();
		checkParams(message, request);
		message.putAll(oauth2Service.bindFirst(getJsonFromMessage(message), getUserFromMessage(message), getHttpRequestInfo(request)));
		return message.getMap();
	}
}