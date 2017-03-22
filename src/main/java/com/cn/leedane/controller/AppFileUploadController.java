package com.cn.leedane.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.cn.leedane.model.FilePathBean;
import com.cn.leedane.model.UploadBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.FilePathService;
import com.cn.leedane.service.UploadService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.ResponseCode;
import com.cn.leedane.utils.StringUtil;

@Controller
@RequestMapping("/leedane/appUpload")
public class AppFileUploadController extends BaseController{

	/**
     * 上传filePath表的service
     */
	@Autowired
  	private FilePathService<FilePathBean> filePathService;
  	
	@Autowired
  	private UploadService<UploadBean> uploadService;
  	
    /**
     * 上传文件（建议断点上传每次的文件不要超过1M）
     * 注意app要保证编号的正确性和顺序，到时合并的时候将按照大小进行合并
     * @return
     */
	@RequestMapping("/execute")
    public String execute(HttpServletRequest request, HttpServletResponse response) {
    	long startTime = System.currentTimeMillis();
    	Map<String, Object> message = new HashMap<String, Object>();
        try {
        	int uid = StringUtil.parseInt(request.getParameter("uid"), 0);
            
            UserBean user = userService.findById(uid);
            if(user == null){
            	message.put("message", EnumUtil.getResponseValue(ResponseCode.请先登录.value));
            	message.put("responseCode", ResponseCode.请先登录.value);
            	printWriter(message, response, startTime);
				return null;
        	}
            
            request.setCharacterEncoding("UTF-8");
        	String fileName = request.getParameter("fileName"); //必须是带后缀的文件名，断点续传可以不用赋值
        	String tableName = request.getParameter("tableName"); //业务对应的物理表名,必须
        	int order = StringUtil.parseInt(request.getParameter("order"), 0); //多张图片时候的图片的位置，必须，为空默认是0

            //String fileId = request.getParameter("fileId");  //文件ID
            String tableUuid = request.getParameter("uuid");  //客户端生成的唯一性uuid标识符
            
            //暂时解决乱码问题
            tableUuid = new String (tableUuid.getBytes("iso-8859-1"), "UTF-8");
            System.out.println("用户ID为："+uid+",fileName:"+fileName+",tableName:"+tableName+",order:"+order+",tableUuid:"+tableUuid);
            
            //编码，注意app要保证编号的正确性和顺序，到时合并的时候将按照大小进行合并
            int serialNumber = StringUtil.parseInt(request.getParameter("serialNumber"), 0);
            
            StringBuffer fileFullPath = new StringBuffer();
            /**
             * 说明启用的是断点上传的方式（建议断点上传每次的文件不要超过1M）
             */
            if(serialNumber > 0){
            	fileFullPath.append(ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER);
                fileFullPath.append("appUpload//");
                fileFullPath.append(uid);
                fileFullPath.append("_");
                fileFullPath.append(tableUuid);
                fileFullPath.append("_");
                fileFullPath.append(order);
                //fileFullPath.append("_");
                //fileFullPath.append(DateUtil.DateToString(new Date(), "yyyyMMddHHmmss"));
                fileFullPath.append("_");
                fileFullPath.append(serialNumber);
                fileFullPath.append(".temp");
            }else{//非断点续传
            	
            	if(StringUtil.isNull(fileName) || StringUtil.isNull(tableName)){
            		message.put("message", EnumUtil.getResponseValue(ResponseCode.某些参数为空.value));
                	message.put("responseCode", ResponseCode.某些参数为空.value);
            		return null;
            	}
            	
            	fileFullPath.append(ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER);
                fileFullPath.append("temporary//");
                fileFullPath.append(uid);
                fileFullPath.append("_");
                fileFullPath.append(tableUuid);
                //fileFullPath.append("_");
                //fileFullPath.append(DateUtil.DateToString(new Date(), "yyyyMMddHHmmss"));
                fileFullPath.append("_");
                fileFullPath.append(fileName);
            }   
            File file = new File(fileFullPath.toString()); 
            //文件存在，先删除，不存在，就创建空的
            if(file.exists()){  
                file.deleteOnExit(); 
                file.createNewFile(); 
            }else{
            	file.createNewFile(); 
            }
            
            InputStream input = request.getInputStream();   
            FileOutputStream fos = new FileOutputStream(fileFullPath.toString());  
      
            int size = 0;  
            byte[] buffer = new byte[1024];  
            while ((size = input.read(buffer,0,1024)) != -1) {  
                fos.write(buffer, 0, size);  
            }  
            fos.close();  
            input.close();  
            
            
           if(serialNumber == 0 ){ 	   
        	   message.put("isSuccess", filePathService.saveSourceAndEachFile(fileFullPath.toString(), user, tableUuid, tableName, order, null, null));
        	   printWriter(message, response, startTime);
        	   return null;
           }else{
        	   UploadBean upload = new UploadBean();
        	   upload.setCreateTime(new Date());
        	   upload.setCreateUserId(user.getId());
        	   upload.setfOrder(order);
        	   upload.setPath(fileFullPath.toString());
        	   upload.setSerialNumber(serialNumber);
        	   upload.setStatus(ConstantsUtil.STATUS_NORMAL);
        	   upload.setTableName(tableName);
        	   upload.setTableUuid(tableUuid);
        	   if(uploadService.addUpload(upload, user, request)){
        		   	message.put("isSuccess", true);
        	   }else{
        		   	message.put("message", EnumUtil.getResponseValue(ResponseCode.文件上传失败.value));
               		message.put("responseCode", ResponseCode.文件上传失败.value);
        	   }
        	   printWriter(message, response, startTime);
        	   return null;
           }
        } catch (Exception e) {
            System.out.println("上传文件发生异常,错误原因 : " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        message.put("message", EnumUtil.getResponseValue(ResponseCode.服务器处理异常.value));
   		message.put("responseCode", ResponseCode.服务器处理异常.value);
        System.out.println("上传总计耗时："+ (endTime - startTime) +"毫秒");
        printWriter(message, response, startTime);
		return null;
    }
}
