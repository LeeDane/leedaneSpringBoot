package com.cn.leedane.wechat.bean;
import java.util.List;

/**
 * 图文信息上的实体
 * @author LeeDane
 * 2015年6月25日 上午11:04:13
 * Version 1.0
 */
public class NewsMessage extends BaseMessage{
	private int ArticleCount;  //文章的总数量
	private List<News> Articles;  //具体每一条图文的封装
	public int getArticleCount() {
		return ArticleCount;
	}
	public void setArticleCount(int articleCount) {
		ArticleCount = articleCount;
	}
	public List<News> getArticles() {
		return Articles;
	}
	public void setArticles(List<News> articles) {
		Articles = articles;
	}
}
