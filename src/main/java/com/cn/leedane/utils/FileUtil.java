package com.cn.leedane.utils;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import com.cn.leedane.handler.CloudStoreHandler;
import com.cn.leedane.model.GalleryBean;
import com.cn.leedane.model.UserBean;

/**
 * 文件工具类(文件的读取操作)
 * @author LeeDane
 * 2016年7月12日 上午10:27:49
 * Version 1.0
 */
public class FileUtil {
	
	/**
	 * 读取文件(文件夹)大小
	 * @param file
	 * @return KB
	 */
	public static Long getFileSizeFormatKB(File file) {
		return getFileSize(file) / 1024;
	}

	/**
	 * 读取文件(文件夹)大小
	 * @param file
	 * @return MB
	 */
	public static Long getFileSizeFormatMB(File file) {
		return getFileSizeFormatKB(file) / 1024;
	}

	/**
	 * 读取文件(文件夹)大小
	 * @param file
	 * @return Byte
	 */
	public static Long getFileSize(File file) {
		long size = 0;
		try {
			if (!file.exists()) {
				return size;
			}

			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					size += getFileSize(files[i]);
				}
			} else {
				size = file.length();
			}
		} catch (Exception ex) {

		}
		return new Long(size);
	}
	
	/**
	 * 读取文件(文件夹)文件数
	 * @param file
	 * @return
	 */
	public static int getFileNumber(File file) {
		int size = 0;
		try {
			if (!file.exists()) {
				return size;
			}

			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					size += getFileNumber(files[i]);
				}
			} else {
				size = 1;
			}
		} catch (Exception ex) {

		}
		return size;
	}
	
	/**
	 * 判断文件是否存在
	 * @param fileName
	 * @return
	 */
	public static boolean isExist(String fileName) {
		try {
			if (fileName == null || fileName.equals("")) {
				return false;
			}
			File file = new File(fileName);
			if (file.exists() && file.isFile()) {
				return true;
			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	/**
	 * 从指定的文件路径中加载字符串文件返回
	 * @param filePath 文件的路径
	 * @return
	 * @throws IOException 
	 */
	public static String getStringFromPath(String filePath) throws IOException{
		StringBuffer bf = new StringBuffer();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(filePath));
		BufferedReader bufferedReader = new BufferedReader(reader);
		String text = "";
		while( (text = bufferedReader.readLine()) != null){
			bf.append(text);
		}		
		reader.close();
		return bf.toString();	
	}
	
	/**
	 * 读取断点文件进流中
	 * @param filePath
	 * @param out
	 * @throws IOException
	 */
	public static boolean readFile(String filePath, FileOutputStream out) throws IOException{
		boolean result = false;
		try{
			DataInputStream in = new DataInputStream(new FileInputStream(filePath)); 
			int bytes = 0;  
	        byte[] buffer = new byte[1024];  
	        while ((bytes = in.read(buffer)) != -1) {  
	            out.write(buffer, 0, bytes);  
	        }  
	        out.flush();
	        in.close(); 
	        
	        /*//删除文件
	        File file = new File(filePath);
	        if(file.exists())
	        	file.deleteOnExit();*/
	        
	        result = true;
		}catch(Exception e){
			e.printStackTrace();
		}
		
        return result;
	}
	
	/**
	 * 获取网络图片的宽和高以及大小
	 * @param imgUrl
	 * @return 返回数组，依次是：宽，高，大小
	 */
	@Deprecated
	public static long[] getNetWorkImgAttr(String imgUrl) {
		
		long[] result = new long[3];
		boolean b = false;
        if(StringUtil.isNotNull(imgUrl) && ImageUtil.isSupportType(StringUtil.getFileName(imgUrl))){
        	
        	StringBuffer tempFilePath = new StringBuffer();
        	tempFilePath.append(ConstantsUtil.getDefaultSaveFileFolder());
        	tempFilePath.append("temporary");
        	tempFilePath.append(File.separator);
        	tempFilePath.append(UUID.randomUUID().toString());
        	tempFilePath.append("_");
        	tempFilePath.append(StringUtil.getFileName(imgUrl));
        	try {
        		InputStream inputStream = HttpUtil.getInputStream(imgUrl);
        		if(inputStream != null){
        			//载入图片到输入流
                    java.io.BufferedInputStream bis = new BufferedInputStream(inputStream);
                    //实例化存储字节数组
                    byte[] bytes = new byte[1024];
                    //设置写入路径以及图片名称
                    OutputStream bos = new FileOutputStream(new File(tempFilePath.toString()));
                    int len;
                    while ((len = bis.read(bytes)) > 0) {
                        bos.write(bytes, 0, len);
                    }
                    inputStream.close();
                    bis.close();
                    bos.flush();
                    bos.close();
                    //关闭输出流
                    b=true;
        		}
            } catch (Exception e) {
            	e.printStackTrace();
                //如果图片未找到
                b=false;
            }
            
            if(b){    
            	//图片存在
                //得到文件
                java.io.File file = new java.io.File(tempFilePath.toString());
                BufferedImage bi = null;
                try {
                    //读取图片
                    bi = javax.imageio.ImageIO.read(file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                result[0] = bi.getWidth(); //获得 宽度
                result[1] = bi.getHeight(); //获得 高度
                result[2] = file.length(); //获取文件大小
                //删除文件
                file.delete();
            }
        }
        
       return result;

    }
	
	/**
	 * 获取网络图片的详细信息，用GalleryBean返回
	 * @param imgUrl
	 * @return galleryBean
	 */
	public static GalleryBean getNetWorkImgAttrs(UserBean user, String imgUrl) {
		
		GalleryBean galleryBean = null;
		boolean b = false;
        if(StringUtil.isNotNull(imgUrl) && ImageUtil.isSupportType(StringUtil.getFileName(imgUrl))){
        	
        	StringBuffer tempFilePath = new StringBuffer();
        	tempFilePath.append(ConstantsUtil.getDefaultSaveFileFolder());
        	tempFilePath.append("temporary");
        	tempFilePath.append(File.separator);
        	tempFilePath.append(UUID.randomUUID().toString());
        	tempFilePath.append("_");
        	tempFilePath.append(StringUtil.getFileName(imgUrl));
        	try {
        		InputStream inputStream = HttpUtil.getInputStream(imgUrl);
        		if(inputStream != null){
        			//载入图片到输入流
                    java.io.BufferedInputStream bis = new BufferedInputStream(inputStream);
                    //实例化存储字节数组
                    byte[] bytes = new byte[1024];
                    //设置写入路径以及图片名称
                    OutputStream bos = new FileOutputStream(new File(tempFilePath.toString()));
                    int len;
                    while ((len = bis.read(bytes)) > 0) {
                        bos.write(bytes, 0, len);
                    }
                    inputStream.close();
                    bis.close();
                    bos.flush();
                    bos.close();
                    //关闭输出流
                    b=true;
        		}
            } catch (Exception e) {
            	e.printStackTrace();
                //如果图片未找到
                b=false;
            }
            
            if(b){
            	galleryBean = new GalleryBean();
            	//图片存在
                //得到文件
                java.io.File file = new java.io.File(tempFilePath.toString());
                BufferedImage bi = null;
                try {
                    //读取图片
                    bi = javax.imageio.ImageIO.read(file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                //不是七牛存储的互联网连接，先将文件上传到服务器
                if(imgUrl.indexOf(ConstantsUtil.QINIU_SERVER_URL_NO_HTTP) < 0)
                	imgUrl = CloudStoreHandler.uploadFile(user, file);
                
                galleryBean.setPath(imgUrl);
               
                galleryBean.setWidth(bi.getWidth()); //获得 宽度
                galleryBean.setHeight(bi.getHeight()); //获得 高度
                galleryBean.setLength(file.length()); //获取文件大小
                //删除文件
                file.delete();
            }
        }
        
       return galleryBean;

    }

	/**
	 * 将文本写入本地
	 * @param file
	 * @param text
	 * @throws IOException 
	 */
	public static void textToFile(File file, String text) throws IOException{
		PrintWriter pw = new PrintWriter( new FileWriter(file) );
        pw.print(text);
        pw.close();
	}
}
