package com.cn.leedane.handler.circle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.handler.CommentHandler;
import com.cn.leedane.handler.TransmitHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.ZanHandler;
import com.cn.leedane.mapper.circle.CirclePostMapper;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CirclePostBean;
import com.cn.leedane.model.circle.CircleUserPostBean;
import com.cn.leedane.model.circle.CircleUserPostsBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.OptionUtil;
import com.cn.leedane.utils.RelativeDateFormat;
import com.cn.leedane.utils.SerializeUtil;
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
	
	@Autowired
	private SystemCache systemCache;
	
	/**
	 * 获取热门帖子列表(这里只缓存8条记录，主要用于首页的展示)
	 * @param circleId
	 * @param userId
	 * @return
	 */
	public CircleUserPostsBean getHotestPosts(){
		String key = getHotestPostKey();
		Object obj = systemCache.getCache(key);
		CircleUserPostsBean userPostBean = null;
		//deleteHotestPosts();
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					userPostBean =  (CircleUserPostsBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), CircleUserPostsBean.class);
					if(userPostBean != null){
						systemCache.addCache(key, userPostBean);
						return userPostBean;
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						userPostBean = getHostestPosts(8);
						if(CollectionUtil.isNotEmpty(userPostBean.getPosts())){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(userPostBean));
								systemCache.addCache(key, userPostBean);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				userPostBean = getHostestPosts(8);
				if(CollectionUtil.isNotEmpty(userPostBean.getPosts())){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(userPostBean));
						systemCache.addCache(key, userPostBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			userPostBean = (CircleUserPostsBean)obj;
		}
		
		//由于缓存，这里统一再处理一下时间
		if(userPostBean != null && CollectionUtil.isNotEmpty(userPostBean.getPosts())){
			for(CircleUserPostBean cUserPostBean: userPostBean.getPosts()){
				if(cUserPostBean.getCreateTime().length() == "2017-10-10 10:10:10".length()){
					cUserPostBean.setCreateTime(RelativeDateFormat.format(DateUtil.stringToDate(cUserPostBean.getCreateTime())));
				}else{
					deleteHotestPosts();
				}
			}
		}
		return userPostBean;
	}
	
	/**
	 * 删除热门帖子的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deleteHotestPosts(){
		String key = getHotestPostKey();
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	
	/**
	 * 获取圈子用户的帖子列表(这里只缓存8条用户最新帖子记录，主要用于首页的展示)
	 * @param circleId
	 * @param userId
	 * @return
	 */
	public CircleUserPostsBean getUserCirclePosts(int userId){
		String key = getUserCirclePostKey(userId);
		Object obj = systemCache.getCache(key);
		CircleUserPostsBean userPostBean = null;
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					userPostBean =  (CircleUserPostsBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), CircleUserPostsBean.class);
					if(userPostBean != null){
						systemCache.addCache(key, userPostBean);
						return userPostBean;
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						userPostBean = getTheUserCirclePosts(userId, 8);
						if(CollectionUtil.isNotEmpty(userPostBean.getPosts())){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(userPostBean));
								systemCache.addCache(key, userPostBean);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				userPostBean = getTheUserCirclePosts(userId, 8);
				if(CollectionUtil.isNotEmpty(userPostBean.getPosts())){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(userPostBean));
						systemCache.addCache(key, userPostBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			userPostBean = (CircleUserPostsBean)obj;
		}
		
		//由于缓存，这里统一再处理一下时间
		if(userPostBean != null && CollectionUtil.isNotEmpty(userPostBean.getPosts())){
			for(CircleUserPostBean cUserPostBean: userPostBean.getPosts()){
				if(cUserPostBean.getCreateTime().length() == "2017-10-10 10:10:10".length()){
					cUserPostBean.setCreateTime(RelativeDateFormat.format(DateUtil.stringToDate(cUserPostBean.getCreateTime())));
				}else{
					deleteUserCirclePosts(userId);
				}
			}
		}
		return userPostBean;
	}
	
	/**
	 * 获取圈子当前用户的帖子列表
	 * @param userId
	 * @param limit
	 * @return
	 */
	private CircleUserPostsBean getHostestPosts(int limit){
		Date time = DateUtil.getDayBeforeOrAfter(OptionUtil.circleHostestBeforeDay, true);
		CircleUserPostsBean userPostsBean = new CircleUserPostsBean();
		//这里只缓存8条用户最新帖子记录
		userPostsBean.setPosts(getCircleUserPost(circlePostMapper.getHostestPosts(time, 0, limit, ConstantsUtil.STATUS_NORMAL)));
		return userPostsBean;
	}
	
	/**
	 * 获取圈子当前用户的帖子列表
	 * @param userId
	 * @param limit
	 * @return
	 */
	private CircleUserPostsBean getTheUserCirclePosts(int userId, int limit){
		CircleUserPostsBean userPostsBean = new CircleUserPostsBean();
		//这里只缓存8条用户最新帖子记录
		userPostsBean.setPosts(getCircleUserPost(circlePostMapper.getUserCirclePosts(userId, 0, limit, ConstantsUtil.STATUS_NORMAL)));
		userPostsBean.setTotal(SqlUtil.getTotalByList(circlePostMapper.getTotal(DataTableType.帖子.value, " m where create_user_id = "+ userId + " and status = 1")));
		return userPostsBean;
	}
	
	private List<CircleUserPostBean> getCircleUserPost(List<CirclePostBean> circlePostBeans){
		List<CircleUserPostBean> datas = new ArrayList<CircleUserPostBean>();
		if(CollectionUtil.isNotEmpty(circlePostBeans)){
			for(CirclePostBean postBean: circlePostBeans){
				CircleUserPostBean data = new CircleUserPostBean();
				data.setId(postBean.getId());
				data.setCircleId(postBean.getCircleId());
				data.setContent(postBean.getContent());
				data.setHasImg(postBean.isHasImg());
				data.setImgs(postBean.getImgs());
				data.setPid(postBean.getPid());
				data.setTag(postBean.getTag());
				data.setTitle(postBean.getTitle());
				data.setCreateTime(DateUtil.DateToString(postBean.getCreateTime()));
				data.setDigest(postBean.getDigest());
				datas.add(data);
			}
		}
		return datas;
	}
	
	/**
	 * 根据用户id删除其对应的帖子的cache和redis缓存
	 * @param userId
	 * @return
	 */
	public boolean deleteUserCirclePosts(int userId){
		String key = getUserCirclePostKey(userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取圈子用户的帖子列表(这里只缓存8条用户最新帖子记录，主要用于首页的展示)
	 * @param postId
	 * @param userId
	 * @return
	 */
	public CircleUserPostsBean getUserPostPosts(int circleId, int userId){
		/*CirclePostBean postBean =CirclePostBean 
		if(postBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该帖子不存在.value));
		
		int circleId = postBean.getCircleId();*/
		String key = getUserPostPostKey(circleId, userId);
		Object obj = systemCache.getCache(key);
		CircleUserPostsBean userPostsBean = null;
		//deleteUserPostPosts(circleId, userId);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					userPostsBean =  (CircleUserPostsBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), CircleUserPostsBean.class);
					if(userPostsBean != null){
						systemCache.addCache(key, userPostsBean);
						return userPostsBean;
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						userPostsBean = getTheUserPostPosts(circleId, userId, 8);
						if(CollectionUtil.isNotEmpty(userPostsBean.getPosts())){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(userPostsBean));
								systemCache.addCache(key, userPostsBean);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				userPostsBean = getTheUserPostPosts(circleId, userId, 8);
				if(CollectionUtil.isNotEmpty(userPostsBean.getPosts())){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(userPostsBean));
						systemCache.addCache(key, userPostsBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			userPostsBean = (CircleUserPostsBean)obj;
		}
		
		return userPostsBean;
	}
	
	/**
	 * 根据圈子的当前登录用户的id删除其对应的帖子的cache和redis缓存
	 * @param postId
	 * @param userId
	 * @return
	 */
	public boolean deleteUserPostPosts(int circleId, int userId){
		String key = getUserPostPostKey(circleId, userId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	/**
	 * 获取帖子当前用户的帖子列表
	 * @param circleId
	 * @param userId
	 * @param limit
	 * @return
	 */
	private CircleUserPostsBean getTheUserPostPosts(int circleId, int userId, int limit){
		CircleUserPostsBean userPostsBean = new CircleUserPostsBean();
		//这里只缓存8条用户最新帖子记录
		userPostsBean.setPosts(getCircleUserPost(circlePostMapper.getUserPostPosts(circleId, userId, 0, limit, ConstantsUtil.STATUS_NORMAL)));
		userPostsBean.setTotal(SqlUtil.getTotalByList(circlePostMapper.getTotal(DataTableType.帖子.value, " m where circle_id = "+ circleId +" and create_user_id = "+ userId +" and status = 1")));
		return userPostsBean;
	}

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
	 * 获取正常状态的帖子对象
	 * @param circleId
	 * @param postId
	 * @return
	 */
	public CirclePostBean getNormalCirclePostBean(int postId){
		return getNormalCirclePostBean(-1, postId, null);
	}
	
	/**
	 * 根据圈子ID/帖子ID删除该帖子的cache和redis缓存
	 * @param postId
	 * @return
	 */
	public boolean deletePostBeanCache(int postId){
		String key = getPostKey(postId);
		redisUtil.delete(key);
		systemCache.removeCache(key);
		return true;
	}
	
	
	/**
	 * 获取帖子的详情
	 * @param postId
	 * @param user
	 * @return
	 */
	public List<Map<String, Object>> getPostDetail(int postId, UserBean user){
		CirclePostBean circlePostBean = getNormalCirclePostBean(postId);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		if(circlePostBean != null){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("create_user_id", circlePostBean.getCreateUserId());
			map.put("account", userHandler.getUserName(circlePostBean.getCreateUserId()));
			map.put("create_time", RelativeDateFormat.format(circlePostBean.getCreateTime()));
			map.put("title", circlePostBean.getTitle());
			map.put("content", circlePostBean.getContent());
			list.add(map);
		}
		return list;
	}
	
	/**
	 * 获取正常状态的帖子对象（校验是否帖子属于圈子的用户的，不是返回null对象）
	 * @param circle
	 * @param postId
	 * @param user
	 * @return
	 */
	public CirclePostBean getNormalCirclePostBean(CircleBean circle, int postId, UserBean user){
		return getNormalCirclePostBean(circle.getId(), postId, user);
	}
	
	/**
	 * 获取正常状态的帖子对象（校验是否帖子属于圈子的用户的，不是返回null对象）
	 * @param circle
	 * @param postId
	 * @return
	 */
	public CirclePostBean getNormalCirclePostBean(CircleBean circle, int postId){
		return getNormalCirclePostBean(circle.getId(), postId, null);
	}
	
	
	/**
	 * 获取正常状态的帖子对象（校验是否帖子属于圈子的用户的，不是返回null对象）
	 * @param circleId
	 * @param postId
	 * @param user 不为空将校验帖子和该用户是否是同一个
	 * @return
	 */
	public CirclePostBean getNormalCirclePostBean(int circleId, int postId, UserBean user){
		CirclePostBean circlePostBean = getCirclePostBean(postId);
		if(postId > 0 && circlePostBean != null){
			//非正常状态
			if(circlePostBean.getStatus() != ConstantsUtil.STATUS_NORMAL || (circleId > 0 && circlePostBean.getCircleId() != circleId) || (user != null && circlePostBean.getCreateUserId() != user.getId()))
				return null;
		}
		return circlePostBean;
	}
	
	/**
	 * 获取帖子对象(没有状态)
	 * @param postId
	 * @param user 不为空将校验帖子和该用户是否是同一个
	 * @return
	 */
	public CirclePostBean getCirclePostBean(int postId, UserBean user){
		return getCirclePostBean(postId);
	}
	

	/**
	 * 获取帖子对象(没有状态)
	 * @param circleId
	 * @param postId
	 * @param user 不为空将校验帖子和该用户是否是同一个
	 * @param normal
	 * @return
	 */
	private CirclePostBean getCirclePostBean(int postId){		
		String key = getPostKey(postId);
		Object obj = systemCache.getCache(key);
		CirclePostBean circlePostBean = null;
		//deleteUserCirclePosts(userId);
		if(obj == ""){
			if(redisUtil.hasKey(key)){
				try {
					circlePostBean =  (CirclePostBean) SerializeUtil.deserializeObject(redisUtil.getSerialize(key.getBytes()), CirclePostBean.class);
					if(circlePostBean != null){
						systemCache.addCache(key, circlePostBean);
					}else{
						//对在redis中存在但是获取不到对象的直接删除redis的缓存，重新获取数据库数据进行保持ecache和redis
						redisUtil.delete(key);
						circlePostBean = circlePostMapper.findById(CirclePostBean.class, postId);
						if(circlePostBean != null){
							try {
								redisUtil.addSerialize(key, SerializeUtil.serializeObject(circlePostBean));
								systemCache.addCache(key, circlePostBean);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}else{//redis没有的处理
				circlePostBean = circlePostMapper.findById(CirclePostBean.class, postId);
				if(circlePostBean != null){
					try {
						redisUtil.addSerialize(key, SerializeUtil.serializeObject(circlePostBean));
						systemCache.addCache(key, circlePostBean);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}else{
			circlePostBean = (CirclePostBean)obj;
		}
			
		return circlePostBean;
	}
	
	/**
	 * 获取正常状态的帖子对象（校验是否帖子属于圈子的用户的，不是返回null对象）
	 * @param circleId
	 * @param postId
	 * @return
	 */
	public CirclePostBean getNormalCirclePostBean(int circleId, int postId){
		return getNormalCirclePostBean(circleId, postId, null);
	}
	
	/**
	 * 获取帖子在redis的key
	 * @param postId
	 * @return
	 */
	public static String getPostKey(int postId){
		return ConstantsUtil.CIRCLE_REDIS + "P_" + postId;
	}
	
	/**
	 * 获取用户的圈子帖子列表关系在redis的key
	 * @param circleId
	 * @param userId
	 * @return
	 */
	public static String getUserCirclePostKey(int userId){
		return ConstantsUtil.CIRCLE_REDIS +"_U_" + userId;
	}
	
	/**
	 * 获取圈子里面的用户的帖子列表关系在redis的key
	 * @param circleId
	 * @param postId
	 * @param userId
	 * @return
	 */
	public static String getUserPostPostKey(int circleId, int userId){
		return ConstantsUtil.CIRCLE_REDIS+ circleId +"_U_" + userId;
	}
	
	/**
	 * 获取热门帖子列表关系在redis的key
	 * @param circleId
	 * @param userId
	 * @return
	 */
	public static String getHotestPostKey(){
		return ConstantsUtil.CIRCLE_REDIS +"_H";
	}
}
