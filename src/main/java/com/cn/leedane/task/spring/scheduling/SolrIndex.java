package com.cn.leedane.task.spring.scheduling;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import com.cn.leedane.lucene.solr.BlogSolrHandler;
import com.cn.leedane.lucene.solr.MoodSolrHandler;
import com.cn.leedane.lucene.solr.UserSolrHandler;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.MoodMapper;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.MoodBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;

/**
 * 定时加入solr索引任务
 * @author LeeDane
 * 2017年6月6日 上午10:53:08
 * version 1.0
 */
@Component("solrIndex")
public class SolrIndex implements BaseScheduling{
	private Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 每次最多获取的数量
	 */
	public static final int MAX_SIZE = 20;
	
	@Resource
	private BlogMapper blogMapper;
	
	@Resource
	private MoodMapper moodMapper;
	
	@Resource
	private UserMapper userMapper;

	@Override
	public void execute() throws SchedulerException {
		logger.info("SolrIndex-->execute():定时任务开始执行博客、说说，用户加入solr索引中，当前服务器时间:"+DateUtil.DateToString(new Date()));
		
		
		List<Future<Boolean>> futures = new ArrayList<Future<Boolean>>();
		SingleIndexTask SingleIndexTask;
		//派发5个线程执行
		ExecutorService threadpool = Executors.newFixedThreadPool(5);
		
		List<BlogBean> blogs = getNoIndexBlogList();
		if(blogs.size() > 0)
			for(BlogBean blog: blogs){
				SingleIndexTask = new SingleIndexTask(1, blog);
				futures.add(threadpool.submit(SingleIndexTask));
			}
		
		List<MoodBean> moods = getNoIndexMoodList();
		if(moods.size() > 0)
			for(MoodBean mood: moods){
				SingleIndexTask = new SingleIndexTask(2, mood);
				futures.add(threadpool.submit(SingleIndexTask));
			}
		
		List<UserBean> users = getNoIndexUserList();
		if(users.size() > 0)
			for(UserBean user: users){
				SingleIndexTask = new SingleIndexTask(3, user);
				futures.add(threadpool.submit(SingleIndexTask));
			}
		
		threadpool.shutdown();
		
		for(int i = 0; i < futures.size() ;i++){
			try {
				if(!futures.get(i).get()){
					futures.get(i).cancel(true);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
				futures.get(i).cancel(true);
			} catch (ExecutionException e) {
				e.printStackTrace();
				futures.get(i).cancel(true);
			}
		}
		logger.info("本次所有要加入索引的任务已经处理完成");
			
	}
	
	/**
	 * 获取没有索引的博客列表(最多获取1000条)
	 * @return
	 */
	private List<BlogBean> getNoIndexBlogList(){
		List<Map<String, Object>> list = blogMapper.executeSQL("select b.id, b.title, b.content, b.digest from "+DataTableType.博客.value+" b where b.status=? and b.is_solr_index = ? order by b.id desc limit 0,?", ConstantsUtil.STATUS_NORMAL, false, 20);
		List<BlogBean> blogs = new ArrayList<BlogBean>();
		
		if(list != null && list.size()> 0){
			logger.info("获取未加入索引的文章总数:"+ list.size());
			BlogBean blogBean;
			for(Map<String, Object> map: list){
				blogBean = new BlogBean();
				blogBean.setId(StringUtil.changeObjectToInt(map.get("id")));
				blogBean.setTitle(StringUtil.changeNotNull(map.get("title")));
				blogBean.setContent(StringUtil.changeNotNull(map.get("content")));
				blogBean.setDigest(StringUtil.changeNotNull(map.get("digest")));
				blogs.add(blogBean);
			}
		}
		return blogs;
	}
	
	/**
	 * 获取没有索引的心情列表(最多获取1000条)
	 * @return
	 */
	private List<MoodBean> getNoIndexMoodList(){
		List<Map<String, Object>> list = blogMapper.executeSQL("select m.id, m.content from "+DataTableType.心情.value+" m where m.status=? and m.is_solr_index=? order by m.id desc limit 0,?", ConstantsUtil.STATUS_NORMAL, false, MAX_SIZE);
		List<MoodBean> moods = new ArrayList<MoodBean>();
		if(list != null && list.size()> 0){
			MoodBean moodBean;
			for(Map<String, Object> map: list){
				moodBean = new MoodBean();
				moodBean.setId(StringUtil.changeObjectToInt(map.get("id")));
				moodBean.setContent(StringUtil.changeNotNull(map.get("content")));
				moods.add(moodBean);
			}
		}
		return moods;
	}
	
	/**
	 * 获取没有索引的用户列表(最多获取1000条)
	 * @return
	 */
	private List<UserBean> getNoIndexUserList(){
		List<Map<String, Object>> list = userMapper.executeSQL("select u.id, u.account, u.china_name, u.real_name, u.mobile_phone, u.id_card, u.email from "+DataTableType.用户.value+" u where u.status=? and u.is_solr_index=? order by u.id desc limit 0,?", ConstantsUtil.STATUS_NORMAL, false, MAX_SIZE);
		List<UserBean> users = new ArrayList<UserBean>();
		if(list != null && list.size()> 0){
			UserBean userBean;
			for(Map<String, Object> map: list){
				userBean = new UserBean();
				userBean.setId(StringUtil.changeObjectToInt(map.get("id")));
				userBean.setAccount(StringUtil.changeNotNull(map.get("account")));
				userBean.setChinaName(StringUtil.changeNotNull(map.get("china_name")));
				userBean.setRealName(StringUtil.changeNotNull(map.get("real_name")));
				userBean.setMobilePhone(StringUtil.changeNotNull(map.get("mobile_phone")));
				userBean.setIdCard(StringUtil.changeNotNull(map.get("id_card")));
				userBean.setEmail(StringUtil.changeNotNull(map.get("email")));
				users.add(userBean);
			}
		}
		return users;
	}
	
	
	class SingleIndexTask implements Callable<Boolean>{
		private Object obj;
		private int tempId;
		public SingleIndexTask(int tempId, Object obj) {
			this.obj = obj;
			this.tempId = tempId;
		}

		@Override
		public Boolean call() throws Exception {
			boolean result = false;
			if(tempId == 1){
				BlogBean blog = (BlogBean) obj;
				BlogBean updateBlog = blogMapper.findById(BlogBean.class, blog.getId());
				result = BlogSolrHandler.getInstance().addBean(updateBlog);
				if(result){
					updateBlog.setSolrIndex(true);
					blogMapper.update(updateBlog);
				}
			}else if(tempId ==2){
				MoodBean mood = (MoodBean) obj;
				MoodBean updateMood = moodMapper.findById(MoodBean.class, mood.getId());
				result = MoodSolrHandler.getInstance().addBean(updateMood);
				if(result){
					updateMood.setSolrIndex(true);
					moodMapper.update(updateMood);
				}
			}else if(tempId ==3){
				UserBean user = (UserBean) obj;
				UserBean updateUser = userMapper.findById(UserBean.class, user.getId());
				result = UserSolrHandler.getInstance().addBean(updateUser);
				if(result){
					updateUser.setSolrIndex(true);
					userMapper.update(updateUser);
				}
			}
			return result;
		}
	}
}
