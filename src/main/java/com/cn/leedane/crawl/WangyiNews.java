package com.cn.leedane.crawl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cn.leedane.utils.StringUtil;

/**
 * 临时包装网易新闻的实体(http://news.163.com/)
 * @author LeeDane
 * 2016年7月12日 下午3:33:42
 * Version 1.0
 */
public class WangyiNews extends BaseCrawlBean{
	
	public WangyiNews(String url, String title, String id) {
		super(url, title, id);
	}
	
	public WangyiNews() {
	}
	
	private boolean hasBury = false;  //是否有踩的功能
	
	private String title; //文章的标题
		
	@Override
	public void saveFile() {
		Elements content = html.select(".article-detail .article-content");
		for(Element e : content){
			if(e.select("div script") != null){
				e.select("div script").remove();
			}
		}
		
		if(StringUtil.isNull(title)){
			title = this.getTitle();
			
			//对title再次判断，要是实在还是空的，就直接返回，不再进行下一步的操作
			if(StringUtil.isNull(title) || StringUtil.isNull(this.getContent())) return;
			//对得到的标题进行一系列地转换
			title = "G:/sanwen/"+title.replaceAll("&gt;", "").replaceAll("&lt;", "").replaceAll("　", "").replaceAll("/", "-").replaceAll("\"", "'").replaceAll("\\?", "").replaceAll("？", "").replaceAll(" ", "").replaceAll(":", "-").trim() + ".html";
		}
		//title = title == null || title.equals("") ? "F:/sanwen/"+html.select(".article-detail .title>h1").html()+".html" : title;
		File file = new File(title);
			
		try {
			if(!file.exists()){
				file.createNewFile();
			}else{
				file.delete();
			}			
			FileOutputStream fot = new FileOutputStream(file);
			//fot.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>".getBytes("utf-8"));
			fot.write(this.getContent().getBytes("utf-8"));
			//fot.write("</body></html>".getBytes("utf-8"));
			fot.close();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		endTime = System.currentTimeMillis();
		if(!title.contains(".html")){
			System.out.println("sssssssss");
		}
		System.out.println("文件"+ title +"保存成html成功！共计耗时："+(endTime - startTime) + "毫秒");

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
			if(!u.equals("http://news.163.com/")){
				if(u.startsWith("/")){
					u = "http://news.163.com" + u;
				}
				
				Pattern p=Pattern.compile("http://[0-9a-zA-Z]*.163.com");//找网易新闻的子站
				Matcher m=p.matcher(u);
				boolean result=m.find();
				if(u.startsWith("http://news.163.com/") || result){
					//只对以“http://news.163.com/”开头和是网易的子站进行保存进数据库
					newUrls.add(u);
				}
				
			}
		}
		return newUrls;
	}
	
	public String getContent() {
		Elements e = html.select("#endText");
		//去掉文章最下面的信息
		e.select(".ep-source").remove();
		
		return e.html();
	}
	
	/**
	 * 获得主图像信息
	 */
	public String getMainImg() {
		return super.getMainImg("#endText img", 0);
	}

	/**
	 * 获取评论数
	 * @return
	 */
	private int getCommentNum() {
		String commentHtml = "";
		Elements cn = html.select("#tieArea .js-bactCount");
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
		Elements cn = html.select(".sharecommend-info .commend-info-count");
		if(cn != null){
			diggHtml = cn.html().trim();
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
		if(cn != null){
			buryHtml = cn.html().trim();
		}	
		return Integer.parseInt(buryHtml);
	}
	
	/**
	 * 获取转发的数量
	 * @return
	 */
	private int getTranspond() {
		String transpondHtml = "";
		try {
			toFile("F:/toutiao/getTranspond.html",html.html());
		} catch (IOException e) {
			e.printStackTrace();
		}
		Elements cn = html.getElementsByClass("js-bjoinCount");//.select("#tieArea .js-bjoinCount");
		if(cn != null){
			transpondHtml = cn.html().trim();
		}	
		return Integer.parseInt(transpondHtml);
	}
	
	/**
	 * 获取该篇文章的评分(自定义评分，评论数占40%，顶占30%，踩占30%，转发占30%)
	 * @return
	 */
	public int score(){
		return getCommentNum() * 4 + getDigg() * 3 + (hasBury ? getBury() * 3 : getTranspond() * 3);
	}

	@Override
	public void run() {
		try {
			execute();
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 将字符串保存在本地
	 * @param fileName
	 * @param text
	 * @throws IOException
	 */
	public void toFile(String fileName, String text) throws IOException{
		File file = new File(fileName);
		
		if(!file.exists()){
			file.createNewFile();
		}else{
			file.delete();
		}			
		FileOutputStream fot = new FileOutputStream(file);
		fot.write("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"></head><body>".getBytes("utf-8"));
		fot.write(text.getBytes("utf-8"));
		fot.write("</body></html>".getBytes("utf-8"));
		fot.close();
	}

	public String getTitle() {
		return html.select(".ep-content #h1title").text();
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
}
