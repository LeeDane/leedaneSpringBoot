package com.cn.leedane.utils;
import java.util.Calendar;
import java.util.Date;

/**
 * web版本的主图背景
 * @author LeeDane
 * 2016年10月19日 上午10:20:50
 * Version 1.0
 */
public class WebBackground {
	
	//七天的图像列表
	private static final String[] IMAGES = new String[]{
		"main_content_bg.jpg", "main_content_bg_02.jpg", "main_content_bg_03.jpg",
		"main_content_bg_04.jpg", "main_content_bg_05.jpg", "main_content_bg_06.jpg",
		"main_content_bg_07.jpg"
	};
	
	public String image = null;

	public WebBackground(){
		init();
	}
	
	/**
	 * 获取当天的背景
	 */
	public void init(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (w < 0)
			w = 0;
		
		image = (ConstantsUtil.IS_DEBUG? "page/images/": "http://pic.onlyloveu.top/page_images_") +IMAGES[w];
	}
	
	public synchronized String getImage() {
		return image;
	}
}
