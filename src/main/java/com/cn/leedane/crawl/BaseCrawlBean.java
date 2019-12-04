package com.cn.leedane.crawl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.cn.leedane.exception.ErrorException;
import com.cn.leedane.utils.StringUtil;

/**
 * 抽象的抓取类
 * @author LeeDane
 * 2016年7月12日 下午3:32:41
 * Version 1.0
 */
public abstract class BaseCrawlBean implements Runnable{
	
	/**
	 * 控制默认的超时时间(毫秒)
	 */
	public static final int DEFAULT_TIME_OUT = 300000;
	
	/**
	 * 默认模拟浏览器的user-agent
	 */
	public static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.64 Safari/537.31";

	/**
	 * 爬取的网页的url
	 */
	protected String url; 
	
	/**
	 * 文章的ID
	 */
	protected String id;
	
	/**
	 * 超时时间限制
	 */
	private int timeOut;
	
	/**
	 * 是否保存在本地
	 */
	protected boolean isSaveFile;
	
	/**
	 * 浏览器的user-agent
	 */
	private String userAgent;
	
	/**
	 * url所在网页的文档信息
	 */
	public Document html; 
	
	public BaseCrawlBean() {
	}
	
	public BaseCrawlBean(String url,String title,String id) {
		this.url = url;
	}
	
	//开始时间
	long startTime; 
	
	//结束时间
	long endTime; 
	
	/**
	 * 执行爬取数据
	 * @throws IOException 
	 */
	public synchronized void execute() throws IOException {
		
		if(StringUtil.isNull(url)) 
			throw new NullPointerException("爬取数据的url不能为空！");
		
		startTime = System.currentTimeMillis();
		html = Jsoup.connect(this.url)
			    .userAgent(this.getUserAgent()).timeout(this.getTimeOut()).get();
		
		if(isSaveFile){
			this.saveFile();
		}
	}
	
	/**
	 * 解析html的content数据
	 * @throws IOException 
	 */
	protected abstract void saveFile();
	
	/**
	 * 获得url的所有a标签中的href值
	 * @param cssSelect  选择的css元素表达式
	 * @throws IOException 
	 */
	protected List<String> getHomeListsHref(String cssSelect) throws IOException {
		
		List<String> urls = new ArrayList<String>();
		if(StringUtil.isNull(cssSelect))
			return urls;
		//获取body中的a标签
		Elements ems = html.select(cssSelect);		
		for(Element e: ems){
			String u = e.attr("href");
			if(u == null || u.equals("")) 
				continue;			
			urls.add(u);
		}
		return urls;
	}
	
	
	/**
	 * 获得网页需要的内容
	 * @param cssSelect  选择的css元素表达式
	 * @param cssRemoves  移除的元素表达式
	 * @return
	 */
	protected String getContent(String cssSelect,String ...cssRemoves){
		//"#endText"
		Elements e = html.select(cssSelect);
		//去掉文章最下面的信息".ep-source"
		for(String cssRemove: cssRemoves)
			e.select(cssRemove).remove();
		
		return e.html();
	}

	/**
	 * 获得主图像信息(默认将获取第一张图片作为主图),num为小于1表示取默认索引是1的图片
	 * @param cssSelect
	 * @param num 取第几张作为主图
	 * @return
	 */
	public String getMainImg(String cssSelect,int num) {
		//"#endText img"
		Elements es = html.select(cssSelect);
		if(num > 0) {
			//获取用户指定的第几张图片的src作为图像的地址返回
			return es.eq(num).attr("src");
		}
		String src = null;
		for(Element e: es){
			src  = e.attr("src");
			if(src == null || src.equals(""))
				continue;	
			return src;		
		}
		return src;
	}


	/**
	 * 解析url去获取文章ID（默认是以/id结尾才处理，其他情况需要覆盖重写该方法去获取）
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
	 * @param filePath
	 * @param text
	 * @throws IOException
	 */
	protected void toFile(String filePath, String text) throws IOException{
		File file = new File(filePath);
		
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
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
		
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 小于等于0使用默认的超时时间12秒
	 * @return
	 */
	public int getTimeOut() {
		return timeOut <= 0 ? BaseCrawlBean.DEFAULT_TIME_OUT : timeOut;
	}

	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
	}

	public String getUserAgent() {
		return StringUtil.isNull(userAgent) ? BaseCrawlBean.DEFAULT_USER_AGENT: userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public Document getHtml() {
		return html;
	}

	public void setHtml(Document html) {
		this.html = html;
	}

	public boolean isSaveFile() {
		return isSaveFile;
	}

	public void setSaveFile(boolean isSaveFile) {
		this.isSaveFile = isSaveFile;
	}	
	
	
}
