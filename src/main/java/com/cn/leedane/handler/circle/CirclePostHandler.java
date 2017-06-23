package com.cn.leedane.handler.circle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

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
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 圈子帖子处理类
 * @author LeeDane
 * 2017年6月21日 下午2:58:12
 * version 1.0
 */
@Component
public class CirclePostHandler {
	//private Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private CirclePostMapper circlePostMapper;
	
	private RedisUtil redisUtil = RedisUtil.getInstance();

	@Autowired
	private CommentHandler commentHandler;
	
	@Autowired
	private ZanHandler zanHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	/**
	 * 添加转发帖子
	 * @param postId
	 */
	public void addTransmit(int postId){
		String key = TransmitHandler.getTransmitKey(postId, DataTableType.帖子.value);
		int count = 0;
		//还没有添加到redis中
		if(StringUtil.isNull(redisUtil.getString(key))){
			//获取数据库中所有评论的数量
			count = SqlUtil.getTotalByList(circlePostMapper.getTotal(DataTableType.帖子.value, "where pid = "+postId));	
		}else{
			count = Integer.parseInt(redisUtil.getString(key)) + 1;
		}
		redisUtil.addString(key, String.valueOf(count));
	}
	
	/**
	 * 获取帖子转发总数
	 * @param postId
	 * @return
	 */
	public int getTransmitNumber(int postId){
		
		String transmitKey = TransmitHandler.getTransmitKey(postId, DataTableType.帖子.value);
		int transmitNumber;
		//redisUtil.delete(transmitKey);
		//转发
		if(!redisUtil.hasKey(transmitKey)){
			transmitNumber = SqlUtil.getTotalByList(circlePostMapper.getTotal(DataTableType.帖子.value, "where pid = "+postId));
			redisUtil.addString(transmitKey, String.valueOf(transmitNumber));
		}else{
			transmitNumber = Integer.parseInt(redisUtil.getString(transmitKey));
		}
		return transmitNumber;
	}
	
	/**
	 * 删除转发后修改转发数量
	 * @param postId
	 */
	public void deleteTransmit(int postId){
		String transmitKey = TransmitHandler.getTransmitKey(postId, DataTableType.帖子.value);
		int transmitNumber;
		//转发
		if(!redisUtil.hasKey(transmitKey)){
			transmitNumber = SqlUtil.getTotalByList(circlePostMapper.getTotal(DataTableType.帖子.value, "where pid = "+postId));
			redisUtil.addString(transmitKey, String.valueOf(transmitNumber));
		}else{
			transmitNumber = Integer.parseInt(redisUtil.getString(transmitKey));
			transmitNumber = transmitNumber -1;//转发总数减去1
			redisUtil.addString(transmitKey, String.valueOf(transmitNumber));
		}
	}
	
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
	 * @param circle
	 * @param postId
	 * @param user
	 * @return
	 */
	public CirclePostBean getCirclePostBean(CircleBean circle, int postId, UserBean user){
		return getCirclePostBean(circle.getId(), postId, user);
	}
	
	/**
	 * 获取帖子对象（校验是否帖子属于圈子的用户的，不是返回null对象）
	 * @param circleId
	 * @param postId
	 * @param user 不为空将校验帖子和该用户是否是同一个
	 * @return
	 */
	public CirclePostBean getCirclePostBean(int circleId, int postId, UserBean user){
		CirclePostBean circlePostBean = getCirclePostBean(postId);
		if(circleId > 0 && circlePostBean != null){
			if(circlePostBean.getCircleId() != circleId || (user != null && circlePostBean.getCreateUserId() != user.getId()) )
				return null;
		}
			
		return circlePostBean;
	}
	
	/**
	 * 获取帖子对象（校验是否帖子属于圈子的用户的，不是返回null对象）
	 * @param circleId
	 * @param postId
	 * @return
	 */
	public CirclePostBean getCirclePostBean(int circleId, int postId){
		return getCirclePostBean(circleId, postId, null);
	}
	
	/**
	 * 获取帖子的详细信息(注意：有照片的情况下，只缓存该照片已经上传到七牛存储服务器的，未上传的情况不做缓存)
	 * @param circleId
	 * @param postId
	 * @param user
	 * @return
	 */
	public List<Map<String, Object>> getPostDetail(int postId, UserBean user){
		CirclePostBean postBean = circlePostMapper.findById(CirclePostBean.class, postId);
		if(postBean == null)
			return null;
		
		return getPostDetail(postBean.getCircleId(), postId, user, false);
	}
	
	/**
	 * 获取帖子的详细信息(注意：有照片的情况下，只缓存该照片已经上传到七牛存储服务器的，未上传的情况不做缓存)
	 * @param circleId
	 * @param postId
	 * @param user
	 * @return
	 */
	public List<Map<String, Object>> getPostDetail(int circleId, int postId, UserBean user){
		return getPostDetail(circleId, postId, user, false);
	}
	
	/**
	 * 获取帖子的详细信息(注意：有照片的情况下，只缓存该照片已经上传到七牛存储服务器的，未上传的情况不做缓存)
	 * @param circleId
	 * @param postId
	 * @param user
	 * @param onlyContent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getPostDetail(int circleId, int postId, UserBean user, boolean onlyContent){
		String postKey = getPostKey(circleId, postId);
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
			list.get(0).put("transmit_number", getTransmitNumber(postId));
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
	 * 删除在redis
	 * @param circleId
	 * @param postId
	 * @return
	 */
	public boolean delete(int circleId, int postId){
		String postKey = getPostKey(circleId, postId);
		return redisUtil.delete(postKey);
	}
	
	/**
	 * 获取帖子在redis的key
	 * @param circleId
	 * @param postId
	 * @return
	 */
	public static String getPostKey(int circleId, int postId){
		return ConstantsUtil.CIRCLE_REDIS + circleId +"_POST_" + postId;
	}
	
}
