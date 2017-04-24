package com.cn.leedane.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.redis.util.RedisUtil;
import com.cn.leedane.service.SqlBaseService;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.StringUtil;

/**
 * 心情处理类
 * @author LeeDane
 * 2016年3月22日 上午10:32:38
 * Version 1.0
 */
@Component
public class MoodHandler {
	@Autowired
	private SqlBaseService<IDBean> sqlBaseService;
	
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
	 * 获取心情的详细信息(注意：有照片的情况下，只缓存该照片已经上传到七牛存储服务器的，未上传的情况不做缓存)
	 * @param moodId
	 * @param user
	 * @return
	 */
	public List<Map<String, Object>> getMoodDetail(int moodId, UserBean user){
		return getMoodDetail(moodId, user, false);
	}
	
	/**
	 * 获取心情的详细信息(注意：有照片的情况下，只缓存该照片已经上传到七牛存储服务器的，未上传的情况不做缓存)
	 * @param moodId
	 * @param user
	 * @param onlyContent true表示只获取内容
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMoodDetail(int moodId, UserBean user, boolean onlyContent){
		String moodKey = getMoodKey(moodId);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		JSONArray jsonArray = new JSONArray();
		if(!redisUtil.hasKey(moodKey)){
			StringBuffer sql = new StringBuffer();
			sql.append("select m.id, m.create_user_id, m.froms, m.content, m.has_img, m.can_comment, m.can_transmit, date_format(m.create_time,'%Y-%m-%d %H:%i:%s') create_time,location,longitude,latitude");
			//sql.append(" ,(case when has_img = 1 then (select qiniu_path from "+DataTableType.文件.value+" where status = ? and table_name='"+DataTableType.心情.value+"' and table_uuid = m.uuid and pic_size=?) else '' end) path");
			sql.append(" ,uuid");
			sql.append(" from "+DataTableType.心情.value+" m");
			sql.append(" where m.id=? ");
			//sql.append(" and m.status = ?");
			list = sqlBaseService.executeSQL(sql.toString(), moodId/*, ConstantsUtil.STATUS_NORMAL*/);
			if(list != null && list.size() >0){
				int createUserId;
				for(int i = 0; i < list.size(); i++){
					createUserId = StringUtil.changeObjectToInt(list.get(i).get("create_user_id"));
					list.get(i).putAll(userHandler.getBaseUserInfo(createUserId));
				}
			}
			jsonArray = JSONArray.fromObject(list);
			redisUtil.addString(moodKey, jsonArray.toString());
		}else{
			String mood = redisUtil.getString(moodKey);
			//要把null转化成“”字符串，在json转化才不会报错：net.sf.json.JSONException: null object
			mood = mood.replaceAll("null", "\"\"");
			if(StringUtil.isNotNull(mood) && !"[null]".equalsIgnoreCase(mood)){
				jsonArray = JSONArray.fromObject(mood);
				list = (List<Map<String, Object>>) jsonArray;
			}
		}
		
		if(list != null && list.size() == 1 && !onlyContent){
			list.get(0).put("comment_number", commentHandler.getCommentNumber(moodId, DataTableType.心情.value));
			list.get(0).put("transmit_number", transmitHandler.getTransmitNumber(moodId, DataTableType.心情.value));
			list.get(0).put("zan_number", zanHandler.getZanNumber(moodId, DataTableType.心情.value));
			list.get(0).put("zan_users", zanHandler.getZanUser(moodId, DataTableType.心情.value, user, 6));
			int createUserId = StringUtil.changeObjectToInt(list.get(0).get("create_user_id"));
			if( createUserId > 0)
				//填充图片信息
				list.get(0).putAll(userHandler.getBaseUserInfo(createUserId));
		}
		return list;
	}
	/**
	 * 获取心情的内容
	 * @param moodId
	 * @param user
	 * @return
	 */
	public String getMoodContent(int moodId, UserBean user){
		List<Map<String, Object>> list = getMoodDetail(moodId, user, true);
		String content = null;
		if(list != null && list.size() == 1){
			content = StringUtil.changeNotNull(list.get(0).get("content"));
		}
		return content;
	}
	/**
	 * 获得心情的全部图片列表
	 * @param tableName
	 * @param tableUuid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> getMoodIms(String tableName, String tableUuid){
		String moodImgsKey = getMoodImgsKey(tableName, tableUuid);
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		JSONArray jsonArray = new JSONArray();
		if(!redisUtil.hasKey(moodImgsKey)){
			StringBuffer sql = new StringBuffer();
			sql.append("select qiniu_path, pic_size, pic_order, width, height, lenght ");
			sql.append(" from "+DataTableType.文件.value);
			sql.append(" where status = ? and table_name=? and table_uuid = ? and is_upload_qiniu=? order by pic_order,id");
			list = sqlBaseService.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableUuid, true);
			if(list != null && list.size() > 0){
				jsonArray = JSONArray.fromObject(list);
				redisUtil.addString(moodImgsKey, jsonArray.toString());
			}
		}else{
			String moodImgs = redisUtil.getString(moodImgsKey);
			if(StringUtil.isNotNull(moodImgs)){
				jsonArray = JSONArray.fromObject(moodImgs);
				list = (List<Map<String, Object>>) jsonArray;
			}
		}
		
		
		return list == null? new ArrayList<Map<String,Object>>(): list;
	}
	/**
	 * 获得心情指定大小的图片
	 * @param tableName
	 * @param tableUuid
	 * @param picSize
	 * @return
	 */
	public String getMoodImg(String tableName, String tableUuid, String picSize){
		String moodImgKey = getMoodImgKey(tableName, tableUuid, picSize);
		if(!redisUtil.hasKey(moodImgKey)){
			String path = null;
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			StringBuffer sql = new StringBuffer();
			sql.append("select qiniu_path, pic_size, pic_order, width, height, lenght ");
			sql.append(" from "+DataTableType.文件.value);
			sql.append(" where status = ? and table_name=? and table_uuid = ? and is_upload_qiniu=? and pic_size =? order by pic_order,id");
			list = sqlBaseService.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableUuid, true, picSize);
			
			//找指定大小的图片,找原图
			if(list == null || list.size() == 0 ){
				list = sqlBaseService.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, tableName, tableUuid, true, "source");	
			}
			
			if(list != null && list.size() > 0 ){
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
					redisUtil.addString(moodImgKey, path);
				}
			}
			return path;
		}else{
			System.out.println("moodImgKey:"+moodImgKey);
			return redisUtil.getString(moodImgKey);
		}
	}
	
	/**
	 * 删除在redis
	 * @param moodId
	 * @param tableName
	 * @param tableUuid
	 * @return
	 */
	public boolean delete(int moodId, String tableName, String tableUuid){
		
		String moodKey = getMoodKey(moodId);
		if(StringUtil.isNotNull(tableName) && StringUtil.isNotNull(tableUuid)){
			String moodImgKey30 = getMoodImgKey(tableName, tableUuid, "30x30");
			String moodImgKey60 = getMoodImgKey(tableName, tableUuid, "60x60");
			String moodImgKey80 = getMoodImgKey(tableName, tableUuid, "80x80");
			String moodImgKey100 = getMoodImgKey(tableName, tableUuid, "100x100");
			String moodImgKey120 = getMoodImgKey(tableName, tableUuid, "120x120");
			String moodImgKey320 = getMoodImgKey(tableName, tableUuid, "320x400");
			String moodImgKeySource = getMoodImgKey(tableName, tableUuid, "source");
			String moodImgKeyDefault = getMoodImgKey(tableName, tableUuid, "default");
			String moodImgsKey = getMoodImgsKey(tableName, tableUuid);
			
			redisUtil.delete(moodImgKey30);
			redisUtil.delete(moodImgKey60);
			redisUtil.delete(moodImgKey80);
			redisUtil.delete(moodImgKey100);
			redisUtil.delete(moodImgKey120);
			redisUtil.delete(moodImgKey320);
			redisUtil.delete(moodImgKeySource);
			redisUtil.delete(moodImgKeyDefault);
			redisUtil.delete(moodImgsKey);
		}	
		return redisUtil.delete(moodKey);
	}
	
	/**
	 * 获取心情在redis的key
	 * @param moodId
	 * @return
	 */
	public static String getMoodKey(int moodId){
		return ConstantsUtil.MOOD_REDIS +moodId;
	}
	
	/**
	 * 获取心情图片在redis的key
	 * @param moodId
	 * @return
	 */
	public static String getMoodImgsKey(String tableName, String tableUuid){
		return ConstantsUtil.MOOD_IMGS_REDIS +tableName +"_" +tableUuid;
	}
	
	/**
	 * 获取心情单张图片在redis的key
	 * @param moodId
	 * @return
	 */
	public static String getMoodImgKey(String tableName, String tableUuid, String picSize){
		return ConstantsUtil.MOOD_IMG_REDIS +tableName +"_" +tableUuid+"_"+picSize;
	}
}
