package com.cn.leedane.test;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.StringUtil;
import com.qiniu.TempFile;
import com.qiniu.TestConfig;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Client;
import com.qiniu.http.Response;
import com.qiniu.storage.ResumeUploader;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.sun.org.apache.xml.internal.security.exceptions.Base64DecodingException;

/**
 * 七牛云存储相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:28:20
 * Version 1.0
 */
public class QiniuTest extends BaseTest{

	public static void main(String[] args) {
		String accessKey = "aMgOWQCqz6CPIzjKbfLbQDbD0Jf9CD0P7DBA060W";
		String secretKey = "4TlTuQqE5s8r1bn3M82-3EQmNw22KzV6oIRBL3Pr";
		String bucketName = "leedane";
		try {
			test(accessKey, secretKey, bucketName);
		} catch (Base64DecodingException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void test(String accessKey, String secretKey, String bucketName) throws IOException, Base64DecodingException{
		UploadManager uploadManager = new UploadManager();
	    Auth auth = Auth.create(accessKey, secretKey);
	    String token = auth.uploadToken(bucketName);
	    //Response r = uploadManager.put("hello world".getBytes(), "yourkey", token);
	   
	    Response r = uploadManager.put(getDataOrFile(), "1_82378545124366_20160119184016_myJava.pdf", token, null, StringUtil.getMime(ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER +"file//1_82378545124366_20160119184016_myJava.pdf"), false);
	    MyRet ret = r.jsonToObject(MyRet.class);
	    System.out.println(ret.fname);
	}
	
	private static byte[] getDataOrFile() throws IOException, Base64DecodingException {
		//leedane_4eebe109-6f8d-48f1-8fc7-fe822461f1b3_20160219-175431-267-676.jpg
		//leedane_b55c3c7c-4850-4e69-b899-cbb234bad662_20160219-175730-5-227.jpg
		String filePath = ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER +"file//1_82378545124366_20160119184016_myJava.pdf";
		//return convertImageToBase64(filePath, "jpg");
		//读取图片字节数组  
		FileInputStream inputStream = new FileInputStream(filePath);
		byte[] data = new byte[inputStream.available()];  
		inputStream.read(data);  
		inputStream.close();  
		return data;
	}

	class MyRet {
		public String hash;
        public String key;
        public String fsize;
        public String fname;
        public String mimeType;
	}
	
	/**
	 * 获取指定路径下的图像，将图像转化成base64格式的字符串
	 * @param filePath   根据指定路径下的图片文件
	 * @param type 图像的类型(png/jpg)可以为空，默认是jpg
	 * @return
	 */
	public static byte[] convertImageToBase64(String filePath, String type) throws IOException{		        
		return convertImageToBase64(new FileInputStream(filePath), type);//返回Base64编码过的字节数组字符串	
	}
	
	/**
	 * 将图像转化成base64格式的字符串
	 * @param inputStream 输入流对象
	 * @param 图像的类型(png/jpg)可以为空，默认是jpg
	 * @return
	 * @throws IOException
	 */
	public static byte[] convertImageToBase64(InputStream inputStream, String type) throws IOException{		
		//读取图片字节数组  
		byte[] data = new byte[inputStream.available()];  
		inputStream.read(data);  
		inputStream.close();  
		return data;//返回Base64编码过的字节数组字符串
	}
	
	 String ACCESS_KEY = "aMgOWQCqz6CPIzjKbfLbQDbD0Jf9CD0P7DBA060W";
	 String SECRET_KEY = "4TlTuQqE5s8r1bn3M82-3EQmNw22KzV6oIRBL3Pr";
	 Auth auth = Auth.create(ACCESS_KEY, SECRET_KEY);
	 UploadManager uploadManager = new UploadManager();
	 
	 // 覆盖上传
	 /*可以参考七牛的上传策略
	 http://developer.qiniu.com/docs/v6/api/reference/security/put-policy.html
	 指定上传的目标资源空间（Bucket）和资源键名（Key）。
	 有两种格式：
	 ● <bucket>，表示允许用户上传文件到指定的 bucket。在这种格式下文件只能“新增”，若已存在同名资源则会失败。
	 ● <bucket>:<key>，表示只允许用户上传指定key的文件。在这种格式下文件默认允许“修改”，已存在同名资源则会被本次覆盖。如果希望只能上传指定key的文件，并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1。*/

	  private String getUpToken(){
	      return auth.uploadToken("leedane", "leedane", 312600, new StringMap().put("insertOnly", "1"));
	  }

	  public void upload() throws QiniuException{

	   Response res = uploadManager.put("G:\\head.jpg", "fhre.jpg", getUpToken());
	   System.out.println(getUpToken());
	   System.out.println(res.bodyString());

	  }

	  /*public static void main(String args[]) throws QiniuException{
	   new QiniuTest().upload();
	  } */
	  

	    private void template(int size1) throws IOException {
	        final String expectKey = "1_82378545124366_20160119184016_myJava.pdf";
	        final File f = new File(ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER +"//file//1_82378545124366_20160119184016_myJava.pdf");
	        final String returnBody = "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"fsize\":\"$(fsize)\""
	                + ",\"fname\":\"$(fname)\",\"mimeType\":\"$(mimeType)\"}";
	        String token = TestConfig.testAuth.uploadToken(TestConfig.bucket, expectKey, 3600,
	                new StringMap().put("returnBody", returnBody));

	        try {
	        	System.out.println("开始...");
	            ResumeUploader up = new ResumeUploader(new Client(), token, expectKey, f, null, null, null, null);
	            Response r = up.upload();
	            MyRet ret = r.jsonToObject(MyRet.class);
	            System.out.println("Key:"+ret.key+",name:"+ret.fname);
	        } catch (QiniuException e) {
	            e.printStackTrace();
	        }
	        TempFile.remove(f);
	    }

	    @org.junit.Test
	    public void test1K() throws Throwable {
	        template(1);
	    }

	    @org.junit.Test
	    public void test600k() throws Throwable {
	        template(600);
	    }

	    @org.junit.Test
	    public void test4M() throws Throwable {
	        if (TestConfig.isTravis()) {
	            return;
	        }
	        template(1024 * 4);
	    }

	    @org.junit.Test
	    public void test8M1k() throws Throwable {
	        if (TestConfig.isTravis()) {
	            return;
	        }
	        template(1024 * 8 + 1);
	    }
}
