package com.cn.leedane.service.impl;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.FileUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;
import com.cn.leedane.mapper.GalleryMapper;
import com.cn.leedane.model.GalleryBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.GalleryService;
import com.cn.leedane.service.OperateLogService;

/**
 * 图库service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:45:25
 * Version 1.0
 */
@Service("galleryService")
public class GalleryServiceImpl implements GalleryService<GalleryBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private GalleryMapper galleryMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Override
	public boolean isExist(UserBean user, String path) {
		return SqlUtil.getBooleanByList(galleryMapper.isExist(user.getId(), path));
	}
	
	@Override
	public Map<String, Object> addLink(JSONObject jo, UserBean user,
			HttpServletRequest request) throws Exception {
		logger.info("GalleryServiceImpl-->add():JSONObject="+jo.toString());
		
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		
		int width = JsonUtil.getIntValue(jo, "width", 0); //获取参数中宽度的值,可以为空
		int height = JsonUtil.getIntValue(jo, "height", 0); //获取参数中高度的值,可以为空
		long length = JsonUtil.getLongValue(jo, "length", 0); //获取参数中高度的值,可以为空
		String path = JsonUtil.getStringValue(jo, "path"); //获取参数中图片路径的值，不能为空
		String desc = JsonUtil.getStringValue(jo, "desc"); //获取参数中描述信息的值，可以为空
		
		if(StringUtil.isNull(path)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message;
		}
		
		if(isExist(user, path)){
			message.put("message", "您已添加过该链接，请勿重复操作！");
			message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
			return message;
		}
		
		//宽高有为0，需要网络获取宽高
		if(width ==0 || height == 0){
			GalleryBean gBean = FileUtil.getNetWorkImgAttrs(user, path);
			if(gBean == null){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
				message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
				return message;
			}
			
			width = gBean.getWidth();
			height = gBean.getHeight();
			length = gBean.getLength();
			path = gBean.getPath();
		}
		
		if(length > 1024 * 1024 * 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.图片大于1M无法上传.value));
			message.put("responseCode", EnumUtil.ResponseCode.图片大于1M无法上传.value);
			return message;
		}
		
		GalleryBean bean = new GalleryBean();
		bean.setCreateUserId(user.getId());
		bean.setCreateTime(new Date());
		bean.setGalleryDesc(desc);
		bean.setHeight(height);
		bean.setLength(length);
		bean.setPath(path);
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		bean.setWidth(width);
		if(galleryMapper.save(bean) > 0){
			message.put("isSuccess", true);
			message.put("message", "添加到图库成功");
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		
		//保存操作日志
		String subject = user.getAccount() + "操作加入图库，链接是：" + path;
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "addLink", 1 , 0);
		
		return message;
	}

	@Override
	public Map<String, Object> delete(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("GalleryServiceImpl-->delete():JSONObject="+jo.toString());
		
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("isSuccess", false);
		
		int galleryId = JsonUtil.getIntValue(jo, "gid"); //获取参数中需要删除的图库的ID
		if(galleryId == 0){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message;
		}
		GalleryBean galleryBean = galleryMapper.findById(GalleryBean.class, galleryId);
		if(!user.isAdmin() && (galleryBean == null || galleryBean.getCreateUserId() != user.getId())){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有操作权限.value);
			return message;
		}
		
		boolean result = galleryMapper.deleteById(GalleryBean.class, galleryId) > 0;
		if(result){
			message.put("isSuccess", true);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库删除数据失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库删除数据失败.value);
		}
		
		//保存操作日志
		String subject = user.getAccount() + "删除图库ID为："+galleryId+"的数据"+StringUtil.getSuccessOrNoStr(result);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "delete()", StringUtil.changeBooleanToInt(result) , 0);
		return message;
	}
	
	@Override
	public List<Map<String, Object>> getGalleryByLimit(JSONObject jo,
			UserBean user, HttpServletRequest request) {	
		long start = System.currentTimeMillis();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int uid = JsonUtil.getIntValue(jo, "uid", user.getId()); //操作的用户的id
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		
		logger.info("GalleryServiceImpl-->getGalleryByLimit():JSONObject="+jo.toString());
		
		StringBuffer sql = new StringBuffer();
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select g.id, g.path, g.width, g.height, g.length, g.create_user_id, date_format(g.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" , g.gallery_desc, u.account");
			sql.append(" from "+DataTableType.图库.value+" g inner join "+DataTableType.用户.value+" u on u.id = g.create_user_id where g.status = ? ");
			sql.append(" and g.create_user_id = ?");
			sql.append(" order by g.id desc limit 0,?");
			rs = galleryMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, uid, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select g.id, g.path, g.width, g.height, g.length, g.create_user_id, date_format(g.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" , g.gallery_desc, u.account");
			sql.append(" from "+DataTableType.图库.value+" g inner join "+DataTableType.用户.value+" u on u.id = g.create_user_id where g.status = ? ");
			sql.append(" and g.create_user_id = ?");
			sql.append(" and g.id < ? order by g.id desc limit 0,? ");
			rs = galleryMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, uid, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select g.id, g.path, g.width, g.height, g.length, g.create_user_id, date_format(g.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" , g.gallery_desc, u.account");
			sql.append(" from "+DataTableType.图库.value+" g inner join "+DataTableType.用户.value+" u on u.id = g.create_user_id where g.status = ? ");
			sql.append(" and g.create_user_id = ?");
			sql.append(" and g.id > ? limit 0,?  ");
			rs = galleryMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, uid, firstId, pageSize);
		}
		
		//保存操作日志
		try {
			operateLogService.saveOperateLog(user, request, null, user.getAccount()+"获取用户ID为"+uid +"的用户的图库列表", "getGalleryByLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		long end = System.currentTimeMillis();
		System.out.println("获取图库列表总计耗时：" +(end - start) +"毫秒");
		return rs;
	}

	
	
}
