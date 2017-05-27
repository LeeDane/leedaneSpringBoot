package com.cn.leedane.controller;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.pam.UnsupportedTokenException;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.UploadBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FilePathService;
import com.cn.leedane.service.UploadService;
import com.cn.leedane.upload.Progress;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.ResponseCode;
import com.cn.leedane.utils.ImageCutUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.ResponseMap;

@RestController
@RequestMapping(value = ControllerBaseNameUtil.wul)
public class WebFileUploadController extends BaseController{
	
	private Logger logger = Logger.getLogger(getClass());

	/**
     * 上传filePath表的service
     */
	@Autowired
  	private FilePathService<FilePathBean> filePathService;
  	
	@Autowired
  	private UploadService<UploadBean> uploadService;
  	
    /**
     * 多个文件（建议断点上传每次的文件不要超过1M）
     * @return
     */
	@RequestMapping(value = "/uploads")
	@ResponseBody
    public Map<String, Object> uploads(HttpServletRequest request, HttpServletResponse response, 
    		@RequestParam(value="myfile", required = false) CommonsMultipartFile[] multipartFiles) {
		
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getMap();
		}
		
		UserBean user = getUserFromMessage(message);
        try {
            if(multipartFiles == null || user == null)
           	 	throw new UnsupportedTokenException();
                        
            //保存全部的七牛链接
            List<String> qiniuLinks = new ArrayList<String>();
            for(CommonsMultipartFile multipartFile: multipartFiles){
            	if(multipartFile == null)
            		throw new NullPointerException("multipartFile为空");
            	
            	//saveFile(user, currentUser, request, message, file);
                File tempFile = new File(getRootPath(user, multipartFile));
                multipartFile.transferTo(tempFile);
                
                //把上传好的七牛云存储路径放到session中
                qiniuLinks.add(CloudStoreHandler.uploadFile(null, tempFile));
                
                //删除缓存文件
                if(tempFile != null)
                	tempFile.delete();
            }
            message.put("message", qiniuLinks);
       		message.put("responseCode", ResponseCode.请求返回成功码.value);
       		message.put("isSuccess", true);
        	return message.getMap();
        } catch (Exception e) {
        	logger.error("上传文件发生异常,错误原因 : " + e.getMessage());
        }
        message.put("message", EnumUtil.getResponseValue(ResponseCode.服务器处理异常.value));
   		message.put("responseCode", ResponseCode.服务器处理异常.value);
   		return message.getMap();
    }
	
	/**
     * 只上传图像文件
     * @return
     */
	@RequestMapping(value = "/upload/imgage")
    public Map<String, Object> imgage(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="file") CommonsMultipartFile file) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getMap();
		}
		
		 //判断文件的MIMEtype
        String type = file.getContentType();
        if(type==null || !type.toLowerCase().startsWith("image/")){
        	message.put("message", EnumUtil.getResponseValue(ResponseCode.文件上传失败.value));
       		message.put("responseCode", ResponseCode.文件上传失败.value);
       		return message.getMap();
        }
        JSONObject json = getJsonFromMessage(message).getJSONObject("img-data");
          // 用户经过剪辑后的图片的大小  
        float x = JsonUtil.getFloatValue(json, "x");
        float y = JsonUtil.getFloatValue(json, "y");
        float w = JsonUtil.getFloatValue(json, "width");
        float h = JsonUtil.getFloatValue(json, "height");

        //开始上传
        File targetFile = new File(getRootPath(getUserFromMessage(message), file));
        
        //保存  
        try {  
            if(!targetFile.exists()){  
                targetFile.createNewFile();  
                InputStream is = file.getInputStream();
                ImageCutUtil.cut(is, targetFile, (int)x,(int)y,(int)w,(int)h);  
                is.close();
                
                //上传图片到七牛云储存
	            String qiniuPath = CloudStoreHandler.uploadFile(null, targetFile);
	            
	            //删除临时文件
	            if (targetFile != null)
	            	targetFile.delete();
	            
                message.put("message", qiniuPath);
           		message.put("responseCode", ResponseCode.请求返回成功码.value);
           		message.put("isSuccess", true);
           		return message.getMap();
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
        message.put("message", EnumUtil.getResponseValue(ResponseCode.服务器处理异常.value));
   		message.put("responseCode", ResponseCode.服务器处理异常.value);
   		return message.getMap();
    }
	
	/**
     * 单个文件上传
     * @return
     */
	@RequestMapping(value = "/upload")
	@ResponseBody
	@Deprecated  //暂时不使用
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="myfile") CommonsMultipartFile multipartFile) {
		
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request)){
			return message.getMap();
		}
		
        try {
        	UserBean user = null;
        	Subject currentUser = SecurityUtils.getSubject();
            if(currentUser.isAuthenticated()){
           	 	user = (UserBean)currentUser.getSession().getAttribute(UserController.USER_INFO_KEY);
            }
            
            if(user == null)
           	 	throw new UnsupportedTokenException();
            
            if(multipartFile == null)
        		throw new NullPointerException("multipartFile为空");
        	//saveFile(user, currentUser, request, message, file);
            File tempFile = new File(getRootPath(user, multipartFile));
            multipartFile.transferTo(tempFile);
            
        	return message.getMap();
        } catch (Exception e) {
        	logger.error("上传文件发生异常,错误原因 : " + e.getMessage());
        }
        message.put("message", EnumUtil.getResponseValue(ResponseCode.服务器处理异常.value));
   		message.put("responseCode", ResponseCode.服务器处理异常.value);
   		return message.getMap();
    }
	
	/**
	 * 获取进度条的进度
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getProgress", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> getProgress(HttpServletRequest request) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
        try {
        	Subject currentUser = SecurityUtils.getSubject();
        	Progress status = (Progress) currentUser.getSession().getAttribute("status");
        	//从session中获取进度
            message.put("message", status);
            message.put("responseCode", ResponseCode.请求返回成功码.value);
            message.put("isSuccess", true);
            return message.getMap();
        } catch (Exception e) {
        	logger.error("上传文件发生异常,错误原因 : " + e.getMessage());
        }
        message.put("message", EnumUtil.getResponseValue(ResponseCode.服务器处理异常.value));
   		message.put("responseCode", ResponseCode.服务器处理异常.value);
		return message.getMap();
    }
	
	
	/**
	 * 获取进度条的进度
	 * @param request
	 * @param itemId
	 * @return
	 */
	@RequestMapping(value = "/getQiNiuPath/{item}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> getQiNiuPath(HttpServletRequest request, @PathVariable(value="item") String item) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
        try {
        	Subject currentUser = SecurityUtils.getSubject();
        	//从session中获取进度
            message.put("message", currentUser.getSession().getAttribute("qiniu-"+ item));
            message.put("responseCode", ResponseCode.请求返回成功码.value);
            message.put("isSuccess", true);
            //把session缓存的信息清掉
            currentUser.getSession().removeAttribute("qiniu-"+ item);
            return message.getMap();
        } catch (Exception e) {
        	logger.error("上传文件发生异常,错误原因 : " + e.getMessage());
        }
        message.put("message", EnumUtil.getResponseValue(ResponseCode.服务器处理异常.value));
   		message.put("responseCode", ResponseCode.服务器处理异常.value);
		return message.getMap();
    }
	
    /**
     * 获取文件的在本地的临时路径
     * @param user
     * @param file
     * @return
     */
    private String getRootPath(UserBean user, MultipartFile file){
    	//新的文件名，为了避免冲突
		String newFileName = user.getAccount() + "_"+DateUtil.DateToString(new Date(), "yyyyMMddHHmmss") + file.getOriginalFilename();
        StringBuffer rootPath = new StringBuffer();
		rootPath.append(ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER);
		rootPath.append(com.cn.leedane.enums.FileType.TEMPORARY.value);
		rootPath.append(File.separator);
		rootPath.append(newFileName);
		return rootPath.toString();
    }
  
}
