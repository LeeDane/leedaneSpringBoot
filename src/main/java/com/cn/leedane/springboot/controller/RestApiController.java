package com.cn.leedane.springboot.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 提供外部访问的restful规范的api控制器
 * @author LeeDane
 * 2017年3月13日 下午12:23:13
 * Version 1.0
 */
@RestController
public class RestApiController {
	
	@RequestMapping("/{ggg}")
	public String test(@PathVariable int ggg){
		return "hehe ="+ ggg + "===>";
	}

	
	/** 
     * 通过post请求去登陆 
     *  
     * @param name 
     * @param pwd 
     * @return 
     */  
    /*@RequestMapping(value = "/login", method = RequestMethod.POST)  
    public Map<String, Object> loginByPost(@RequestParam(value = "account", required = true) String name,  
    		@RequestParam(value = "pwd", required = true) String pwd, @RequestBody(required = true)String dd) {  
        System.out.println("hello post"); 
        Map<String, Object> mp = new HashMap<String, Object>();
        mp.put("account", name);
        mp.put("pwd", pwd);
        mp.put("success", true);
        return mp;  
    }  */
}
