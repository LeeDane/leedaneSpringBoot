package com.cn.leedane.crawl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
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
 * 临时包装散文网的实体(http://www.sanwen.net/)
 * @author LeeDane
 * 2016年7月12日 下午3:33:13
 * Version 1.0
 */
public class SanwenNet extends BaseCrawlBean{
	
	private String title; //文章的标题
	
	public SanwenNet() {
		
	}
	
	public SanwenNet(String url,String title,String id) {
		this.url = url;
	}
	
	@Override
	public void saveFile() {
		try {
			Elements ems = html.select("#article .content");
			for(Element e : ems){
				if(e.select("div script") != null){
					e.select("div script").remove();
				}
			}
			
			ems.select("div:last-child").remove();
			ems.select("p:last-child").remove();
			ems.select("p:last-child").remove();
			if(StringUtil.isNull(title)){
				title = this.getTitle();
				
				//对title再次判断，要是实在还是空的，就直接返回，不再进行下一步的操作
				if(StringUtil.isNull(title)) return;
				
				//对得到的标题进行一系列地转换
				title = "G:/sanwen/"+html.select("#article .title>h1").html().replaceAll("&gt;", "").replaceAll("&lt;", "").replaceAll("　", "").replaceAll("/", "-").replaceAll("\"", "'").replaceAll("\\?", "").replaceAll("？", "").replaceAll(" ", "").replaceAll(":", "-").trim() + ".html";
			}

			//System.out.println("需要保存的文件路径是："+title+",原来的url是："+ this.url);
			File file = new File(title);
				
			if(!file.exists()){
				file.createNewFile();
			}else{
				file.delete();
			}			
			FileOutputStream fot = new FileOutputStream(file);
			//fot.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>".getBytes("utf-8"));
			fot.write(ems.html().getBytes("utf-8"));
			//fot.write("</body></html>".getBytes("utf-8"));
			fot.close();
			
			endTime = System.currentTimeMillis();
			System.out.println("文件"+ title +"保存成html成功！共计耗时："+(endTime - startTime) + "毫秒");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		
		List<String> urls = super.getHomeListsHref("body a");
		List<String> newUrls = new ArrayList<String>();
		for(String u: urls){
			//以“/”开始的，自动补全路径
			if(u.startsWith("/")){
				u = this.url + u.substring(1, u.length());
				newUrls.add(u);
				continue;
			}
			//必须有url前缀才添加
			if(u.startsWith(this.url)){
				newUrls.add(u);
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
