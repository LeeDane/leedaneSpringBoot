package com.cn.leedane.utils;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.cn.leedane.enums.FileType;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 处理base64位图片的类
 * @author LeeDane
 * 2016年7月12日 下午2:39:12
 * Version 1.0
 */
public class Base64ImageUtil {
	private static Logger logger = Logger.getLogger(Base64ImageUtil.class);
	private Base64ImageUtil(){}
	/**
	 * 保存base64位的字符串成图像到本地
	 * @param base64  base64位字符串 
	 * @param account 用户账号
	 * @param pathFolder 保存在本地的文件路径
	 * @throws IOException 
	 * @throws Exception
	 * @return 返回文件名(没有前面的文件夹)
	 */
	public static String saveBase64ToImage(String base64, String account, String pathFolder){// 对字节数组字符串进行Base64解码并生成图片
	    if (StringUtil.isNull(base64) || StringUtil.isNull(account)){ // 图像数据为空
	        return "";
	    }    
	    //文件完整的路径
	    String path = "";
	    //文件的路径(名称+后缀)
	    String fileName = "";
	    if(base64.startsWith(ConstantsUtil.BASE64_JPG_IMAGE_HEAD)){
	    	base64 = base64.substring(ConstantsUtil.BASE64_JPG_IMAGE_HEAD.length(), base64.length() -1);
	    	fileName = buildFileNameNotSuffix(account)+ ".jpg";
	    	path = pathFolder + fileName;
	    }else if(base64.startsWith(ConstantsUtil.BASE64_PNG_IMAGE_HEAD)){
	    	base64 = base64.substring(ConstantsUtil.BASE64_PNG_IMAGE_HEAD.length(), base64.length() -1);
	    	fileName = buildFileNameNotSuffix(account)+ ".png";
	    	path = pathFolder +fileName;
	    }else{
	    	fileName = buildFileNameNotSuffix(account)+ ".jpg";
	    	path = pathFolder + fileName;
	    }
	    
		File file = new File(path);
		OutputStream out = null;
		try {
			if(file.exists()){
				file.createNewFile();
			}
			BASE64Decoder decoder = new BASE64Decoder();
			// Base64解码
			byte[] bytes = decoder.decodeBuffer(base64);
			for (int i = 0; i < bytes.length; ++i) {
			    if (bytes[i] < 0) {// 调整异常数据
			        bytes[i] += 256;
			    }
			}
			// 生成jpeg图片
			out = new FileOutputStream(path);
			out.write(bytes);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(out != null){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	    return fileName;
	}
	
	/**
	 * 将base64位图像字符串保存到固定成30X30的大小保存到本地
	 * @param base64
	 * @param account
	 * @return 保存在本地的文件名称(没有文件夹)
	 * @throws Exception
	 */
	public static String base64ImgTo30x30(String base64, String account) throws Exception {	
		return specificBase64Image(base64, account, 30, 30);
	}
	
	/**
	 * 将base64位图像字符串保存到固定成30X30的大小保存到本地
	 * @param base64
	 * @param account
	 * @return 保存在本地的文件名称(没有文件夹)
	 * @throws Exception
	 */
	public static String base64ImgTo60x60(String base64, String account) throws Exception {	
		return specificBase64Image(base64, account, 60, 60);
	}
	
	/**
	 * 将base64位图像字符串保存到固定成80X80的大小保存到本地
	 * @param base64
	 * @param account
	 * @return 保存在本地的文件名称(没有文件夹)
	 * @throws Exception
	 */
	public static String base64ImgTo80x80(String base64, String account) throws Exception {	
		return specificBase64Image(base64, account, 80, 80);
	}
	/**
	 * 将base64位图像字符串保存到固定成100X100的大小保存到本地
	 * @param base64
	 * @param account
	 * @return 保存在本地的文件名称(没有文件夹)
	 * @throws Exception
	 */
	public static String base64ImgTo100x100(String base64, String account) throws Exception {	
		return specificBase64Image(base64, account, 100, 100);
	}
	/**
	 * 将base64位图像字符串保存到固定成120X120的大小保存到本地
	 * @param base64
	 * @param account
	 * @return 保存在本地的文件名称(没有文件夹)
	 * @throws Exception
	 */
	public static String base64ImgTo120x120(String base64, String account) throws Exception {	
		return specificBase64Image(base64, account, 120, 120);
	}
	
	/**
	 * 将base64位图像字符串保存到固定成320X400的大小保存到本地
	 * @param base64
	 * @param account
	 * @return 保存在本地的文件名称(没有文件夹)
	 * @throws Exception
	 */
	public static String base64ImgTo320x400(String base64, String account) throws Exception {	
		return specificBase64Image(base64, account, 320, 400);
	}
	
	
	/**
	 * 执行生成指定大小的图像
	 * @throws IOException 
	 * @throws Exception
	 */
	public static String specificBase64Image(String base64, String account, int width, int height){
			
		
		//将当前的base64临时保存在本地的文件夹路径
		String tempPathFolder = buildFolderPath(FileType.TEMPORARY.value);
		//真的保存指定大小图像的文件夹路径
		String savePathFolder = buildFolderPath(FileType.FILE.value);
		
		String tempFileName = saveBase64ToImage(base64, account, tempPathFolder);	
		
		String tempPath = tempPathFolder + tempFileName;
		String saveFileName = "";
		String savePath = "";
		if(base64.startsWith(ConstantsUtil.BASE64_JPG_IMAGE_HEAD)){
	    	base64 = base64.substring(ConstantsUtil.BASE64_JPG_IMAGE_HEAD.length(), base64.length() -1);
	    	saveFileName = buildFileNameNotSuffix(account)+ "_" + width + "x" + height + ".jpg";
	    	savePath = savePathFolder + saveFileName ;
	    }else if(base64.startsWith(ConstantsUtil.BASE64_PNG_IMAGE_HEAD)){
	    	base64 = base64.substring(ConstantsUtil.BASE64_PNG_IMAGE_HEAD.length(), base64.length() -1);
	    	saveFileName = buildFileNameNotSuffix(account)+ "_" + width + "x" + height + ".png";
	    	savePath = savePathFolder + saveFileName ;
	    }else{
	    	saveFileName = buildFileNameNotSuffix(account)+ "_" + width + "x" + height + ".jpg";
	    	savePath = savePathFolder + saveFileName ;
	    }
		
		if(!StringUtil.isNull(tempPath)){
			// 输出到文件流
			BufferedOutputStream out = null;
			FileInputStream fileInputStream = null;
			try {
				//Image img = ImageIO.read(tempFile);
				File tempFile = new File(tempPath);
				fileInputStream = new FileInputStream(tempFile);
				BufferedImage img =ImageIO.read(fileInputStream);			
				BufferedImage image = new BufferedImage(width, height, BufferedImage.SCALE_SMOOTH);	
				//输出
				File saveFile = new File(savePath); 
				
				out = new BufferedOutputStream(new FileOutputStream(saveFile)); 

				// 转换成JPEG图像格式
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				
				//高质量的图片
				
				Graphics2D g2 = image.createGraphics();
				
				//scale 为 比例， w为宽度，h为 高度
				g2.drawImage(img, 0, 0, width, height, null); //绘制缩小后的图
						
				JPEGEncodeParam jpeg = encoder.getDefaultJPEGEncodeParam(image);
				
				//设置图片质量，1表示最优
				jpeg.setQuality(1.0f, false);

				encoder.setJPEGEncodeParam(jpeg);
				
				//JPEG编码  
				encoder.encode(image);
				img.flush();
				image.flush();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}finally{
				if(fileInputStream != null){
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(out != null){
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			
			
			return saveFileName;
		}else{
			logger.error("生成临时文件错误");
		}
		return "";
	}
	
	/**
	 * 判断是否是支持的类型
	 * @param fileName
	 * @return
	 */
	public static boolean isSupportType(String fileName){ 
		//获取文件的后缀
		String suffix = fileName.substring(fileName.lastIndexOf(".")+1 , fileName.length());
		for(String supportSuffix : ConstantsUtil.SUPPORTIMAGESUFFIXS){
			//判断是否在支持的类型里面
			if(supportSuffix.equalsIgnoreCase(suffix)) return true;
		}
		
		logger.warn("该文件不是目前系统支持的类型");
		return false;
	}
	
	/**
	 * 获取文件文件夹（后面带/）
	 * @param folder
	 * @return
	 */
	public static String buildFolderPath(String folder){		
		StringBuffer buffer = new StringBuffer();
		buffer.append(ConstantsUtil.DEFAULT_SAVE_FILE_FOLDER);
		buffer.append(folder);
		buffer.append("/");
	    return buffer.toString() ; 
	}
	
	/**
	 * 获取文件的名称(没有后缀的)
	 * @param account
	 * @return
	 */
	public static String buildFileNameNotSuffix(String account){
		Random rd = new Random();
		StringBuffer buffer = new StringBuffer();
		buffer.append(account);
		buffer.append("_");
		buffer.append(UUID.randomUUID());
		buffer.append("_");
		buffer.append(DateUtil.DateToString(new Date(), "yyyyMMdd-HHmmss-S"));	
		buffer.append("-");
		buffer.append(rd.nextInt(1000));
	    return buffer.toString() ; 
	}
	
	/**
	 * 将图像转化成base64格式的字符串
	 * @param inputStream 输入流对象
	 * @param 图像的类型(png/jpg)可以为空，默认是jpg
	 * @return
	 * @throws IOException
	 */
	public static String convertImageToBase64(InputStream inputStream, String type) throws IOException{		
		//读取图片字节数组  
		byte[] data = new byte[inputStream.available()];  
		inputStream.read(data);  
		inputStream.close();  
		
		String suffixs = ConstantsUtil.BASE64_JPG_IMAGE_HEAD;
		if("png".equalsIgnoreCase(type))
			suffixs = ConstantsUtil.BASE64_PNG_IMAGE_HEAD;
		
		//去除默认的换行
		String str = new BASE64Encoder().encode(data).replaceAll("[\n\r]", "");
		return  suffixs + str;//返回Base64编码过的字节数组字符串
	}
	
	/**
	 * 获取指定路径下的图像，将图像转化成base64格式的字符串
	 * @param filePath   根据指定路径下的图片文件
	 * @param type 图像的类型(png/jpg)可以为空，默认是jpg
	 * @return
	 */
	public static String convertImageToBase64(String filePath, String type) throws IOException{		        
		return convertImageToBase64(new FileInputStream(filePath), type);//返回Base64编码过的字节数组字符串	
	}
	
	/**
	 * 获取指定路径下的图像，将图像转化成base64格式的字符串
	 * @param file   根据指定路径下的图片文件
	 * @param type 图像的类型(png/jpg)可以为空，默认是jpg
	 * @return
	 */
	public static String convertImageToBase64(File file, String type) throws IOException{		        
		return convertImageToBase64(new FileInputStream(file), type);//返回Base64编码过的字节数组字符串	
	}
	
	/**
	 * 将base64格式的字符串转化成图像
	 * @param path  图片文件的保存路径
	 * @param fileName  图片的名称
	 * @param image  base64位的字符串
	 * @return
	 * @throws IOException 
	 */
	public static boolean convertBase64ToImage(String filePath,String image) throws IOException {
		 //对字节数组字符串进行Base64解码并生成图片          
		if (image == null) //图像数据为空              
			return false;      
		if(image.contains(ConstantsUtil.BASE64_JPG_IMAGE_HEAD)){
			image = image.substring(ConstantsUtil.BASE64_JPG_IMAGE_HEAD.length(), image.length());
			int lastPointIndex = filePath.lastIndexOf(".");
			filePath = filePath.substring(0, lastPointIndex) + ".jpg";
		}else if(image.contains(ConstantsUtil.BASE64_PNG_IMAGE_HEAD)){
			image = image.substring(ConstantsUtil.BASE64_PNG_IMAGE_HEAD.length(), image.length());
			int lastPointIndex = filePath.lastIndexOf(".");
			filePath = filePath.substring(0, lastPointIndex) + ".png";
		}else{
			int lastPointIndex = filePath.lastIndexOf(".");
			filePath = filePath.substring(0, lastPointIndex) + ".jpg";
		}
		BASE64Decoder decoder = new BASE64Decoder();                      
		//Base64解码              
		byte[] b = decoder.decodeBuffer(image);              
		for(int i=0;i<b.length;++i)              
		{                  
			if(b[i]<0)                  
			{
				//调整异常数据                      
				b[i]+=256;                  
			}              
		}              
		//新生成的图片             
		OutputStream out = new FileOutputStream(filePath); 
		out.write(b);              
		out.flush();              
		out.close();              
		return true;          		
	}
	
	/**
     * 获取图片宽度
     * @param file  图片文件
     * @return 宽度
     */
    public static int getImgWidth(File file) {
        InputStream is = null;
        BufferedImage src = null;
        int ret = -1;
        try {
            is = new FileInputStream(file);
            src = javax.imageio.ImageIO.read(is);
            ret = src.getWidth(null); // 得到源图宽
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
    
    /**
     * 获取图片宽度和高度
     * @param file  图片文件
     * @return 高度
     */
    public static int[] getImgWidthAndHeight(File file) {
    	
    	BufferedImage bis = null; 
    	int[] ret = new int[2];
	    try{ 
	        bis = ImageIO.read(file); 
	        ret[0] = bis.getWidth(); 
	        ret[1] = bis.getHeight(); 
	    }catch(Exception e){ 
	     try{ 
		      ThumbnailConvert tc = new ThumbnailConvert(); 
		      tc.setCMYK_COMMAND(file.getPath()); 
		      Image image =null; 
		      image = Toolkit.getDefaultToolkit().getImage(file.getPath()); 
		      MediaTracker mediaTracker = new MediaTracker(new Container()); 
		      mediaTracker.addImage(image, 0); 
		      mediaTracker.waitForID(0); 
		      ret[0] = image.getWidth(null); 
		      ret[1] = image.getHeight(null); 
		     }catch (Exception e1){ 
		    	 e1.printStackTrace(); 
		     } 
	    } 
	    
        /*InputStream is = null;
        BufferedImage src = null;
        int[] ret = new int[2];
        try {
            is = new FileInputStream(file);
            src = javax.imageio.ImageIO.read(is);
            ret[0] = src.getWidth(null); // 得到源图宽
            ret[1] = src.getHeight(null); // 得到源图高
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return ret;
    }
    
    /**
     * 获取图片高度
     * @param file  图片文件
     * @return 高度
     */
    public static int getImgHeight(File file) {
        InputStream is = null;
        BufferedImage src = null;
        int ret = -1;
        try {
            is = new FileInputStream(file);
            src = javax.imageio.ImageIO.read(is);
            ret = src.getHeight(null); // 得到源图高
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
    }
	/*public static void main(String[] args) {
		String account = "leedane";
		String imageStr = "data:image/jpeg;base64,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";
		try {
			String path30 = base64ImgTo30x30(imageStr, account);
			logger.info("path30:" +path30);
			String path60 = base64ImgTo60x60(imageStr, account);
			logger.info("path60:" +path60);
			
			String path80 = base64ImgTo80x80(imageStr, account);
			logger.info("path80:" +path80);
			
			String path100 = base64ImgTo100x100(imageStr, account);
			logger.info("path100:" +path100);
			
			String path120 = base64ImgTo120x120(imageStr, account);
			logger.info("path120:" +path120);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/*public static void main(String[] args) throws Exception {
		
		String path = "D://dd.jpg";
		String base64Str = convertImageToBase64(path, null);
		long start = System.currentTimeMillis();
		//String paths = base64ImgTo320x400(base64Str, "dane");
		logger.info(base64Str);
		long end = System.currentTimeMillis();
		//logger.info(paths+"总计耗时:" + (end - start)+"毫秒");		
	}*/
}
