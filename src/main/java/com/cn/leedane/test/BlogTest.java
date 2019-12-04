package com.cn.leedane.test;

import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.JsoupUtil;
import com.cn.leedane.utils.StringUtil;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 博客相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:09:03
 * Version 1.0
 */
public class BlogTest extends BaseTest {
	@Resource
	private BlogMapper blogMapper;
	
	@Resource
	private UserService<UserBean> userService;
	@Resource
	private OperateLogService<OperateLogBean> operateLogService;

	@Test
	public void addBlog() throws Exception{
		BlogBean blog = new BlogBean();
		blog.setTitle("测试标题2");
		blog.setContent("这是测试信息2。。。。。。。。。。");
		//optionService.loadById(1);
		blog.setCreateUserId(userService.findById(12).getId());
		//operateLogService.loadById(1);
		blog.setCreateTime(new Date());
		blogMapper.save(blog);
	}
	
	@Test
	public void loadByOneBlog() {
		BlogBean bean = blogMapper.findById(BlogBean.class, 3);
		logger.info(bean);
	}
	
	
	/**
	 * 提取所有文章的摘要
	 */
	@Test
	public void addDigest(){
		
		List<Map<String, Object>> idList = blogMapper.executeSQL("select id from "+DataTableType.博客.value+" where id > ?", 2);
		
		int count = 0;
		if(idList.size() > 0){
			count = StringUtil.changeObjectToInt(idList.size());
			logger.info("一共" + count+ "条数据");
		}
		
		for(int i = 0; i < count; i++){
			try{
				BlogBean bean = blogMapper.findById(BlogBean.class, StringUtil.changeObjectToInt(idList.get(i).get("id")));
				if(StringUtil.isNull(bean.getDigest())){
					String digest = JsoupUtil.getInstance().getDigest(bean.getContent(), 0, 120);
					bean.setDigest(digest);
					blogMapper.update(bean);
				}
				
				logger.info("blog" + bean.getId() + "操作完成");
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
			
		}
	}
	
	/**
	 * 删除重复标题的数据，保留最新的一条
	 */
	@Test
	public void deleteSameBlog(){
		int index = 0;
		deleteOne(index);
		logger.info("处理完成啦");
	}
	
	public void deleteOne(int index){
		List<Map<String, Object>> list = blogMapper.executeSQL("select id, title from "+DataTableType.博客.value+" where id > ? order by id asc limit 1", index);
		if(list.size() > 0){
			int preId = StringUtil.changeObjectToInt(list.get(0).get("id"));
			List<Map<String, Object>> list1 = blogMapper.executeSQL("select id from "+DataTableType.博客.value+" where title=? and id > ?", list.get(0).get("title"), preId);
			if(list1.size()> 0){
				//int id = StringUtil.changeObjectToInt(list1.get(0).get("id"));
				try {
					blogMapper.deleteById(BlogBean.class, preId);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			deleteOne(index + 1);
		}
	}
	
	/**
	 * 查询
	 */
	@Test
	public void searchBlog(){
		String search = "他";
		StringBuffer blogSql = new StringBuffer();
		blogSql.append("select b.id, b.img_url, b.title, b.has_img, b.tag, date_format(b.create_time,'%Y-%c-%d %H:%i:%s') create_time ");
		blogSql.append(" , b.digest, b.froms, b.create_user_id, u.account ");
		blogSql.append(" from "+DataTableType.博客.value+" b inner join "+DataTableType.用户.value+" u on b.create_user_id = u.id ");
		blogSql.append(" where b.status = ? and ((b.content like '%"+search+"%') or (b.title like '%"+search+"%')) limit 10");
		List<Map<String, Object>> blogs = blogMapper.executeSQL(blogSql.toString(), ConstantsUtil.STATUS_NORMAL);
		logger.info(blogs.size());
	}
	
}
