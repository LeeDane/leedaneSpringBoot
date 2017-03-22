package com.cn.leedane.crawl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cn.leedane.exception.ErrorException;

/**
 * 临时包装红袖添香的实体(http://www.hongxiu.com/)
 * @author LeeDane
 * 2016年7月12日 下午3:32:56
 * Version 1.0
 */
public class HongXiu implements Runnable{
	
	//超时时间限制
	public static final int TIME_OUT = 12000;
	
	//执行挖掘的最大深度
	public static final int MAX_DEP = 2;
	
	//当前的深度
	public int current_dep = 1;
	
	//模拟浏览器的的user-agent
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";
	
	private String url; //文章的url路径
	private String title; //文章的标题
	private String id; //文章的id（可以从URL中截取）
	public int count = 1;
	//private int commentNum = 0;   //评论数
	//private int digg = 0; //顶的数量
	//private int bury = 0; //踩的数量
	
	private Document html;  //内容的文档信息
	
	public HongXiu(String url,String title,String id) {
		this.url = url;
		this.title = title;
		try {
			this.id = id == null || id.equals("") ? getIdFromUrl() : id;
		} catch (ErrorException e) {
			e.printStackTrace();
		}
	}
	
	long startTime;  //开始时间
	long endTime;  //结束时间
	
	/**
	 * 执行爬取数据
	 * @throws IOException 
	 */
	public void execute() throws IOException {
		
		if(this.url == null || this.url.equals("")) 
			throw new NullPointerException("爬取数据的url不能为空！");
		
		startTime = System.currentTimeMillis();
		html = Jsoup.connect(this.url)
			    .userAgent(HongXiu.USER_AGENT).timeout(HongXiu.TIME_OUT).get();
		//parse();
		//getCommentNum();
	}
	
	/**
	 * 解析html的content数据
	 * @throws IOException 
	 */
	public void parse() throws IOException {
		Elements content = html.select("#divedit #divcontent");
		/*for(Element e : content){
			if(e.select("div script") != null){
				e.select("div script").remove();
			}
		}*/
		
		/*ems.select("div:last-child").remove();
		ems.select("p:last-child").remove();
		ems.select("p:last-child").remove();*/
		
		title = title == null || title.equals("") ? "F:/hongxiu/"+html.select("#htmltimu").html()+ count +".html" : title;
		count = count + 1;
		File file = new File(title);
			
		if(!file.exists()){
			file.createNewFile();
		}else{
			file.delete();
		}			
		FileOutputStream fot = new FileOutputStream(file);
		fot.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>".getBytes("utf-8"));
		fot.write(content.html().getBytes("utf-8"));
		fot.write("</body></html>".getBytes("utf-8"));
		fot.close();
		endTime = System.currentTimeMillis();
		System.out.println("文件"+ title +"保存成html成功！共计耗时："+(endTime - startTime) + "毫秒");
		this.getListsHref();
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
	public static void main(String[] args) {
		
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		HongXiu hongxiu = new HongXiu("http://article.hongxiu.com","","");

		try {
			//hongxiu.getListsHref(null);
			hongxiu.execute();
			hongxiu.getListsHref();
			//System.out.println(hongxiu.score());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		threadPool.execute(hongxiu);
		
	}
	
	/**
	 * 获得一定深度的<a href="">
	 * @param ems
	 * @throws IOException 
	 */
	private void getListsHref() throws IOException {
		Elements ems = html.select("body a");
		
		if(current_dep <= MAX_DEP){
			for(Element e: ems){
				if(e.attr("href").contains("hongxiu.com") && e.attr("href").contains(".html") && !e.attr("href").contains("www.hongxiu.com")){
					//HongXiu hongXiu = new HongXiu(, "", "");
					url = e.attr("href");
					execute();
					parse();
					
				}
				//hongXiu.getListsHref();
				//getListsHref();
				current_dep++;
			}			
		}
		return;	
	}
	

	/**
	 * 获取评论数
	 * @return
	 */
	private int getCommentNum() {
		String commentHtml = "";
		Elements cn = html.select("#pagelet-comment .cc");
		if(cn.hasText()){
			commentHtml = cn.html();
			if(commentHtml.contains("条评论")){
				commentHtml = commentHtml.substring(0, cn.html().length()-3).trim();
			}
		}
		return Integer.parseInt(commentHtml);
	}
	
	
	/**
	 * 获取顶的数量
	 * @return
	 */
	private int getDigg() {
		String diggHtml = "";
		Elements cn = html.select("#pagelet-like .count");
		if(cn.size() == 2){
			diggHtml = cn.get(0).html().trim();
		}	
		return Integer.parseInt(diggHtml);
	}

	/**
	 * 获取踩的数量
	 * @return
	 */
	private int getBury() {
		String buryHtml = "";
		Elements cn = html.select("#pagelet-like .count");
		if(cn.size() == 2){
			buryHtml = cn.get(1).html().trim();
		}	
		return Integer.parseInt(buryHtml);
	}
	
	/**
	 * 获取该篇文章的评分(自定义评分，评论数占40%，顶占30%，踩占30%)
	 * @return
	 */
	public int score(){
		return getCommentNum() * 4 + getDigg() * 3 + getBury() * 3;
	}

	@Override
	public void run() {
		try {
			execute();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
