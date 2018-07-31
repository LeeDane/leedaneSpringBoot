package com.cn.leedane.test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONObject;

import org.junit.Test;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.SignInBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.SignInService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 签到相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:29:19
 * Version 1.0
 */
public class SignInTest extends BaseTest {
	
	
	@Resource
	private SignInService<SignInBean> signInService;
	
	@Resource
	private UserService<UserBean> userService;

	/**
	 * 缓存
	 */
	private SystemCache systemCache;
	
	/**
	 * 是否签到
	 */
	@Test
	public void isSignIn(){
		logger.info(signInService.isSign(1, DateUtil.DateToString(new Date(), "yyyy-MM-dd")));
	}
	
	/**
	 * 签到
	 */
	@Test
	public void save(){
		SignInBean bean = new SignInBean();
		//int score = 1;
		int continuous = 0; //连续天数
		List<Map<String, Object>> parents = signInService.getNewestRecore(1);
		if(parents != null && parents.size() > 0){
			bean.setPid(StringUtil.changeObjectToInt(parents.get(0).get("id")));
			
			//昨天是否签到
			if(DateUtil.DateToString(DateUtil.objectToDate(parents.get(0).get("create_time")), "yyyy-MM-dd").equals(DateUtil.DateToString(DateUtil.getYestoday(), "yyyy-MM-dd"))){
				
				//根据连续天数计算积分
				//score = StringUtil.getScoreBySignin(StringUtil.changeObjectToInt(parents.get(0).get("continuous")), StringUtil.changeObjectToInt(parents.get(0).get("score")));
				continuous = StringUtil.changeObjectToInt(parents.get(0).get("continuous")) + 1;		
			}else{
				//score = StringUtil.changeObjectToInt(parents.get(0).get("score")) + 1;
			}
		}
		
		//bean.setScore(score);
		Date currentTime = new Date();
		bean.setCreateTime(currentTime);
		bean.setContinuous(continuous);
		bean.setCreateDate(DateUtil.DateToString(currentTime, "yyyy-MM-dd"));
		systemCache = (SystemCache) SpringUtil.getBean("systemCache");
		String adminId = (String) systemCache.getCache("admin-id");
		int aid = 1;
		if(!StringUtil.isNull(adminId)){
			aid = Integer.parseInt(adminId);
		}
		UserBean user = userService.findById(aid);
		
		if(user != null)
			bean.setCreateUserId(user.getId());
		bean.setStatus(1);		
		//logger.info("签到："+signInService.saveSignIn(jo, user, request)(bean,1));
	}
	

	/**
	 * 获取最新一条签到记录
	 */
	@Test
	public void getNewestRecore(){
		logger.info(signInService.getNewestRecore(1).toString());
	}
	
	@Test
	public void saveSignIn(){
		
		UserBean user = userService.findById(1);
		String str = "{\"account\":\"leedane\",\"login_mothod\":\"android\",\"no_login_code\":\"14480951808066e31568670e51be42bc7978cc2066ea060.521926355594616\"}";
		JSONObject jo = JSONObject.fromObject(str);
		try {
			boolean isSave = signInService.saveSignIn(jo, user, null);
			logger.info("isSave:" +isSave);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void getSignInByLimit(){
		UserBean user = userService.findById(1);
		String str = "{'uid':1, 'pageSize':5,'timeScope':2, 'start_date': '2016-01-18', 'end_date':'2016-01-18'}";
		JSONObject jo = JSONObject.fromObject(str);
		try {
			List<Map<String, Object>> list = signInService.getSignInByLimit(jo, user, null);
			logger.info("list数量:" +list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
