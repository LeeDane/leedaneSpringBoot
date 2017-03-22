package com.cn.leedane.crawl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 抓取网页的信息
 * @author LeeDane
 * 2016年7月12日 下午3:32:47
 * Version 1.0
 */
public class CrawlPage {
	
	public static void CrawlOSC() throws IOException{
		/*Connection.Response res = Jsoup.connect("https://www.oschina.net/action/user/hash_login").data("email", "825711424@qq.com","pwd","0d81b75e92e7a2757381e1b2e892be514ff0b3f4","save_login","1").method(Method.POST).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").timeout(12000).execute();
		//Document doc = Jsoup.parse(res.body());//转换为Dom树
		//这儿的SESSIONID需要根据要登录的目标网站设置的session Cookie名字而定
		String oscid = res.cookie("oscid"); 
		
		System.out.println("oscid:" + oscid);*/
		/*String oscid = "2F";*/
		Document objectDoc = Jsoup.connect("http://www.oschina.net/")
			    //.cookie("oscid", oscid)
			    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").timeout(12000)
			    .get();
		List<Element> ems = objectDoc.select("#NewCodeList ul a");
		for(Element em : ems){
			System.out.println("Element:" + em.attr("href"));
		}
	
	}
	
	public static void CrawlSanwen() throws IOException{
		/*Connection.Response res = Jsoup.connect("https://www.oschina.net/action/user/hash_login").data("email", "825711424@qq.com","pwd","0d81b75e92e7a2757381e1b2e892be514ff0b3f4","save_login","1").method(Method.POST).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").timeout(12000).execute();
		//Document doc = Jsoup.parse(res.body());//转换为Dom树
		//这儿的SESSIONID需要根据要登录的目标网站设置的session Cookie名字而定
		String oscid = res.cookie("oscid"); 
		
		System.out.println("oscid:" + oscid);*/
		/*String oscid = "2F";*/
		
		
		long startTime = System.currentTimeMillis();
		Document objectDoc = Jsoup.connect("http://www.sanwen.net/subject/3765049/")
			    //.cookie("oscid", oscid)
			    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31").timeout(12000)
			    .get();
		Elements ems = objectDoc.select("#article .content");
		//ems.select("div script").remove();
		for(Element e : ems){
			if(e.select("div script") != null){
				e.select("div script").remove();
			}
		}
		
		ems.select("div:last-child").remove();
		ems.select("p:last-child").remove();
		ems.select("p:last-child").remove();
		ems.select("p:last-child").html(null);
			//em.remov
		File file = new File("F:/sanwen/"+objectDoc.select("#article .title>h1").html()+".html");
			
			if(!file.exists()){
				file.createNewFile();
			}else{
				file.delete();
			}
			//FileInputStream fin = new FileInputStream(file);
			
			FileOutputStream fot = new FileOutputStream(file);
			fot.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>".getBytes("utf-8"));
			fot.write(ems.html().getBytes("utf-8"));
			fot.write("</body></html>".getBytes("utf-8"));
			fot.close();
			long endTime = System.currentTimeMillis();
			System.out.println("将文件保存成html成功！共计耗时："+(endTime - startTime) + "毫秒");
		
	
	}

	public static void main(String[] args) throws IOException {
		//CrawlPage.CrawlOSC();
		CrawlPage.CrawlSanwen();
	}
	
}
