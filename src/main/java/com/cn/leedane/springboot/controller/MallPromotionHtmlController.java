package com.cn.leedane.springboot.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.controller.UserController;
import com.cn.leedane.handler.mall.S_PromotionAuditHandler;
import com.cn.leedane.handler.mall.S_PromotionUserHandler;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.promotion.S_PromotionAuditBean;
import com.cn.leedane.model.mall.promotion.S_PromotionUserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.ControllerBaseNameUtil;

/**
 * 商城Html页面的控制器
 * @author LeeDane
 * 2017年5月2日 下午5:00:32
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.mall_promotion)
public class MallPromotionHtmlController extends BaseController{
	
	@Autowired
	private UserService<UserBean> userService;
	
	@Autowired
	private S_PromotionUserHandler promotionUserHandler;
	
	@Autowired
	private S_PromotionAuditHandler promotionAuditHandler;
	
	/**
	 * 推广平台入口
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/index")
	public String promotionIndex(Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(request);	
				
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = null;
        //int wishNumber = 0;
        //判断是否开通了推广
        S_PromotionAuditBean promotionAuditBean = null;
        S_PromotionUserBean promotionUserBean = null;
        
        //判断是否申请了审核
        if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	promotionUserBean = promotionUserHandler.getPromotion(user.getId());
        	if(promotionUserBean == null){
        		//判断是否提交了审核
        		promotionAuditBean = promotionAuditHandler.getPromotion(user.getId());
        	}
        }
        model.addAttribute("promotionUserBean", promotionUserBean);
        model.addAttribute("promotionAuditBean", promotionAuditBean);
		return loginRoleCheck("mall/promotion/index", true, model, request);
	}
}
