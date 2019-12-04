package com.cn.leedane.handler;

import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.model.BlogsBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 文章处理类
 * @author LeeDane
 * 2016年7月12日 上午11:53:35
 * Version 1.0
 */
@Component
public class BlogHandler extends BaseCacheHandler<BlogsBean>{
	@Autowired
	private BlogMapper blogMapper;

	@Autowired
	private CommentHandler commentHandler;

	@Autowired
	private TransmitHandler transmitHandler;
	
	@Autowired
	private ZanHandler zanHandler;

	@Autowired
	private ReadHandler readHandler;

	@Override
	protected BlogsBean getBean() {
		return new BlogsBean();
	}

	@Override
	protected BlogsBean getT(Object... params) {
		BlogsBean blogsBean = new BlogsBean();
		StringBuffer sql = new StringBuffer();
		sql.append("select b.id, b.img_url, b.title, b.has_img, b.tag, date_format(b.create_time,'%Y-%m-%d %H:%i:%s') create_time");
		sql.append(" , b.digest, b.froms, b.create_user_id, b.stick, (select account from "+DataTableType.用户.value+" where id = b.create_user_id) account");
		sql.append(" from "+ DataTableType.博客.value +" b");
		sql.append(" where b.id=? ");
		sql.append(" and b.status = ?");
		List<Map<String, Object>> list = blogMapper.executeSQL(sql.toString(), StringUtil.changeObjectToInt(params[0]), ConstantsUtil.STATUS_NORMAL);
		blogsBean.setList(list);
		return blogsBean;
	}

	/**
	 * 获取博客的详细信息
	 * @param blogId
	 * @param user
	 * @return
	 */
	public List<Map<String, Object>> getBlogDetail(int blogId, UserBean user){
		return getBlogDetail(blogId, user, false);
	}
	
	/**
	 * 获取博客的详细信息
	 * @param blogId
	 * @param user
	 * @param onlyContent true表示只获取内容
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getBlogDetail(long blogId, UserBean user, boolean onlyContent){
		Object obj = super.get(blogId, user, onlyContent);
		List<Map<String, Object>> list = new ArrayList<>();
		if(obj != null){
			list = ((BlogsBean)obj).getList();
		}
		
		if(list != null && list.size() == 1 && !onlyContent){
			list.get(0).put("comment_number", commentHandler.getCommentNumber(DataTableType.博客.value, blogId));
			list.get(0).put("transmit_number", transmitHandler.getTransmitNumber(DataTableType.博客.value, blogId));
			list.get(0).put("zan_number", zanHandler.getZanNumber(DataTableType.博客.value, blogId));
//			list.get(0).put("zan_users", zanHandler.getZanUser(blogId, DataTableType.博客.value, user, 6));
//			int createUserId = StringUtil.changeObjectToInt(list.get(0).get("create_user_id"));
			//暂时没用上图片
//			if( createUserId > 0)
//				//填充图片信息
//				list.get(0).put("user_pic_path", userHandler.getUserPicPath(createUserId, "30x30"));
		}
		return list;
	}
	
	/**
	 * 删除在redis
	 * @param params
	 * @return
	 */
	@Override
	public boolean delete(Object... params) {
		int blogId = StringUtil.changeObjectToInt(params[0]);
		zanHandler.deleteZan(DataTableType.博客.value, blogId);
		zanHandler.deleteZanUsers(DataTableType.博客.value, blogId);
		commentHandler.deleteComment(blogId);
		commentHandler.deleteComment(DataTableType.博客.value, blogId);
		transmitHandler.deleteTransmit(DataTableType.博客.value, blogId);
		readHandler.delete(DataTableType.博客.value, blogId);
		try{
			systemCache.removeCache(ZanHandler.getZanKey(DataTableType.博客.value, blogId));
			systemCache.removeCache(ZanHandler.getZanUserKey(DataTableType.博客.value, blogId));
			systemCache.removeCache(CommentHandler.getCommentKey(blogId));
			systemCache.removeCache(CommentHandler.getCommentKey(DataTableType.博客.value, blogId));
			systemCache.removeCache(TransmitHandler.getTransmitKey(DataTableType.博客.value, blogId));
		}catch (Exception e){
			e.printStackTrace();
		}
		return super.delete(params);
	}

	/**
	 * 获取心情在redis的key
	 * @param params
	 * @return
	 */
	@Override
	public String getKey(Object... params) {
		return ConstantsUtil.BLOG_REDIS + StringUtil.changeObjectToInt(params[0]);
	}
}
