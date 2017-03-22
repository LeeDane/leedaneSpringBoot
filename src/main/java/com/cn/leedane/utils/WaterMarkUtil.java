package com.cn.leedane.utils;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
/**
 * 图片水印相关的工具类
 * @author LeeDane
 * 2016年7月12日 上午10:33:28
 * Version 1.0
 */
public class WaterMarkUtil {   
  
    /**  
     * @param args  
     */  
    /*public static void main(String[] args) {   
        String srcImgPath = "d:/test/michael/myblog_01.png";   
        String iconPath = "d:/test/michael/blog_logo.png";   
        String targerPath = "d:/test/michael/img_mark_icon.jpg";   
        String targerPath2 = "d:/test/michael/img_mark_icon_rotate.jpg";   
        // 给图片添加水印   
        WaterMarkUtil.markImageByIcon(iconPath, srcImgPath, targerPath);   
        // 给图片添加水印,水印旋转-45   
        WaterMarkUtil.markImageByIcon(iconPath, srcImgPath, targerPath2,   
                -45);   
  
    }   */
  
    /**  
     * 给图片添加水印  
     * @param iconPath 水印图片路径  
     * @param srcImgPath 源图片路径  
     * @param targerPath 目标图片路径  
     */  
    public static void markImageByIcon(String iconPath, String srcImgPath,   
            String targerPath) {   
        markImageByIcon(iconPath, srcImgPath, targerPath, null);   
    }   
  
    /**  
     * 给图片添加水印、可设置水印图片旋转角度  
     * @param iconPath 水印图片路径  
     * @param srcImgPath 源图片路径  
     * @param targerPath 目标图片路径  
     * @param degree 水印图片旋转角度  
     */  
    public static void markImageByIcon(String iconPath, String srcImgPath,   
            String targerPath, Integer degree) {   
        OutputStream os = null;   
        try {   
            Image srcImg = ImageIO.read(new File(srcImgPath));   
  
            BufferedImage buffImg = new BufferedImage(srcImg.getWidth(null),   
                    srcImg.getHeight(null), BufferedImage.TYPE_INT_RGB);   
  
            // 得到画笔对象   
            // Graphics g= buffImg.getGraphics();   
            Graphics2D g = buffImg.createGraphics();     
  
            // 设置对线段的锯齿状边缘处理   
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,   
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);   
  
            g.drawImage(srcImg.getScaledInstance(srcImg.getWidth(null), srcImg   
                    .getHeight(null), Image.SCALE_SMOOTH), 0, 0, null);   
  
            if (null != degree) {   
                // 设置水印旋转   
                g.rotate(Math.toRadians(degree),   
                        (double) buffImg.getWidth() / 2, (double) buffImg   
                                .getHeight() / 2);   
            }   
  
            // 水印图象的路径 水印一般为gif或者png的，这样可设置透明度   
            //ImageIcon imgIcon = new ImageIcon(iconPath);   
  
            // 得到Image对象。   
            //Image img = imgIcon.getImage();   
  
            float alpha = 0.5f; // 透明度   
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,   
                    alpha));   
  
            // 表示水印图片的位置   
            //g.drawImage(img, 150, 300, null);   
  
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));   
  
            g.dispose();   
  
            os = new FileOutputStream(targerPath);   
  
            // 生成图片   
            ImageIO.write(buffImg, "JPG", os);   
  
            System.out.println("图片完成添加Icon印章。。。。。。");   
        } catch (Exception e) {   
            e.printStackTrace();   
        } finally {   
            try {   
                if (null != os)   
                    os.close();   
            } catch (Exception e) {   
                e.printStackTrace();   
            }   
        }   
    }   
    
    public static void main(String[]args) throws Exception{
        
        //1.jpg是你的 主图片的路径
        InputStream is = new FileInputStream("G://head.jpg");
        
        
        //通过JPEG图象流创建JPEG数据流解码器
        JPEGImageDecoder jpegDecoder = JPEGCodec.createJPEGDecoder(is);
        //解码当前JPEG数据流，返回BufferedImage对象
        BufferedImage buffImg = jpegDecoder.decodeAsBufferedImage();
        //得到画笔对象
        Graphics g = buffImg.getGraphics();
        
        //创建你要附加的图象。
        //2.jpg是你的小图片的路径
        //ImageIcon imgIcon = new ImageIcon("2.jpg"); 
        
        //得到Image对象。
        //Image img = imgIcon.getImage();
        
        //将小图片绘到大图片上。
        //5,300 .表示你的小图片在大图片上的位置。
        //g.drawImage(img,5,330,null);
        
        int width = buffImg.getWidth();   
        int height = buffImg.getHeight();  
        System.out.println(width +":"+ height);
        int x = 0,y = 0;
        if(width > 80){
        	x = width - 80;
        }
        
        if(height > 8){
        	y = height - 8;
        }
        
        //设置颜色。
        g.setColor(Color.BLACK);
        
        
        //最后一个参数用来设置字体的大小
        Font f = new Font("宋体",Font.BOLD, 20);
        
        g.setFont(f);
        
        //10,20 表示这段文字在图片上的位置(x,y) .第一个是你设置的内容。
        
        g.drawString("LeeDane",x,y);
        
        g.dispose();
        
        
        
        OutputStream os = new FileOutputStream("G://head1.jpg");
        
        //创键编码器，用于编码内存中的图象数据。
        
        JPEGImageEncoder en = JPEGCodec.createJPEGEncoder(os);
        en.encode(buffImg);
        
        
        is.close();
        os.close();
        
        System.out.println ("合成结束。。。。。。。。");
        
        
    }    
} 