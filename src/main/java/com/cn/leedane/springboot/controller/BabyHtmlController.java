package com.cn.leedane.springboot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.display.baby.IndexBabyDisplay;
import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.baby.BabyHandler;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.model.baby.BabyBean;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.utils.baby.BabyUtil;

/**
 * 宝宝Html页面的控制器
 * @author LeeDane
 * 2018年5月30日 下午5:03:08
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.baby)
public class BabyHtmlController extends BaseController{
	
	/**
	 * 吃
	 */
	public static final int BABY_TYPE_EAT = 1;
	
	/**
	 * 睡
	 */
	public static final int BABY_TYPE_SLEEP = 2;
	
	/**
	 * 穿
	 */
	public static final int BABY_TYPE_WEAR = 3;
	
	/**
	 * 病
	 */
	public static final int BABY_TYPE_SICK = 4;
	
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	@Autowired
	private BabyHandler babyHandler;
	
	/***
	 * 下面的mapping会导致js/css文件依然访问到templates，返回的是html页面
	 * @param model
	 * @param httpSession
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	public String index(Model model, HttpServletRequest request){
		return index2(0, model, request);
	}
	
	@RequestMapping("/")
	public String index1(Model model, HttpServletRequest request){
		return index2(0, model, request);
	}
	
	@RequestMapping("/{babyId}")
	public String index2(@PathVariable(value="babyId") int babyId, Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);	
		//model.addAttribute("babyName", "小公主");
		
		//首页一进去默认会加载一个默认孩子的信息
		//获取该用户的第一个孩子
		List<BabyBean> babys = babyHandler.getBabys(getUserIdFromShiro());
		if(babyId > 0){
			//获取宝宝信息
			BabyBean baby = babyHandler.getNormalBaby(babyId, getMustLoginUserFromShiro());
			model.addAttribute("infos", BabyUtil.getInfoDisplay(baby));
			model.addAttribute("babyName", baby.getNickname());
		}else{
			if(CollectionUtil.isNotEmpty(babys)){
				BabyBean baby = babys.get(0);
				babyId = baby.getId();
				model.addAttribute("infos", BabyUtil.getInfoDisplay(baby));
				model.addAttribute("babyName", baby.getNickname());
			}
		}
		
		//处理宝宝列表的展示
		if(CollectionUtil.isNotEmpty(babys)){
			List<IndexBabyDisplay> babyDisplays = new ArrayList<IndexBabyDisplay>();
			for(BabyBean baby:  babys){
				IndexBabyDisplay babyDisplay = new IndexBabyDisplay();
				babyDisplay.setPic(StringUtil.isNull(baby.getHeadPic())? ConstantsUtil.DEFAULT_NO_PIC_PATH: baby.getHeadPic());
				babyDisplay.setId(baby.getId());
				babyDisplay.setNickname(baby.getNickname());
				babyDisplay.setCurrent(baby.getId() == babyId);
				babyDisplays.add(babyDisplay);
			}
			model.addAttribute("babys", babyDisplays);
		}
		
		model.addAttribute("babyId", babyId);
		JSONObject jso = new JSONObject();
		Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("0-11-29", "生日");
		mp.put("0-5-31", "跨年");
		mp.put("0-0-1", "工资");
		mp.put("0-6-18", "<span style=\"background:red; color: white;\">爸爸的生日</span>");
		mp.put("2017-8-20", "预发");
		mp.put("2017-8-21", "发布");
		jso.putAll(mp); 
		model.addAttribute("mark", jso);
		return loginRoleCheck("baby/index", true, model, request);
	}
	
	@RequestMapping("/index")
	public String index2(Model model, HttpServletRequest request){
		return index1(model, request);
	}
	
	/**
	 * 添加新的宝宝
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/add")
	public String newBaby(Model model, HttpServletRequest request){
		return editBaby(0, model, request);
	}
	
	/**
	 * 添加新的宝宝
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/{babyId}/manage")
	public String editBaby(@PathVariable(value="babyId") int babyId, Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);	
		
		if(babyId > 0){
			//获取宝宝信息
			BabyBean baby = babyHandler.getNormalBaby(babyId, getMustLoginUserFromShiro());
			model.addAttribute("baby", baby);
			model.addAttribute("babyName", baby.getNickname());
		}else{
			//model.addAttribute("baby", null);
		}
		model.addAttribute("babyId", babyId);
		return loginRoleCheck("baby/new-or-manage", model, request);
	}
	
	/**
	 * 宝宝的设置
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/{babyId}/setting")
	public String setting(@PathVariable(value="babyId") int babyId, Model model, HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);
		//获取宝宝信息
		BabyBean baby = babyHandler.getNormalBaby(babyId, getMustLoginUserFromShiro());
		
		model.addAttribute("baby", baby);
		model.addAttribute("babyName", baby.getNickname());
		
		model.addAttribute("babyId", babyId);
		return loginRoleCheck("baby/setting", model, request);
	}
	
	/**
	 * 添加吃的模板
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/{babyId}/eat")
	public String eat(
			@PathVariable(value="babyId") int babyId,
			@RequestParam(value="edit", required=false) boolean isEdit,
			@RequestParam(value="time", required=false) String time,
			Model model, 
			HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);	
				
		//获取宝宝信息
		BabyBean baby = babyHandler.getBaby(babyId);
		if(baby != null){
			model.addAttribute("babyName", baby.getNickname());
			model.addAttribute("babyId", baby.getId());
			/*if(isEdit && StringUtil.isNotNull(time) && time.length() == 12){
				model.addAttribute("defaultDate", DateUtil.DateToString(DateUtil.stringToDate(time, "yyyyMMddHHmm"), "yyyy-MM-dd"));
				model.addAttribute("defaultTime", DateUtil.DateToString(DateUtil.stringToDate(time, "yyyyMMddHHmm"), "HH:mm"));
			}*/
		}
		
		return loginRoleCheck("baby/eat", model, request);
	}
	
	/**
	 * 添加睡觉的模板
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/{babyId}/sleep")
	public String sleep(
			@PathVariable(value="babyId") int babyId,
			@RequestParam(value="edit", required=false) boolean isEdit,
			@RequestParam(value="time", required=false) String time,
			Model model, 
			HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);	
		//获取宝宝信息
		BabyBean baby = babyHandler.getBaby(babyId);
		if(baby != null){
			model.addAttribute("babyName", baby.getNickname());
			model.addAttribute("babyId", baby.getId());
			/*if(isEdit && StringUtil.isNotNull(time) && time.length() == 12){
				model.addAttribute("defaultDate", DateUtil.DateToString(DateUtil.stringToDate(time, "yyyyMMddHHmm"), "yyyy-MM-dd"));
				model.addAttribute("defaultTime", DateUtil.DateToString(DateUtil.stringToDate(time, "yyyyMMddHHmm"), "HH:mm"));
			}*/
		}
		return loginRoleCheck("baby/sleep", model, request);
	}
	
	/**
	 * 添加洗刷的模板
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/{babyId}/wash")
	public String wash(
			@PathVariable(value="babyId") int babyId,
			@RequestParam(value="edit", required=false) boolean isEdit,
			@RequestParam(value="time", required=false) String time,
			Model model, 
			HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);	
				
		//获取宝宝信息
		BabyBean baby = babyHandler.getBaby(babyId);
		if(baby != null){
			model.addAttribute("babyName", baby.getNickname());
			model.addAttribute("babyId", baby.getId());
			/*if(isEdit && StringUtil.isNotNull(time) && time.length() == 12){
				model.addAttribute("defaultDate", DateUtil.DateToString(DateUtil.stringToDate(time, "yyyyMMddHHmm"), "yyyy-MM-dd"));
				model.addAttribute("defaultTime", DateUtil.DateToString(DateUtil.stringToDate(time, "yyyyMMddHHmm"), "HH:mm"));
			}*/
		}
		return loginRoleCheck("baby/wash", model, request);
	}
	
	/**
	 * 添加生病的模板
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/{babyId}/sick")
	public String sick(
			@PathVariable(value="babyId") int babyId,
			@RequestParam(value="edit", required=false) boolean isEdit,
			@RequestParam(value="time", required=false) String time,
			Model model, 
			HttpServletRequest request){
		//检查权限，通过后台配置
		checkRoleOrPermission(model,request);
				
		//获取宝宝信息
		BabyBean baby = babyHandler.getBaby(babyId);
		if(baby != null){
			model.addAttribute("babyName", baby.getNickname());
			model.addAttribute("babyId", baby.getId());
			/*if(isEdit && StringUtil.isNotNull(time) && time.length() == 12){
				model.addAttribute("defaultDate", DateUtil.DateToString(DateUtil.stringToDate(time, "yyyyMMddHHmm"), "yyyy-MM-dd"));
				model.addAttribute("defaultTime", DateUtil.DateToString(DateUtil.stringToDate(time, "yyyyMMddHHmm"), "HH:mm"));
			}*/
		}
		return loginRoleCheck("baby/sick", model, request);
	}
}
