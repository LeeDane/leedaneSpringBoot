package com.cn.leedane.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.cn.leedane.model.BlogBean;
/**
 * 博客mapper接口类
 * @author LeeDane
 * 2016年7月12日 上午11:04:39
 * Version 1.0
 */
public interface BlogMapper extends BaseMapper<BlogBean>{
	
	/**
	 * 根据条件查询记录，where语句，"where"需要用户自己写
	 * @param conditions 条件的字符串
	 * @return
	 */
	public List<Map<String, Object>> searchBlog(@Param("status") int status, @Param("conditions") String conditions);
	
	/**
	 * 查找一条博客信息(跟findById的区别是字段可以自定义显示，去掉冗余字段)
	 * @param id
	 */
	public List<Map<String, Object>> getOneBlog(@Param("status") int status,@Param("id") int id);
	
	public List<BlogBean> getMoreBlog(int start,int end,String showType);
	public List<BlogBean> managerAllBlog();
	
	/**
	 * 获得全部的博客总数
	 * @return
	 */
	public int getTotalNum();
	
	public int getZanNum(int Bid);//获得赞的总数
	
	public int getCommentNum(int Bid);//获得评论的总数
	
	public int getSearchBlogTotalNum(String conditions,String conditionsType);  //按照条件和条件的类型搜索符合条件的博客的数量
	//public List<BlogBean> SearchBlog(int start,int end ,String conditions,String conditionsType); //按照条件和条件的类型分页搜索符合条件的博客
	
	
	public void addReadNum(BlogBean blog);//此处不必有返回值
	public int getReadNum(int Bid);//根据博客Id获得该博客的阅读次数
	
	/**
	 * 根据num的值，如5表示获取最新5条，负数表示获得全部
	 * @param start,@param end
	 * @return
	 */
	public List<BlogBean> getLatestBlog(int start , int end);

	/**
	 * 根据最后一条博客的ID取得指定num条数的博客
	 * @param lastBlogId 上次去的博客结果集中最后一条博客的ID,注意：当值小于1将取最新的num条
	 * @param num  需要获取的博客条数,注意：当值小于1将取最新的num条
	 * @return
	 */
	public List<Map<String, Object>> getLatestBlogById(@Param("last_blog_id")int lastBlogId, @Param("num")int num);
	
	
	/**
	 * 获得最热门的size条记录
	 * 热门的评分规则：评论数*0.45+转发数*0.25+分享数*0.2+赞数*0.1-踩数*0.4+阅读数*0.1
	 * @param size  条数,默认是5条
	 * @return
	 */
	public List<Map<String, Object>> getHotestBlogs(int size);
	
	/**
	 * 获得最热门博客的size条记录
	 * @param size  条数,默认是5条
	 * @return
	 */
	public List<Map<String, Object>> getNewestBlogs(int size);
	
	/**
	 * 获得推荐博客的size条记录
	 * @param size  条数,默认是5条
	 * @return
	 */
	public List<Map<String, Object>> getRecommendBlogs(int size);

	/**
	 * 摇一摇获取文章
	 * @param statusNormal
	 * @return
	 */
	public int shakeSearch(@Param("createUserId")int createUserId, @Param("status")int status);
	
	/**
	 * 更新阅读的数量
	 * @param num 数量
	 * @param id 博客的id
	 * @return
	 */
	//public int updateReadNum(int id, int num);
	
	/**
	 * 通过Id删除相应的博客
	 * @param id
	 * @return
	 */
	//public boolean deleteById(int id);

	/**
	 * 获取博客最大的置顶数据
	 * @return
	 */
	public int getMaxStick();
}
