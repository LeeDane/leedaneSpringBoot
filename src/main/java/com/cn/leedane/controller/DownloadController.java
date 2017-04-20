package com.cn.leedane.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.cn.leedane.utils.Base64ImageUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.ControllerBaseNameUtil;
import com.cn.leedane.utils.HttpUtil;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.StringUtil;

@Controller
@RequestMapping(value = ControllerBaseNameUtil.dl)
public class DownloadController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());
	
	private int start; //开始的位置
	private int end;  //结束的位置
	
	private int requestSize;//请求获取的文件大小
	
	private String filename;//请求下载的文件名称
	
	/**
	 * 下载
	 */
	@RequestMapping(value = "/execute", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public void executeDown(HttpServletRequest request, HttpServletResponse response){
		try {
			//获取下载的范围
			String range = request.getHeader("RANGE");
			if(!StringUtil.isNull(range) && range.startsWith("bytes=")){
				String[] values =range.split("=")[1].split("-");  
	            start = Integer.parseInt(values[0]);  
	            //end = Integer.parseInt(values[1]);  
			}
			
			if(end!=0 && end > start){  
	            requestSize = end - start + 1;  
	            response.addHeader("content-length", ""+(requestSize));  
	        } else {  
	            requestSize = Integer.MAX_VALUE;  
	        }  
			
			JSONObject jo = HttpUtil.getJsonObjectFromInputStream(null,request);
			if(jo != null && jo.containsKey("filename")){
				filename = jo.getString("filename");
			}
			
			if(StringUtil.isNull(filename)){
				filename = "F:/个人/资料/萌头像/02.jpg";
			}
			
			RandomAccessFile raFile = new RandomAccessFile(filename, "r");
			byte[] buffer = new byte[4096];  
	        
			response.setContentType("application/x-download");  
			response.addHeader("Content-Disposition", "attachment;filename=a.iso");  
			
			response.addHeader("Pragma", "no-cache");
			response.addHeader("Cache-Control", "no-cache");
	        OutputStream os = response.getOutputStream();  
	          
	        int needSize = requestSize;  
	        
	        response.setContentLength(needSize);
	        // 设置状态 HTTP/1.1 206 Partial Content
	        response.setStatus(javax.servlet.http.HttpServletResponse.SC_PARTIAL_CONTENT);

	        // 表示使用了断点续传（默认是“none”，可以不指定）
	        response.setHeader("Accept-Ranges", "bytes");
	          
	        raFile.seek(start);  
	        while(needSize > 0){  
	            int len = raFile.read(buffer);  
	            if(needSize < buffer.length){  
	                os.write(buffer,0,needSize);  
	            } else {  
	                os.write(buffer,0,len);  
	                if(len < buffer.length){  
	                    break;  
	                }  
	            }  
	            needSize -= buffer.length;  
	        }  
	              
	        raFile.close();  
	        os.close(); 
	        System.out.println("下载完成");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * 获取webroot/file文件夹下面的图片
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "/localBase64Image", method = RequestMethod.GET, produces = {"application/json;charset=UTF-8"})
	public Map<String, Object> getLocalBase64Image(HttpServletRequest request, HttpServletResponse response){
		ResponseMap message = new ResponseMap();
		
		if(!checkParams(message, request))
			return message.getMap();
		
		checkRoleOrPermission(request);
		String filename = getJsonFromMessage(message).getString("filename");
		if(StringUtil.isNull(filename)){
			message.put("message", "文件名称为空");
			return message.getMap();
		}
		
		String suffixs = StringUtil.getSuffixs(filename);	
		System.out.println("suffixs:"+suffixs);
		filename = ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER + "file//" + filename;
		try {
			message.put("message", Base64ImageUtil.convertImageToBase64(filename, suffixs));
		} catch (IOException e) {
			e.printStackTrace();
			return message.getMap();
		}
		message.put("isSuccess", true);
		return message.getMap();
	}
}
