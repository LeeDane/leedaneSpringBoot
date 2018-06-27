package com.cn.leedane.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.SignInBean;
import com.cn.leedane.service.BlogService;
import com.cn.leedane.service.FilePathService;
import com.cn.leedane.service.SignInService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.bd)
public class BatchDealController extends BaseController{
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private SignInService<SignInBean> signInService;
	
	@Autowired
	private BlogService<BlogBean> blogService;
	
	@Autowired
	private FilePathService<FilePathBean> filePathService;
	
	@Autowired
	private CloudStoreHandler cloudStoreHandler;
	
	@Value("${constant.qiniu.server.url}")
    public String QINIU_SERVER_URL;
	/**
	 * 执行方法
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/registerByPhoneNoValidate", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> registerByPhoneNoValidate(HttpServletRequest request){
		/*List<SignInBean> signInBeans = signInService.executeHQL("SignInBean", null);
		if(signInBeans.size() > 0){
			Date time = null;
			for(SignInBean signInBean: signInBeans){
				time = signInBean.getCreateTime();
				signInBean.setCreateDate(DateUtil.DateToString(time, "yyyy-MM-dd"));
				signInService.update(signInBean);
			}
		}*/
		ResponseMap message = new ResponseMap();
		for(int i = 1504;i > 0; i--){
			List<BlogBean> blogs = blogService.getBlogBeans("select id, digest from "+DataTableType.博客.value+" where id=?", i);
			if(!blogs.isEmpty() && blogs.size() >0){
				List<BlogBean> blogs2 = new ArrayList<BlogBean>(); //blogService.executeHQL("BlogBean", "where digest = '"+StringUtil.changeNotNull(blogs.get(0).get("digest"))+"' and id != "+StringUtil.changeObjectToInt(blogs.get(0).get("id")));
				if(blogs2.size() > 0){
					for(BlogBean blog2: blogs2){
						blogService.executeSQL("delete from "+DataTableType.博客.value+" where id = ?", blog2.getId());
					}
				}
			}
			
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			logger.info("i="+i);
		}
		return message.getMap();
	}
	
	/**
	 * 执行上传最新的10条记录
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/upload10", method = RequestMethod.POST, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> upload10(HttpServletRequest request){
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> paths = filePathService.executeSQL("select * from "+DataTableType.文件.value+" where is_upload_qiniu = 0 and table_name <> '"+DataTableType.心情.value+"' order by id desc limit 10");
		if(paths != null && paths.size() > 0){	
			List<Map<String,Object>> fileBeans = cloudStoreHandler.executeUpload(paths);
			if(fileBeans != null && fileBeans.size() >0){
				for(Map<String, Object> m: fileBeans){
					if(m.containsKey("id")){
						filePathService.updateUploadQiniu(StringUtil.changeObjectToInt(m.get("id")), QINIU_SERVER_URL + StringUtil.changeNotNull(m.get("path")));
					}
				}
			}
		}else{
			logger.info("没有要上传的文件");
		}
		return message.getMap();
	}
}
