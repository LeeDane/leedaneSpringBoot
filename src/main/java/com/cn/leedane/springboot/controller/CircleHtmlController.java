package com.cn.leedane.springboot.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.controller.UserController;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.handler.circle.CirclePostHandler;
import com.cn.leedane.mapper.circle.CircleMemberMapper;
import com.cn.leedane.mapper.circle.CirclePostMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CircleMemberBean;
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.service.circle.CirclePostService;
import com.cn.leedane.service.circle.CircleService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.RelativeDateFormat;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;

/**
 * 圈子Html页面的控制器
 * @author LeeDane
 * 2017年5月31日 上午10:27:00
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.cc)
public class CircleHtmlController extends BaseController{
	
	@Autowired
	private UserService<UserBean> userService;
	
	@Autowired
	private CircleService<CircleBean> circleService;
	
	@Autowired
	private CirclePostService<CirclePostBean> circlePostService;
	
	@Autowired
	private CircleMemberMapper circleMemberMapper;
	
	@Autowired
	private CirclePostMapper circlePostMapper;
	
	@Autowired
	private CircleHandler circleHandler;
	
	@Autowired
	private CirclePostHandler circlePostHandler;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@RequestMapping()
	public String index(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		return index1(model, request);
	}
	
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = null;
        if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        }
    	//获取页面初始化的信息
    	model.addAllAttributes(circleService.init(user, request));
		//首页不需要验证是否登录
		return loginRoleCheck("circle/index", model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
	
	@RequestMapping("/list")
	public String list(Model model, HttpServletRequest request){
		return loginRoleCheck("circle/list", true, model, request);
	}
	
	@RequestMapping("/circle/{cid}")
	public String main(@PathVariable(value="cid") int cid, Model model, HttpServletRequest request){
		
		CircleBean circle = circleHandler.getCircleBean(cid);
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = null;
        if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	//获取页面初始化的信息
        	model.addAllAttributes(circleService.main(circle, user, request));
        }
        
        circleService.saveVisitLog(cid, user, request);
		return loginRoleCheck("circle/main", true, model, request);
	}
	
	/**
	 * 圈子成员列表
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/{circleId}/member-list")
	public String memberList(@PathVariable(value="circleId") int circleId, Model model, HttpServletRequest request){
		CircleBean circle = circleHandler.getCircleBean(circleId);
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = null;
        if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	//获取页面初始化的信息
        	model.addAllAttributes(circleService.memberListInit(circle, user, request));
    		
        }
		model.addAttribute("circle", circle);
		return loginRoleCheck("circle/member-list", true, model, request);
	}
	
	@RequestMapping("/{circleId}/write")
	public String write(@PathVariable(value="circleId") int circleId, 
			@RequestParam(value="postId", required = false) Integer postId, 
			Model model, 
			HttpServletRequest request){
		//postId 不为空表示编辑
		CircleBean circle = circleHandler.getNormalCircleBean(circleId);
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		CirclePostBean circlePostBean = new CirclePostBean();
		//circlePostBean.setHasImg(true);
		List<String> imgs = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		//imgs.add(ConstantsUtil.DEFAULT_NO_PIC_PATH);
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = null;
        if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
    		if(!SqlUtil.getBooleanByList(members))
    			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
    		
    		if(postId != null){
    			circlePostBean = circlePostHandler.getCirclePostBean(circle, postId, user);
    			if(circlePostBean == null || circlePostBean.getCreateUserId() != user.getId())
    				throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));

    			if(circlePostBean.isHasImg() && StringUtil.isNotNull(circlePostBean.getImgs())){
    				imgs = Arrays.asList(circlePostBean.getImgs().split(";"));
    			}
    			if(StringUtil.isNotNull(circlePostBean.getTag()))
    				tags = Arrays.asList(circlePostBean.getTag().split(","));
    		}
        }
        
        model.addAttribute("circle", circle);
        model.addAttribute("post", circlePostBean);
        model.addAttribute("imgs", imgs); //这个是有图像的时候转化下的List列表
        model.addAttribute("tags", tags); //这个是有图像的时候转化下的List列表
		return loginRoleCheck("circle/write", true, model, request);
	}
	
	/**
	 * 帖子详情
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/circle/{circleId}/post/{postId}")
	public String postDetail(@PathVariable(value="circleId") int circleId, @PathVariable(value="postId") int postId, Model model, HttpServletRequest request){
		CircleBean circle = circleHandler.getCircleBean(circleId);
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		CirclePostBean postBean = circlePostHandler.getCirclePostBean(circle, postId);
		if(postBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = null;
        if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        }
        model.addAllAttributes(circlePostService.initDetail(circle, postBean, user, request));
        
        //保存帖子的访问记录
        circlePostService.saveVisitLog(postId, user, request);
		return loginRoleCheck("circle/post-detail", true, model, request);
	}
}
