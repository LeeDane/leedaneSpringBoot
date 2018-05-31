package com.cn.leedane.controller.mall;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.mall.S_WishBean;
import com.cn.leedane.service.mall.S_WishService;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.LayuiTableResponseMap;
import com.cn.leedane.utils.ResponseMap;

/**
 * 商品心愿单接口controller
 * @author LeeDane
 * 2017年11月13日 下午7:21:38
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.mall)
public class WishController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private S_WishService<S_WishBean> wishService;
		
	
	/**
	 * 给商品加到心愿单
	 * @return
	 */
	@RequestMapping(value = "/wish", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> add(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(wishService.add(getJsonFromMessage(message), getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 给商品加到心愿单
	 * @return
	 */
	@RequestMapping(value = "/wish/{wishId}", method = RequestMethod.DELETE, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> delete(@PathVariable("wishId") int wishId, Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(wishService.delete(wishId, getUserFromMessage(message), request));
		return message.getMap();
	}

	/**
	 * 获取心愿单数量
	 * @return
	 */
	@RequestMapping(value = "/wish/number", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getNumber(Model model, HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(wishService.getWishNumber(getUserFromMessage(message), request));
		return message.getMap();
	}
	
	/**
	 * 获取心愿单数量
	 * @return
	 */
	@RequestMapping(value = "/wishs", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> mywish(Model model, HttpServletRequest request, 
			@RequestParam(value="page") Integer current,
			@RequestParam(value="limit") Integer pageSize){
		
		//JSONArray value =JSONArray.fromObject("[{\"id\":10000,\"username\":\"user-0\",\"sex\":\"女\",\"city\":\"城市-0\",\"sign\":\"签名-0\",\"experience\":255,\"logins\":24,\"wealth\":82830700,\"classify\":\"作家\",\"score\":57},{\"id\":10001,\"username\":\"user-1\",\"sex\":\"男\",\"city\":\"城市-1\",\"sign\":\"签名-1\",\"experience\":884,\"logins\":58,\"wealth\":64928690,\"classify\":\"词人\",\"score\":27},{\"id\":10002,\"username\":\"user-2\",\"sex\":\"女\",\"city\":\"城市-2\",\"sign\":\"签名-2\",\"experience\":650,\"logins\":77,\"wealth\":6298078,\"classify\":\"酱油\",\"score\":31},{\"id\":10003,\"username\":\"user-3\",\"sex\":\"女\",\"city\":\"城市-3\",\"sign\":\"签名-3\",\"experience\":362,\"logins\":157,\"wealth\":37117017,\"classify\":\"诗人\",\"score\":68},{\"id\":10004,\"username\":\"user-4\",\"sex\":\"男\",\"city\":\"城市-4\",\"sign\":\"签名-4\",\"experience\":807,\"logins\":51,\"wealth\":76263262,\"classify\":\"作家\",\"score\":6},{\"id\":10005,\"username\":\"user-5\",\"sex\":\"女\",\"city\":\"城市-5\",\"sign\":\"签名-5\",\"experience\":173,\"logins\":68,\"wealth\":60344147,\"classify\":\"作家\",\"score\":87},{\"id\":10006,\"username\":\"user-6\",\"sex\":\"女\",\"city\":\"城市-6\",\"sign\":\"签名-6\",\"experience\":982,\"logins\":37,\"wealth\":57768166,\"classify\":\"作家\",\"score\":34},{\"id\":10007,\"username\":\"user-7\",\"sex\":\"男\",\"city\":\"城市-7\",\"sign\":\"签名-7\",\"experience\":727,\"logins\":150,\"wealth\":82030578,\"classify\":\"作家\",\"score\":28},{\"id\":10008,\"username\":\"user-8\",\"sex\":\"男\",\"city\":\"城市-8\",\"sign\":\"签名-8\",\"experience\":951,\"logins\":133,\"wealth\":16503371,\"classify\":\"词人\",\"score\":14},{\"id\":10009,\"username\":\"user-9\",\"sex\":\"女\",\"city\":\"城市-9\",\"sign\":\"签名-9\",\"experience\":484,\"logins\":25,\"wealth\":86801934,\"classify\":\"词人\",\"score\":75},{\"id\":10010,\"username\":\"user-10\",\"sex\":\"女\",\"city\":\"城市-10\",\"sign\":\"签名-10\",\"experience\":1016,\"logins\":182,\"wealth\":71294671,\"classify\":\"诗人\",\"score\":34},{\"id\":10011,\"username\":\"user-11\",\"sex\":\"女\",\"city\":\"城市-11\",\"sign\":\"签名-11\",\"experience\":492,\"logins\":107,\"wealth\":8062783,\"classify\":\"诗人\",\"score\":6},{\"id\":10012,\"username\":\"user-12\",\"sex\":\"女\",\"city\":\"城市-12\",\"sign\":\"签名-12\",\"experience\":106,\"logins\":176,\"wealth\":42622704,\"classify\":\"词人\",\"score\":54},{\"id\":10013,\"username\":\"user-13\",\"sex\":\"男\",\"city\":\"城市-13\",\"sign\":\"签名-13\",\"experience\":1047,\"logins\":94,\"wealth\":59508583,\"classify\":\"诗人\",\"score\":63},{\"id\":10014,\"username\":\"user-14\",\"sex\":\"男\",\"city\":\"城市-14\",\"sign\":\"签名-14\",\"experience\":873,\"logins\":116,\"wealth\":72549912,\"classify\":\"词人\",\"score\":8},{\"id\":10015,\"username\":\"user-15\",\"sex\":\"女\",\"city\":\"城市-15\",\"sign\":\"签名-15\",\"experience\":1068,\"logins\":27,\"wealth\":52737025,\"classify\":\"作家\",\"score\":28},{\"id\":10016,\"username\":\"user-16\",\"sex\":\"女\",\"city\":\"城市-16\",\"sign\":\"签名-16\",\"experience\":862,\"logins\":168,\"wealth\":37069775,\"classify\":\"酱油\",\"score\":86},{\"id\":10017,\"username\":\"user-17\",\"sex\":\"女\",\"city\":\"城市-17\",\"sign\":\"签名-17\",\"experience\":1060,\"logins\":187,\"wealth\":66099525,\"classify\":\"作家\",\"score\":69},{\"id\":10018,\"username\":\"user-18\",\"sex\":\"女\",\"city\":\"城市-18\",\"sign\":\"签名-18\",\"experience\":866,\"logins\":88,\"wealth\":81722326,\"classify\":\"词人\",\"score\":74},{\"id\":10019,\"username\":\"user-19\",\"sex\":\"女\",\"city\":\"城市-19\",\"sign\":\"签名-19\",\"experience\":682,\"logins\":106,\"wealth\":68647362,\"classify\":\"词人\",\"score\":51},{\"id\":10020,\"username\":\"user-20\",\"sex\":\"男\",\"city\":\"城市-20\",\"sign\":\"签名-20\",\"experience\":770,\"logins\":24,\"wealth\":92420248,\"classify\":\"诗人\",\"score\":87},{\"id\":10021,\"username\":\"user-21\",\"sex\":\"男\",\"city\":\"城市-21\",\"sign\":\"签名-21\",\"experience\":184,\"logins\":131,\"wealth\":71566045,\"classify\":\"词人\",\"score\":99},{\"id\":10022,\"username\":\"user-22\",\"sex\":\"男\",\"city\":\"城市-22\",\"sign\":\"签名-22\",\"experience\":739,\"logins\":152,\"wealth\":60907929,\"classify\":\"作家\",\"score\":18},{\"id\":10023,\"username\":\"user-23\",\"sex\":\"女\",\"city\":\"城市-23\",\"sign\":\"签名-23\",\"experience\":127,\"logins\":82,\"wealth\":14765943,\"classify\":\"作家\",\"score\":30},{\"id\":10024,\"username\":\"user-24\",\"sex\":\"女\",\"city\":\"城市-24\",\"sign\":\"签名-24\",\"experience\":212,\"logins\":133,\"wealth\":59011052,\"classify\":\"词人\",\"score\":76},{\"id\":10025,\"username\":\"user-25\",\"sex\":\"女\",\"city\":\"城市-25\",\"sign\":\"签名-25\",\"experience\":938,\"logins\":182,\"wealth\":91183097,\"classify\":\"作家\",\"score\":69},{\"id\":10026,\"username\":\"user-26\",\"sex\":\"男\",\"city\":\"城市-26\",\"sign\":\"签名-26\",\"experience\":978,\"logins\":7,\"wealth\":48008413,\"classify\":\"作家\",\"score\":65},{\"id\":10027,\"username\":\"user-27\",\"sex\":\"女\",\"city\":\"城市-27\",\"sign\":\"签名-27\",\"experience\":371,\"logins\":44,\"wealth\":64419691,\"classify\":\"诗人\",\"score\":60},{\"id\":10028,\"username\":\"user-28\",\"sex\":\"女\",\"city\":\"城市-28\",\"sign\":\"签名-28\",\"experience\":977,\"logins\":21,\"wealth\":75935022,\"classify\":\"作家\",\"score\":37},{\"id\":10029,\"username\":\"user-29\",\"sex\":\"男\",\"city\":\"城市-29\",\"sign\":\"签名-29\",\"experience\":647,\"logins\":107,\"wealth\":97450636,\"classify\":\"酱油\",\"score\":27}]");
		LayuiTableResponseMap message = new LayuiTableResponseMap(0, "", 0, null);
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(model, request);;
		message.putAll(wishService.paging(current - 1, pageSize, getUserFromMessage(message), request));
		return message.getMap();
	}
}
