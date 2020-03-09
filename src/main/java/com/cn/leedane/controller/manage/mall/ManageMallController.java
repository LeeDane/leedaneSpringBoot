package com.cn.leedane.controller.manage.mall;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.service.manage.ManageMallService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.ResponseModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 商城全局搜索统一入口接口controller
 * @author LeeDane
 * 2019年11月12日 下午6:30:40
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.myManage)
public class ManageMallController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ManageMallService<IDBean> manageMallService;

	/**
	 * 推广位的申请
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mall/promotion/apply", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel thirdUnBind(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getModel();
		}
		return manageMallService.promotionApply(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 生成推荐码
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mall/referrer/code", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel buildReferrerCode(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getModel();
		}
		return manageMallService.buildReferrerCode(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 绑定推荐人
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mall/referrer/bind", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public ResponseModel bindReferrer(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getModel();
		}
		return manageMallService.bindReferrer(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}

	/**
	 * 获取推荐关系
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/mall/referrer/relation", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public ResponseModel referrerRelation(HttpServletRequest request) throws Exception {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getModel();
		}
        /*CheckTicket checkTicket = new CheckTicket();
		System.out.println(checkTicket.check(getUserFromShiro().getAccount()));*/
		return manageMallService.referrerRelation(getJsonFromMessage(message), getMustLoginUserFromShiro(), getHttpRequestInfo(request));
	}
}
