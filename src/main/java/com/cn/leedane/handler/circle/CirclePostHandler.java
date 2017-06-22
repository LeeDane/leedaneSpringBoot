package com.cn.leedane.handler.circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.handler.CommentHandler;
import com.cn.leedane.handler.TransmitHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.ZanHandler;
import com.cn.leedane.mapper.circle.CirclePostMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;

/**
 * 圈子帖子处理类
 * @author LeeDane
 * 2017年6月21日 下午2:58:12
 * version 1.0
 */
@Component
public class CirclePostHandler {
	private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CirclePostMapper circlePostMapper;
	
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
	 * 获取帖子对象
	 * @param postId
	 * @return
	 */
	public CirclePostBean getCirclePostBean(int postId){
		return circlePostMapper.findById(CirclePostBean.class, postId);
	}
	
	/**
	 * 获取帖子对象（校验是否帖子属于圈子的用户的，不是返回null对象）
	 * @param circleId
	 * @param postId
	 * @param user
	 * @return
	 */
	public CirclePostBean getCirclePostBean(CircleBean circle, int postId, UserBean user){
		CirclePostBean circlePostBean = getCirclePostBean(postId);
		if(circle != null && circlePostBean != null){
			if(circlePostBean.getCircleId() != circle.getId() || circlePostBean.getCreateUserId() != user.getId() )
				return null;
		}
			
		return circlePostBean;
	}
	
	/**
	 * 获取帖子的详细信息(注意：有照片的情况下，只缓存该照片已经上传到七牛存储服务器的，未上传的情况不做缓存)
	 * @param moodId
	 * @param user
	 * @return
	 */
	public List<Map<String, Object>> getPostDetail(int postId, UserBean user){
		return getPostDetail(postId, user, false);
	}
	
	/**
	 * 获取帖子的详细信息(注意：有照片的情况下，只缓存该照片已经上传到七牛存储服务器的，未上传的情况不做缓存)
	 * @param moodId
	 * @param user
	 * @param onlyContent true表示只获取内容
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPostDetail(int postId, UserBean user, boolean onlyContent){
		String postKey = getPostKey(postId);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		JSONArray jsonArray = new JSONArray();
		if(!redisUtil.hasKey(postKey)){
			list = circlePostMapper.getCirclePost(postId, ConstantsUtil.STATUS_NORMAL);
			if(list != null && list.size() >0){
				int createUserId;
				for(int i = 0; i < list.size(); i++){
					createUserId = StringUtil.changeObjectToInt(list.get(i).get("create_user_id"));
					list.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
				}
			}
			jsonArray = JSONArray.fromObject(list);
			redisUtil.addString(postKey, jsonArray.toString());
		}else{
			String mood = redisUtil.getString(postKey);
			//要把null转化成“”字符串，在json转化才不会报错：net.sf.json.JSONException: null object
			mood = mood.replaceAll("null", "\"\"");
			if(StringUtil.isNotNull(mood) && !"[null]".equalsIgnoreCase(mood)){
				jsonArray = JSONArray.fromObject(mood);
				list = (List<Map<String, Object>>) jsonArray;
			}
		}
		
		if(CollectionUtil.isNotEmpty(list) && list.size() == 1 && !onlyContent){
			list.get(0).put("comment_number", commentHandler.getCommentNumber(postId, DataTableType.帖子.value));
			list.get(0).put("transmit_number", transmitHandler.getTransmitNumber(postId, DataTableType.帖子.value));
			list.get(0).put("zan_number", zanHandler.getZanNumber(postId, DataTableType.帖子.value));
			list.get(0).put("zan_users", zanHandler.getZanUser(postId, DataTableType.帖子.value, user, 6));
			int createUserId = StringUtil.changeObjectToInt(list.get(0).get("create_user_id"));
			if( createUserId > 0)
				//填充图片信息
				list.get(0).putAll(userHandler.getBaseUserInfo(createUserId));
		}
		return list;
	}
	
	/**
	 * 获得帖子的图片
	 * @param circleId
	 * @param postId
	 * @return
	 */
	public String getPostImgs(int postId){
		String postImgKey = getPostImgKey(postId);
		if(!redisUtil.hasKey(postImgKey)){
			String path = null;
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			StringBuffer sql = new StringBuffer();
			sql.append("select qiniu_path, pic_size, pic_order, width, height, lenght ");
			sql.append(" from "+DataTableType.文件.value);
			sql.append(" where status = ? and table_name=? and table_uuid = ? and is_upload_qiniu=? and pic_size =? order by pic_order,id");
			list = circlePostMapper.getCirclePostImgs(postId, ConstantsUtil.STATUS_NORMAL);
			if(CollectionUtil.isNotEmpty(list)){
				StringBuffer buffer = new StringBuffer();
				String p = null;
				for(int i = 0; i< list.size(); i++){
					p = StringUtil.changeNotNull(list.get(i).get("qiniu_path"));
					if(p.startsWith("http://") || p.startsWith("https://")){
						buffer.append(p);
						if(i != list.size() -1){
							buffer.append(";");
						}
					}
					
				}
				path = buffer.toString();
				if(StringUtil.isNotNull(path)){
					redisUtil.addString(postImgKey, path);
				}
			}
			return path;
		}else{
			logger.info("postImgKey:"+postImgKey);
			return redisUtil.getString(postImgKey);
		}
	}
	
	/**
	 * 删除在redis
	 * @param postId
	 * @return
	 */
	public boolean delete(int postId){
		String postKey = getPostKey(postId);
		String postImgsKey = getPostImgKey(postId);
		redisUtil.delete(postImgsKey);
		return redisUtil.delete(postKey);
	}
	
	/**
	 * 获取帖子在redis的key
	 * @param postId
	 * @return
	 */
	public static String getPostKey(int postId){
		return ConstantsUtil.CIRCLE_REDIS +"POST_" + postId;
	}
	
	/**
	 * 获取帖子图片在redis的key
	 * @param postId
	 * @return
	 */
	public static String getPostImgKey(int postId){
		return ConstantsUtil.CIRCLE_REDIS + "IMGS_POST_" +postId;
	}
	
}
