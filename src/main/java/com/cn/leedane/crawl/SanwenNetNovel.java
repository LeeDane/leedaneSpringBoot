package com.cn.leedane.crawl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.utils.Base64ImageUtil;
import com.cn.leedane.utils.HttpUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 包装散文网小说的实体(http://www.sanwen.net/novel/)
 * @author LeeDane
 * 2016年7月12日 下午3:33:21
 * Version 1.0
 */
public class SanwenNetNovel extends BaseCrawlBean{
	
	private String title; //文章的标题
	
	public SanwenNetNovel() {
		
	}
	
	public SanwenNetNovel(String url,String title,String id) {
		this.url = url;
	}
	
	@Override
	public void saveFile() {
		
	}
	
	public String getTitle() {
		return html.select("#article .title h1").text();
	}
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * 根据url获取文章ID
	 * @return
	 * @throws ErrorException
	 */
	public String getIdFromUrl() throws ErrorException {
		try{
			String selUrl = "";
			if(url.endsWith("/")){
				selUrl = url.substring(0, url.length()-1);	
			}else{
				selUrl = url;
			}
			int n = selUrl.lastIndexOf("/");
			id = selUrl.substring(n+1, selUrl.length());
		}catch(Exception e){
			throw new ErrorException("解析url:"+url+"获取文章的ID出现异常");
		}
		
		return id;
	}
	
	/**
	 * 获得首页的<a href="">
	 * @param ems
	 * @throws IOException 
	 */
	public List<String> getHomeListsHref() throws IOException {
		
		//获取body中的a标签
		Elements ems = html.select(".categorylist ul> li");		
		String l = "";
		List<String> newUrls = new ArrayList<String>();
		for(Element e: ems){
			Elements aLinks = e.select("h3 a");
			if(aLinks.size() == 2){
				l = aLinks.get(1).attr("href");
				if(StringUtil.isNull(l)) 
					continue;	
				//以“/”开始的，自动补全路径
				if(l.startsWith("/")){
					l = "http://www.sanwen.net/" + l.substring(1, l.length());
					newUrls.add(l);
					continue;
				}
				//必须有url前缀才添加
				if(l.startsWith("http://www.sanwen.net/")){
					newUrls.add(l);
				}
			}
		}
		return newUrls;
	}
	public String getContent(String cssSelect, String... cssRemoves) {
		String content = super.getContent(cssSelect, cssRemoves);
		
		//需要对content的图片进行base64位的编译，不然直接范文不了图片地址
		/*if(!StringUtil.isNull(content)){
			Document contentHtml = Jsoup.parse(content);
			if(contentHtml != null){ 
				Elements elements = contentHtml.select("img");
				String imgUrl = null;
				String base64 = null;
				for(Element element: elements){
					imgUrl = element.attr("src");
					try {
						base64 = Base64ImageUtil.convertImageToBase64(HttpUtil.getInputStream(imgUrl), StringUtil.getSuffixs(imgUrl));			
						if(StringUtil.isNotNull(base64)){
							element.attr("src", base64);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
				
				content = contentHtml.html();
			}
		}*/
		return content;
	}
	
	@Override
	public String getMainImg(String cssSelect, int num) {
		String img = super.getMainImg(cssSelect, num);
		if(img !=null && img.contains("sanwen.net")){
			return img;
		}
		return null;
	}
	
	/*public static void main(String[] args) {
		
		
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		SanwenNet sanwen = null;
		for(int i = 3600000; i < 3765049; i++){
			sanwen = new SanwenNet("http://www.sanwen.net/subject/"+ i +"/","","");

			try {
				sanwen.execute();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
			
		threadPool.execute(sanwen);

	}*/

	@Override
	public void run() {
		
		try {
			execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
