package com.cn.leedane.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.StringUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.ResumeUploader;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;


/**
 * 云存储处理类
 * @author LeeDane
 * 2016年7月12日 上午11:54:11
 * Version 1.0
 */
@Component
public class CloudStoreHandler {
	public final static String ACCESSKEY = "aMgOWQCqz6CPIzjKbfLbQDbD0Jf9CD0P7DBA060W";
	public final static String SECRETKEY = "4TlTuQqE5s8r1bn3M82-3EQmNw22KzV6oIRBL3Pr";
	public final static String BUCKETNAME = "leedane";
	final String returnBody = "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"fsize\":\"$(fsize)\""
            + ",\"fname\":\"$(fname)\",\"mimeType\":\"$(mimeType)\"}";
	static Auth auth = Auth.create(ACCESSKEY, SECRETKEY);
	//重用 uploadManager。一般地，只需要创建一个 uploadManager 对象
	private static UploadManager uploadManager;
	public static String token;
    
    static{
   		token = auth.uploadToken(BUCKETNAME);
   	}
    
    /**
   	 * 系统的缓存对象
   	 */
   	@Autowired
   	private SystemCache systemCache;
 
    /**
     * 执行上传操作
     * @param filePathBeans
     * @return 执行成功的List<Map<String, Object>>
     */
    public List<Map<String, Object>> executeUpload(List<Map<String, Object>> filePathBeans){
    	System.out.println("开始执行上传操作...............");
    	List<Map<String, Object>> returnFilePaths = new ArrayList<Map<String,Object>>();
    	System.out.println("..............."+filePathBeans.size());
    	if(filePathBeans == null || filePathBeans.size() < 1)
    		return returnFilePaths;
    	
    	System.out.println("...............");
    	//派发2个线程
		ExecutorService threadpool = Executors.newFixedThreadPool(filePathBeans.size() > 5 ? 5: filePathBeans.size());
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		
		String fileFullPath = null;
		for(Map<String, Object> filePathBean: filePathBeans){	
			fileFullPath = ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER +"file" +File.separator +StringUtil.changeNotNull(filePathBean.get("path"));
			if(!StringUtil.isNull(fileFullPath)){
				System.out.println("fileFullPath:"+fileFullPath);
				File file = new File(fileFullPath);
				if(file.exists()){
					if(file.length() >= 1024 *1024 * 4){//对于大于4M的文件，生成多个临时文件分开上传
						futures.add(threadpool.submit(new CloudStoreHandler.SingleBigUploadTask(filePathBean)));
					}else
						futures.add(threadpool.submit(new CloudStoreHandler.SingleSmallUploadTask(filePathBean)));
				}
			}
			
			try {
				Thread.sleep(2000);//暂停半秒
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		threadpool.shutdown();
		
		List<String> errors = new ArrayList<String>();
		for(int i=0; i <futures.size(); i++){
			try {
				if(!futures.get(i).get(1000000, TimeUnit.MILLISECONDS)){
					errors.add(StringUtil.changeNotNull(filePathBeans.get(i).get("path")));
				}else{
					returnFilePaths.add(filePathBeans.get(i));
				}
				Thread.sleep(500);//暂停0.5秒
			} catch (Exception e) {
				e.printStackTrace();
				futures.get(i).cancel(true);
				errors.add(StringUtil.changeNotNull(filePathBeans.get(i).get("path")));
				continue;
			}
		}
		System.out.println("上传任务执行结束...............");
		for(String path: errors){
			System.out.println("上传到七牛云存储服务器失败的文件："+path);
		}
		
		return returnFilePaths;
    }
    
    /**
     * 执行上传操作
     * @param fileMap
     * @return
     */
    public static String uploadFile(UserBean user, File file){
    	System.out.println("Single开始执行上传操作...............");
    	String path = file.getName();
    	if(StringUtil.isNull(path))
    		return null;
		//String fileFullPath = ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER +"file" +File.separator + user.getAccount() + "_upload_" + UUID.randomUUID().toString() + "_" + StringUtil.getFileName(path);
		//File file = new File(fileFullPath);
		if(file.exists()){
			try {
				//密钥配置
		    	Auth auth1 = Auth.create(ACCESSKEY, SECRETKEY);
		    	String token1 = auth1.uploadToken(BUCKETNAME);
		    	//创建上传对象
		    	UploadManager uploadManager1 = new UploadManager();
		    	Response r = uploadManager1.put(file, path, token1);
				if(r.isOK() && r.statusCode == 200){
					System.out.println("Single上传任务执行结束...............");
					return ConstantsUtil.QINIU_SERVER_URL + path;
				}else{
					System.out.println("上传失败，返回的信息是--->"+r.bodyString());
				}
			} catch (QiniuException e) {
				try {
					Response r = e.response;
					// 请求失败时打印的异常的信息
					System.out.println(r.toString());
					//响应的文本信息
				    System.out.println(r.bodyString());
				} catch (QiniuException e1) {
				}
			}
		}else{
			
		}
		System.out.println("Single上传任务执行结束...............");
		return null;
    }
    
    /**
     * 执行单个文件上传操作
     * @param filePathBeans
     */
    public int executeSingleUpload(Map<String, Object> filePathBean){
    	System.out.println("Single开始执行上传操作...............");
    	int id = 0;
    	if(filePathBean == null || filePathBean.isEmpty())
    		return id;
    	String path = StringUtil.changeNotNull(filePathBean.get("path"));
		String fileFullPath = ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER +"file" +File.separator +path;
		File file = new File(fileFullPath);
		if(file.exists()){
			try {
				//密钥配置
		    	Auth auth1 = Auth.create(ACCESSKEY, SECRETKEY);
		    	String token1 = auth1.uploadToken(BUCKETNAME);
		    	//创建上传对象
		    	UploadManager uploadManager1 = new UploadManager();
		    	Response r = uploadManager1.put(fileFullPath, path, token1);
				if(r.isOK() && r.statusCode == 200){
					return StringUtil.changeObjectToInt(filePathBean.get("id"));
				}else{
					System.out.println("上传失败，返回的信息是--->"+r.bodyString());
				}
			} catch (QiniuException e) {
				try {
					Response r = e.response;
					// 请求失败时打印的异常的信息
					System.out.println(r.toString());
					//响应的文本信息
				    System.out.println(r.bodyString());
				} catch (QiniuException e1) {
				}
			}
		}
		System.out.println("Single上传任务执行结束...............");
		return id;
    }
    
    /**
     * 单例获取UploadManager对象;
     * @return
     */
    public static UploadManager getUploadManager(){
    	if(uploadManager == null)
    		synchronized (UploadManager.class) {
				if(uploadManager== null)
					uploadManager = new UploadManager();
			}
    	return uploadManager;
    }
	
	/**
	 * 上传小于2M的小文件
	 */
	public final class SingleSmallUploadTask implements Callable<Boolean>{
		private Map<String, Object> bean;
		public SingleSmallUploadTask(Map<String, Object> filePathBean){
			this.bean = filePathBean;
		}

		@Override
		public Boolean call() throws Exception {
			
			String filePath = StringUtil.changeNotNull(bean.get("path"));
			String fullPath = ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER +"file"+ File.separator +filePath;
			Response r = new UploadManager().put(getDataOrFile(fullPath), filePath, token, null, StringUtil.getMime(fullPath), false);
			/*if(r.isOK() && r.statusCode == 200){
				
			    //System.out.println("r.toString():" +r.toString());
			    //System.out.println("r.bodyString():" +r.bodyString());
			    //更新状态标记为已经上传
				if(bean.containsKey("id")){
					moodService.updateUploadQiniu(StringUtil.changeObjectToInt(bean.get("id")), ConstantsUtil.QINIU_SERVER_URL + filePath);
				    System.out.println("小文件上传七牛云存储服务器成功,文件本地路径：" + filePath);
				}
			}*/
			return r.isOK() && r.statusCode == 200;
		}
		
	}
	
	/**
	 * 上传大于2M的小文件
	 */
	public final class SingleBigUploadTask implements Callable<Boolean>{
		private Map<String, Object> bean;
		public SingleBigUploadTask(Map<String, Object> filePathBean){
			this.bean = filePathBean;
		}

		@Override
		public Boolean call() throws Exception {
			String filePath = StringUtil.changeNotNull(bean.get("path"));
			String fullPath = ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER +"file" +File.separator +filePath;
	        File f = new File(fullPath);
            ResumeUploader up = new ResumeUploader(new Client(), token, filePath, f, null, null, null, null);
            Response r = up.upload();
            /*if(r.isOK() && r.statusCode == 200){
            	if(bean.containsKey("id")){
				    //更新状态标记为已经上传
				    filePathService.updateUploadQiniu(StringUtil.changeObjectToInt(bean.get("id")), ConstantsUtil.QINIU_SERVER_URL + filePath);
				    System.out.println("大文件上传七牛云存储服务器成功,文件本地路径：" + filePath);
            	}
			}*/
            System.out.println("大文件上传完成");
			return r.isOK() && r.statusCode == 200;
		}
		
	}
	
	private byte[] getDataOrFile(String filePath) throws IOException, Base64DecodingException {
		if(StringUtil.isNull(filePath))
			return null;
		//读取图片字节数组  
		FileInputStream inputStream = new FileInputStream(filePath);
		byte[] data = new byte[inputStream.available()];  
		inputStream.read(data);  
		inputStream.close();  
		return data;
	}
}
