package com.cn.leedane.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.cn.leedane.ueditor.MyActionEnter;
import com.cn.leedane.utils.ControllerBaseNameUtil;

/**
 * ueditor上传文件入口控制类
 * @author LeeDane
 * 2017年3月22日 下午1:55:43
 * version 1.0
 */
@Controller
@RequestMapping(value = ControllerBaseNameUtil.ue)
public class UeditorController extends BaseController{

	protected final Log log = LogFactory.getLog(BlogController.class);
	
	@Value("${ueditor.base.rootPath}")  //@value不支持在普通类中使用
	private String rootPath;
	
	@Value("${ueditor.base.contextPath}")
	private String contextPath;
	
	@Value("${ueditor.base.configJspPath}")
	private String configJspPath;

	/**
	 * 发布博客
	 * @return
	 * @throws IOException 
	 * @throws Exception 
	 */
	@RequestMapping("/jsp")
	public void releaseBlog(HttpServletRequest request, HttpServletResponse response, @RequestParam(value= "upfile", required = false)MultipartFile file) throws IOException{
		request.setCharacterEncoding( "utf-8" );
		response.setHeader("Content-Type" , "text/html");
		MyActionEnter actionEnter;
		if(file != null){
			actionEnter = new MyActionEnter(request, file, rootPath, configJspPath);
		}else{
			actionEnter = new MyActionEnter(request, rootPath, configJspPath);
		}
		response.getWriter().write(actionEnter.exec());
	}
}
