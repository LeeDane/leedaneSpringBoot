package com.cn.leedane.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * 图像相关的工具类
 * @author LeeDane
 * 2016年7月12日 上午10:29:10
 * Version 1.0
 */
public class ImageUtil {
	
	//记录开始的时间
	private long startTime; 
	
	//记录结束的时间
	private long endTime; 
	
	//图片对象
	private Image img;
	
	//原来存放图片文件的路径
	private String oldPath;
	
	//原来的图片名称
	private String oldFileName;
	
	//新的存放图片文件的路径
	private String newPath;
	
	//新的图片名称
	private String newFileName;
	
	//图片宽度
	private int width;
	
	//图片高度
	private int height;
	
	//是否等比缩放
	private boolean proportion;

	//等比缩放比例，当设置proportion=true后记得要设置这个值,默认是1
	//设置的比例一定要大于0，否则将默认是1.0,(0,1)是缩小，大于1是放大
	private float proportionPer = 1.0f;
	
	//设置图片的处理类型，默认是SCALE_SMOOTH
	private int imageType = BufferedImage.SCALE_SMOOTH;
	
	//是否显示高质量图片
	private boolean highQuality;  
	
	//显示高质量图片的等级，1.0是最高，0.1是低的
	private float highQualityPer = 1.0f;

	//默认处理的图片宽度,600像素
	public static final int DEFAULT_WIDTH = 600;
	
	//默认处理的图片高度,400像素
	public static final int DEFAULT_HEIGHT = 400;
	
	//默认新文件需要添加的后缀
	public static final String DEFAULT_NEW_FILENAME = "_new";
	
	//double rate1 = ((double) img.getWidth(null)) / (double) outputWidth + 0.1;
	
	
	/**
	* 将大图片转换成大图片(小图片)，将使用默认的宽度、高度，以及在同一路径下
	 * ，将图片文件名称的后面加上"_new"作为新的文件名称，如“a.jpg”换成“a_new.jpg”
	 * @param oldPath  原来存放图片文件的路径
	 * @param oldFileName  原来的图片名称
	 */
	public ImageUtil(String oldPath,String oldFileName) {
		startTime = System.currentTimeMillis();
		this.oldPath = oldPath;
		this.oldFileName = oldFileName;
		this.newPath = oldPath;		
		this.newFileName = buildNewFileName(oldFileName);
		this.width = ImageUtil.DEFAULT_WIDTH;
		this.height = ImageUtil.DEFAULT_HEIGHT;	
	}
	
	/**
	 * 将大图片转换成大图片(小图片),将使用默认的宽度和高度。
	 * @param oldPath  原来存放图片文件的路径
	 * @param oldFileName  原来的图片名称
	 * @param newPath 新的存放图片文件的路径
	 * @param newFileName 新的图片名称
	 */
	public ImageUtil(String oldPath,String oldFileName,String newPath,String newFileName) {
		startTime = System.currentTimeMillis();
		this.oldPath = oldPath;
		this.oldFileName = oldFileName;
		this.newPath = newPath;		
		this.newFileName = newFileName;
		this.width = ImageUtil.DEFAULT_WIDTH;
		this.height = ImageUtil.DEFAULT_HEIGHT;	
	}
	
	/**
	 * 将大图片转换成大图片(小图片)
	 * @param oldPath  原来存放图片文件的路径
	 * @param oldFileName  原来的图片名称
	 * @param newPath 新的存放图片文件的路径
	 * @param newFileName 新的图片名称
	 */
	public ImageUtil(String oldPath,String oldFileName,String newPath,String newFileName,int newWidth,int newHeight) {
		startTime = System.currentTimeMillis();
		this.oldPath = oldPath;
		this.oldFileName = oldFileName;
		this.newPath = newPath;		
		this.newFileName = newFileName;
		this.width = newWidth;
		this.height = newHeight;	
	}
	
	
	/**
	 * 将大图片转化成小图片
	 * @return
	 */
	public boolean ConverBigImageToSmallImage() {
		//文件名和路径只要有空的值就直接返回false
		if(oldFileName == "" || oldPath == "") return false;
		
		//如果文件名的后缀不是支持的类型，也直接返回false
		if(!this.isSupportType(oldFileName)) return false;
		
		//拼接成完整的旧图片文件路径
		String fullOldPath = oldPath + "\\" + oldFileName;
		
		//拼接成完整的新图片文件路径
		String fullNewPath = newPath + "\\" + newFileName;
		
		File file = new File(fullOldPath);
		
		//定义流
		BufferedOutputStream out = null;
		try{
			img = ImageIO.read(file);
						
			//是等比缩放
			if(this.isProportion()){
				this.proportionPer = proportionPer > 0.0f && proportionPer < 1.0f ? proportionPer : 1.0f;
				width = (int) ((float)img.getWidth(null) * proportionPer);
				height = (int)((float)img.getHeight(null) * proportionPer);
			}
			
			BufferedImage image = new BufferedImage(width, height, getImageType());	
			
			File destFile = new File(fullNewPath); 
			
			// 输出到文件流
			out = new BufferedOutputStream(new FileOutputStream(destFile)); 

			// 转换成JPEG图像格式
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			
			//是否需要高质量的图片
			if(!isHighQuality()){		
				// 绘制缩小后的图 
				image.getGraphics().drawImage(img, 0, 0, width, height, null); 
			}
			else{
				Graphics2D g2 = image.createGraphics();
				
				//scale 为 比例， w为宽度，h为 高度
				g2.drawImage(img, 0, 0, width, height, null); //绘制缩小后的图
						
				JPEGEncodeParam jpeg = encoder.getDefaultJPEGEncodeParam(image);
				
				//设置图片质量，1表示最优，0.5表示中等
				jpeg.setQuality(getHighQualityPer(), false);

				encoder.setJPEGEncodeParam(jpeg);
			}
			//JPEG编码  
			encoder.encode(image);
			
			out.flush();
		}catch(IOException e){
			System.out.println("图片压缩出现异常");
		}finally{
			try {
				if(out != null)
					out.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	
		endTime = System.currentTimeMillis();
		System.out.println("处理"+ oldFileName +"需要花费的时间："+(endTime - startTime) + "毫秒");
		return true;
	}
	
	/**
	 * 根据旧的oldFileName创建出新的newFileName
	 * @param oldFileName
	 */
	private String buildNewFileName(String oldFileName) {
		return oldFileName.substring(0, oldFileName.lastIndexOf(".")) + ImageUtil.DEFAULT_NEW_FILENAME +"." 
				+ oldFileName.substring(oldFileName.lastIndexOf(".")+1 , oldFileName.length());
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
		
		System.out.println("该文件不是目前系统支持的类型");
		return false;
	}

	public boolean isProportion() {
		return proportion;
	}

	public void setProportion(boolean proportion) {
		this.proportion = proportion;
	}

	public float getProportionPer() {
		return proportionPer;
	}

	public void setProportionPer(float proportionPer) {
		this.proportionPer = proportionPer;
	}

	/**
	 * 获取需要处理图片的类型
	 * @return
	 */
	public int getImageType() {
		return imageType;
	}

	public void setImageType(int imageType) {
		this.imageType = imageType;
	}

	public boolean isHighQuality() {
		return highQuality;
	}

	public void setHighQuality(boolean highQuality) {
		this.highQuality = highQuality;
	}

	public float getHighQualityPer() {
		return highQualityPer;
	}

	public void setHighQualityPer(float highQualityPer) {
		this.highQualityPer = highQualityPer;
	}
	
}
