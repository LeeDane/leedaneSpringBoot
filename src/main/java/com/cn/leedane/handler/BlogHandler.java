package com.cn.leedane.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;

/**
 * 文章处理类
 * @author LeeDane
 * 2016年7月12日 上午11:53:35
 * Version 1.0
 */
@Component
public class BlogHandler {
	@Autowired
	private BlogMapper blogMapper;

	private RedisUtil redisUtil = RedisUtil.getInstance();
	
	@Autowired
	private CommentHandler commentHandler;
	
	
	@Autowired
	private TransmitHandler transmitHandler;
	
	@Autowired
	private ZanHandler zanHandler;
	
	@Autowired
	private UserHandler userHandler;
	
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
	public List<Map<String, Object>> getBlogDetail(int blogId, UserBean user, boolean onlyContent){
		String blogKey = getBlogKey(blogId);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		JSONArray jsonArray = new JSONArray();
		if(!redisUtil.hasKey(blogKey)){
			StringBuffer sql = new StringBuffer();
			sql.append("select b.id, b.img_url, b.title, b.has_img, b.tag, date_format(b.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" , b.digest, b.froms, b.create_user_id, (select account from "+DataTableType.用户.value+" where id = b.create_user_id) account");
			sql.append(" from "+DataTableType.博客.value+" b");
			sql.append(" where b.id=? ");
			sql.append(" and b.status = ?");
			list = blogMapper.executeSQL(sql.toString(), blogId, ConstantsUtil.STATUS_NORMAL);
			jsonArray = JSONArray.fromObject(list);
			redisUtil.addString(blogKey, jsonArray.toString());
		}else{
			String blog = redisUtil.getString(blogKey);
			if(StringUtil.isNotNull(blog) && !"[null]".equalsIgnoreCase(blog)){
				jsonArray = JSONArray.fromObject(blog);
				list = (List<Map<String, Object>>) jsonArray;
			}
		}
		
		if(list != null && list.size() == 1 && !onlyContent){
			list.get(0).put("comment_number", commentHandler.getCommentNumber(blogId, DataTableType.博客.value));
			list.get(0).put("transmit_number", transmitHandler.getTransmitNumber(blogId, DataTableType.博客.value));
			list.get(0).put("zan_number", zanHandler.getZanNumber(blogId, DataTableType.博客.value));
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
	 * @param blogId
	 * @return
	 */
	public boolean delete(int blogId){
		return redisUtil.delete(getBlogKey(blogId));
	}
	
	/**
	 * 获取心情在redis的key
	 * @param blogId
	 * @return
	 */
	public static String getBlogKey(int blogId){
		return ConstantsUtil.BLOG_REDIS +blogId;
	}
}
