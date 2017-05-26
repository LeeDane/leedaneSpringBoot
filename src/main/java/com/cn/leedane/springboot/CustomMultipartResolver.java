package com.cn.leedane.springboot;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.cn.leedane.upload.FileUploadProgressListener;

public class CustomMultipartResolver extends CommonsMultipartResolver{
	
	public CustomMultipartResolver(ServletContext servletContext){
		super(servletContext);
	}
	
	// 注入第二步写的FileUploadProgressListener 
    @Autowired
    private FileUploadProgressListener progressListener;

    public void setFileUploadProgressListener(FileUploadProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    @Override
    public MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
    	
    	DiskFileItemFactory factory = new DiskFileItemFactory();  
        String path = new String("C:/webroot/buffer");//设置磁盘缓冲路径  
      
        factory.setRepository(new File(path));  
        factory.setSizeThreshold(10 * 1024*1024);//设置创建缓冲大小  
      
          
        ServletFileUpload upload = new ServletFileUpload(factory);  
        upload.setSizeMax(200 * 1024 * 1024);//设置上传文件限制大小,-1无上限
    	
        String encoding = determineEncoding(request);
        //FileUpload fileUpload = prepareFileUpload(encoding);
        progressListener.setSession(request.getSession());
        upload.setProgressListener(progressListener);
        //fileUpload.setProgressListener(progressListener);
        
        try {
            List<FileItem> fileItems = upload.parseRequest(request);
            return parseFileItems(fileItems, encoding);
        } catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(upload.getSizeMax(), ex);
        } catch (FileUploadException ex) {
            throw new MultipartException("Could not parse multipart servlet request", ex);
        }
    }
}
