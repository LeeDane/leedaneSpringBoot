package com.cn.leedane.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

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

import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.UploadBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FilePathService;
import com.cn.leedane.service.UploadService;
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
     * 上传文件（建议断点上传每次的文件不要超过1M）
     * @return
     */
	@RequestMapping(value = "/upload")
    public Map<String, Object> upload(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="file") MultipartFile file) {
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
            
        	saveFile(user, currentUser, request, message, file);
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
    public Map<String, Object> imgage(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="file") MultipartFile file) {
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
                targetFile.mkdirs();  
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
     * 多文件上传
     * 注意app要保证编号的正确性和顺序，到时合并的时候将按照大小进行合并
     * @return
     */
	@RequestMapping(value = "/uploads")
	@ResponseBody
    public Map<String, Object> uploads(HttpServletRequest request, HttpServletResponse response, @RequestParam(value="fileFields") MultipartFile[] files) {
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
        	saveFiles(user, currentUser, request, message, files);
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
	 * @param uuid 标记当前上传记录的唯一uuid
	 * @return
	 */
	@RequestMapping(value = "/getProgress/{uuid}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> getProgress(HttpServletRequest request, @PathVariable(value="uuid") String uuid) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
        try {
        	Subject currentUser = SecurityUtils.getSubject();
        	//从session中获取进度
            message.put("message", currentUser.getSession().getAttribute(uuid));
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
	 * @param uuid 标记当前上传记录的唯一uuid
	 * @return
	 */
	@RequestMapping(value = "/getQiNiuPath/{uuid}", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
    public Map<String, Object> getQiNiuPath(HttpServletRequest request, @PathVariable(value="uuid") String uuid) {
		ResponseMap message = new ResponseMap();
		if(!checkParams(message, request))
			return message.getMap();
		
        try {
        	Subject currentUser = SecurityUtils.getSubject();
        	//从session中获取进度
            message.put("message", currentUser.getSession().getAttribute("qiniu-"+ uuid));
            message.put("responseCode", ResponseCode.请求返回成功码.value);
            message.put("isSuccess", true);
            //把session缓存的信息清掉
            currentUser.getSession().removeAttribute("qiniu-"+ uuid);
            currentUser.getSession().removeAttribute(uuid);
            return message.getMap();
        } catch (Exception e) {
        	logger.error("上传文件发生异常,错误原因 : " + e.getMessage());
        }
        message.put("message", EnumUtil.getResponseValue(ResponseCode.服务器处理异常.value));
   		message.put("responseCode", ResponseCode.服务器处理异常.value);
		return message.getMap();
    }
	/***
	 * 保存单个文件
	 * @param user
	 * @param request
	 * @param responseMap
	 * @param file
	 * @throws IllegalStateException
	 * @throws IOException
	 */
    private void saveFile(final UserBean user, final Subject currentUser, HttpServletRequest request, ResponseMap responseMap, final MultipartFile file) throws IllegalStateException, IOException {  
    	// 判断文件是否为空  
        if (!file.isEmpty()) { 
            // 文件保存路径  
        	//给客户端生成新的标记uuid，便于获取进度
            final String fileUuid = UUID.randomUUID().toString();
            
        	new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
			    		File tempFile = new File(getRootPath(user, file));
			    		
			            // 转存文件  
			    		InputStream input;
						input = file.getInputStream();
						 FileOutputStream fos = new FileOutputStream(tempFile);  
					      
				            int size = 0;  
				            byte[] buffer = new byte[1024 * 1]; //每次处理10k
				            long total = file.getSize();
				            long already = 0;
				            while ((size = input.read(buffer,0, 1024 *1)) != -1) {  
				                fos.write(buffer, 0, size); 
				                already += size;
				                double db = already * 1.00d / total * 100;
				                BigDecimal b = new BigDecimal(db);
				                double f1 = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
				                
				                //把当前的进度放到session中
				                //由于还有上传的环节，不能直接返回100
				                if(f1 > 98)
				                	f1 = 98;
				                currentUser.getSession().setAttribute(fileUuid, f1);
				                //if(already > 18* 1024)
				                	//size = 10 / 0;
				                
				                /*try {
				                	//模拟休息1秒钟
									Thread.sleep(100);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}*/
				            }  
				            fos.close();  
				            input.close();
				            //把上传好的七牛云存储路径放到session中
			                currentUser.getSession().setAttribute("qiniu-"+fileUuid, CloudStoreHandler.uploadFile(null, tempFile));
			                
			                //上传到七牛之后才设置成100
			                currentUser.getSession().setAttribute(fileUuid, 100);
				            //删除临时文件
			                if (tempFile != null)
			                	tempFile.delete();
			                
					} catch (Exception e) {
						e.printStackTrace();
						//把当前的进度放到session中
		                currentUser.getSession().setAttribute(fileUuid, 9999);
					}
				}
			}).start();
    		
            
            responseMap.put("message", fileUuid);
            responseMap.put("responseCode", ResponseCode.请求返回成功码.value);
            responseMap.put("isSuccess", true);
        }
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
    
    /**
     * 保存多个文件
     * @param user
     * @param request
     * @param responseMap
     * @param files
     * @throws IllegalStateException
     * @throws IOException
     */
    private void saveFiles(UserBean user, Subject currentUser, HttpServletRequest request, ResponseMap responseMap, @RequestParam("files") MultipartFile[] files) throws IllegalStateException, IOException {  
        //判断file数组不能为空并且长度大于0  
        if(files!=null&&files.length>0){  
            //循环获取file数组中得文件  
            for(int i = 0;i<files.length;i++){  
                MultipartFile file = files[i];  
                //保存文件  
                saveFile(user, currentUser, request, responseMap, file);
            }  
        }
    }  
}
