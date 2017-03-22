package com.cn.leedane.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.ueditor.MyActionEnter;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.JsoupUtil;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

/**
 * ueditor上传文件入口控制类
 * @author LeeDane
 * 2017年3月22日 下午1:55:43
 * version 1.0
 */
@Controller
@RequestMapping("/ue")
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
