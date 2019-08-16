package com.cn.leedane.springboot.controller;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.controller.UserController;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.handler.circle.CirclePostHandler;
import com.cn.leedane.handler.circle.CircleSettingHandler;
import com.cn.leedane.mapper.circle.CircleMemberMapper;
import com.cn.leedane.mapper.circle.CirclePostMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CircleMemberBean;
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.service.circle.CirclePostService;
import com.cn.leedane.service.circle.CircleService;
import com.cn.leedane.service.impl.circle.CircleServiceImpl;
import com.cn.leedane.shiro.CustomAuthenticationToken;
import com.cn.leedane.utils.*;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.authz.UnauthorizedException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
	private CircleSettingHandler circleSettingHandler;
	
	@Autowired
	private CirclePostHandler circlePostHandler;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model, HttpServletRequest request){
		//首页不需要验证是否登录
		return index1(model, request);
	}
	
	@RequestMapping(value= "/", method = RequestMethod.GET)
	public String index1(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入圈子模块", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = getUserFromShiro();
        model.addAttribute("nonav", StringUtil.changeObjectToBoolean(currentUser.getSession().getAttribute("nonav")));
    	//获取页面初始化的信息
    	model.addAllAttributes(circleService.init(user, getHttpRequestInfo(request)));
		//首页不需要验证是否登录
		return loginRoleCheck("circle/index", model, request);
	}
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
	
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String list(Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看圈子列表", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		model.addAttribute("nonav", false);
		return loginRoleCheck("circle/list", true, model, request);
	}
	
	@RequestMapping(value = "/{cid}", method = RequestMethod.GET)
	public String main(@PathVariable(value="cid") int cid, Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入圈子ID:"+ cid, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		CircleBean circle = circleHandler.getCircleBean(cid);
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));

		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = getUserFromShiro();
        /*if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	//获取页面初始化的信息
        	model.addAllAttributes(circleService.main(circle, user, getHttpRequestInfo(request)));
        }*/

		if(user != null)
			//获取页面初始化的信息
			model.addAllAttributes(circleService.main(circle, user, getHttpRequestInfo(request)));
        circleService.saveVisitLog(cid, user, getHttpRequestInfo(request));
        model.addAttribute("nonav", StringUtil.changeObjectToBoolean(currentUser.getSession().getAttribute("nonav")));
		return loginRoleCheck("circle/main", true, model, request);
	}
	
	/**
	 * 圈子设置
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/setting", method = RequestMethod.GET)
	public String setting(@PathVariable(value="circleId") int circleId, Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "进入圈子设置", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		return memberList(circleId, model, request);
	}
	
	/**
	 * 圈子成员列表
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/member-list", method = RequestMethod.GET)
	public String memberList(@PathVariable(value="circleId") int circleId, Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看圈子成员管理界面", "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		CircleBean circle = circleHandler.getCircleBean(circleId);
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		//获取当前的Subject  
        Subject currentUser = SecurityUtils.getSubject();
        UserBean user = getUserFromShiro();
        /*if(currentUser.isAuthenticated()){
        	user = (UserBean) currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
        	//获取页面初始化的信息
        	model.addAllAttributes(circleService.memberListInit(circle, user, getHttpRequestInfo(request)));
        }*/

        if(user != null)
			//获取页面初始化的信息
			model.addAllAttributes(circleService.memberListInit(circle, user, getHttpRequestInfo(request)));

		model.addAttribute("circle", circle);
		model.addAttribute("nonav", StringUtil.changeObjectToBoolean(currentUser.getSession().getAttribute("nonav")));
		return loginRoleCheck("circle/member-list", true, model, request);
	}
	
	@RequestMapping(value = "/{circleId}/write", method = RequestMethod.GET)
	public String write(@PathVariable(value="circleId") int circleId, 
			@RequestParam(value="postId", required = false) Integer postId, 
			Model model, 
			HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "发布帖子，圈子ID:"+ circleId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		//获取当前的Subject 
//        UserBean user = (UserBean) SecurityUtils.getSubject().getSession().getAttribute(UserController.USER_INFO_KEY);
		//获取当前的Subject
		Subject currentUser = SecurityUtils.getSubject();
		UserBean user = getMustLoginUserFromShiro();
		//postId 不为空表示编辑
		CircleBean circle = circleHandler.getNormalCircleBean(circleId, user);
		
		CirclePostBean circlePostBean = new CirclePostBean();
		//circlePostBean.setHasImg(true);
		List<String> imgs = new ArrayList<String>();
		List<String> tags = new ArrayList<String>();
		//imgs.add(ConstantsUtil.DEFAULT_NO_PIC_PATH);
        
    	List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
		CircleMemberBean memberBean = members.get(0);
		
		boolean isCircleAdmin = false;
		if(memberBean.getRoleType() == CircleServiceImpl.CIRCLE_CREATER || memberBean.getRoleType() == CircleServiceImpl.CIRCLE_MANAGER){
			isCircleAdmin = true;
		}
		model.addAttribute("isCircleAdmin", isCircleAdmin); 
		if(postId != null){
			circlePostBean = circlePostHandler.getNormalCirclePostBean(circle, postId, user);
			if(circlePostBean == null || circlePostBean.getCreateUserId() != user.getId())
				throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));

			if(circlePostBean.isHasImg() && StringUtil.isNotNull(circlePostBean.getImgs())){
				imgs = Arrays.asList(circlePostBean.getImgs().split(";"));
			}
			if(StringUtil.isNotNull(circlePostBean.getTag()))
				tags = Arrays.asList(circlePostBean.getTag().split(","));
		}
		
        model.addAttribute("circle", circle);
        model.addAttribute("post", circlePostBean);
        model.addAttribute("imgs", imgs); //这个是有图像的时候转化下的List列表
        model.addAttribute("tags", tags); //这个是有图像的时候转化下的List列表
        model.addAttribute("setting", circleSettingHandler.getNormalSettingBean(circleId, user));
        model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		return loginRoleCheck("circle/write", true, model, request);
	}
	
	/**
	 * 帖子详情
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}", method = RequestMethod.GET)
	public String postDetail(@PathVariable(value="circleId") int circleId, @PathVariable(value="postId") int postId, Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看帖子详情，圈子ID:"+ circleId+ ", 帖子ID:"+ postId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		CircleBean circle = circleHandler.getCircleBean(circleId);
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		CirclePostBean postBean = circlePostHandler.getNormalCirclePostBean(circle, postId);
		model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		return toPostDetail(circle, postBean, model, request);
	}
	
	/**
	 * 帖子详情
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "post/dt/{postId}", method = RequestMethod.GET)
	public String postDetail1(@PathVariable(value="postId") int postId, Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看帖子详情, 帖子ID:"+ postId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		CirclePostBean postBean = circlePostHandler.getNormalCirclePostBean(postId);
		if(postBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		CircleBean circle = circleHandler.getCircleBean(postBean.getCircleId());
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		checkRoleOrPermission("/cc/"+ circle.getId()+ "/post/" + postId);
		model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		return toPostDetail(circle, postBean, model, request);
	}
	
	private String toPostDetail(CircleBean circle, CirclePostBean postBean, Model model, HttpServletRequest request){
		//获取当前的Subject  
        UserBean user = getUserFromShiro();
        model.addAllAttributes(circlePostService.initDetail(circle, postBean, user, getHttpRequestInfo(request)));
        model.addAttribute("audit", false);
        //保存帖子的访问记录
        circlePostService.saveVisitLog(postBean.getId(), user, getHttpRequestInfo(request));
		Subject currentUser = SecurityUtils.getSubject();
        model.addAttribute("nonav", StringUtil.changeObjectToBoolean(currentUser.getSession().getAttribute("nonav")));
		return loginRoleCheck("circle/post-detail", true, model, request);
	}
	
	/**
	 * 帖子详情(审核的时候)
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/{postId}/audit", method = RequestMethod.GET)
	public String postCheckDetail(@PathVariable(value="circleId") int circleId, @PathVariable(value="postId") int postId, Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "审核时查看帖子详情，圈子ID:"+ circleId+ ", 帖子ID:"+ postId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		CircleBean circle = circleHandler.getCircleBean(circleId);
		if(circle == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
		//获取当前的Subject 
        UserBean user = getMustLoginUserFromShiro();
        
		//判断是否是圈子或者圈子管理员
		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(members))
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.请先加入该圈子.value));
		CircleMemberBean memberBean = members.get(0);
		if(memberBean.getRoleType() == CircleServiceImpl.CIRCLE_NORMAL)
			throw new UnauthorizedException();
		
        
        CirclePostBean post = circlePostHandler.getCirclePostBean(postId, user);
		if(post == null || post.getStatus() != ConstantsUtil.STATUS_AUDIT)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		model.addAttribute("circle", circle);
		model.addAttribute("post", post);
		model.addAttribute("create_time", DateUtil.DateToString(post.getCreateTime()));
		model.addAttribute("create_user_account", userHandler.getUserName(post.getCreateUserId()));
		model.addAttribute("create_user_pic_path", userHandler.getUserPicPath(post.getCreateUserId(), "30x30"));
		model.addAttribute("setting", circleSettingHandler.getNormalSettingBean(circle.getId(), user));
		model.addAttribute("audit", true);
        //保存帖子的访问记录
        circlePostService.saveVisitLog(postId, user, getHttpRequestInfo(request));
        model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		return loginRoleCheck("circle/post-detail-audit", true, model, request);
	}
	
	/**
	 * 帖子审核页
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/{circleId}/post/check", method = RequestMethod.GET)
	public String postCheck(@PathVariable(value="circleId") int circleId, Model model, HttpServletRequest request){
		checkRoleOrPermission(model, request);
		operateLogService.saveOperateLog(getUserFromShiro(), getHttpRequestInfo(request), null, "查看帖子审核页面，圈子ID:"+ circleId, "", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.网页端.value);
		UserBean user = getMustLoginUserFromShiro();
		CircleBean circle = circleHandler.getNormalCircleBean(circleId, user);
		model.addAttribute("setting", circleSettingHandler.getNormalSettingBean(circle.getId(), user));
		model.addAttribute("circle", circle);
		model.addAttribute("nonav", StringUtil.changeObjectToBoolean(SecurityUtils.getSubject().getSession().getAttribute("nonav")));
		return loginRoleCheck("circle/post-check", true, model, request);
	}
}
