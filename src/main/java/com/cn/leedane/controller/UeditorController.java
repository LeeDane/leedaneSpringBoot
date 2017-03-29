package com.cn.leedane.controller;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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

	/**
	 * 发布博客
	 * @return
	 * @throws IOException 
	 * @throws Exception 
	 */
	@RequestMapping("/jsp")
	public void releaseBlog(HttpServletRequest request, HttpServletResponse response) throws IOException{
		request.setCharacterEncoding( "utf-8" );
		response.setHeader("Content-Type" , "text/html");
		ServletContext application = request.getSession().getServletContext();
		String rootPath = application.getRealPath( "/" );
		
		response.getWriter().write( new MyActionEnter( request, rootPath ).exec() );
	}
}
