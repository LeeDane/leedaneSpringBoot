package com.cn.leedane.service.impl;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.CategoryHandler;
import com.cn.leedane.mapper.GalleryMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.GalleryService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 图库service实现类
 * @author LeeDane
 * 2016年7月12日 下午1:45:25
 * Version 1.0
 */
@Service("galleryService")
public class GalleryServiceImpl extends AdminRoleCheckService implements GalleryService<GalleryBean> {
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private GalleryMapper galleryMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private CategoryHandler categoryHandler;
	@Override
	public boolean isExist(UserBean user, String path) {
		return SqlUtil.getBooleanByList(galleryMapper.isExist(user.getId(), path));
	}
	
	@Override
	public Map<String, Object> manageLink(JSONObject jo, UserBean user,
			HttpRequestInfoBean request){
		logger.info("GalleryServiceImpl-->add():JSONObject="+jo.toString());
		
		ResponseMap message = new ResponseMap();
		int id = JsonUtil.getIntValue(jo, "id", 0); //获取参数中宽度的值,可以为空

		if(id > 0){
			GalleryBean galleryBean = galleryMapper.findById(GalleryBean.class, id);
			if(galleryBean == null || galleryBean.getStatus() != ConstantsUtil.STATUS_NORMAL)
				throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该图库不存在.value));
		}

		int width = JsonUtil.getIntValue(jo, "width", 0); //获取参数中宽度的值,可以为空
		int height = JsonUtil.getIntValue(jo, "height", 0); //获取参数中高度的值,可以为空
		long length = JsonUtil.getLongValue(jo, "length", 0); //获取参数中高度的值,可以为空
		String path = JsonUtil.getStringValue(jo, "path"); //获取参数中图片路径的值，不能为空
		String desc = JsonUtil.getStringValue(jo, "desc"); //获取参数中描述信息的值，可以为空
		int category = JsonUtil.getIntValue(jo, "category"); //获取参数中描述信息的值，可以为空
		
		//为了配合七牛云存储的文件的压缩获取，自动在链接添加了?imageslim，在此需要处理一下
		path = path.replace("?imageslim", "");
		
		if(StringUtil.isNull(path)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
		
		if(isExist(user, path)){
			message.put("message", "您已添加过该链接，请勿重复操作！");
			message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
			return message.getMap();
		}
		
		//宽高有为0，需要网络获取宽高
		if(width ==0 || height == 0){
			GalleryBean gBean = FileUtil.getNetWorkImgAttrs(user, path);
			if(gBean == null){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.服务器处理异常.value));
				message.put("responseCode", EnumUtil.ResponseCode.服务器处理异常.value);
				return message.getMap();
			}
			
			width = gBean.getWidth();
			height = gBean.getHeight();
			length = gBean.getLength();
			path = gBean.getPath();
		}
		
		if(length > 1024 * 1024 * 5){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.图片大于5M无法上传.value));
			message.put("responseCode", EnumUtil.ResponseCode.图片大于5M无法上传.value);
			return message.getMap();
		}
		
		GalleryBean bean = new GalleryBean();
		bean.setCreateUserId(user.getId());
		bean.setCreateTime(new Date());
		bean.setGalleryDesc(desc);
		bean.setHeight(height);
		bean.setLength(length);
		bean.setPath(path+"?imageslim");
		bean.setStatus(ConstantsUtil.STATUS_NORMAL);
		bean.setWidth(width);
		if(category > 0)
			bean.setCategoryId(category);
		if(id > 0){
			bean.setId(id);
			if(galleryMapper.update(bean) > 0){
				message.put("isSuccess", true);
				message.put("message", "编辑图库成功");
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
			}
		}else{
			if(galleryMapper.save(bean) > 0){
				message.put("isSuccess", true);
				message.put("message", "添加到图库成功");
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
			}
		}
		
		//保存操作日志
		String subject = user.getAccount() + "操作加入图库，链接是：" + path;
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "manageLink", 1 , EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> delete(int galleryId, JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("GalleryServiceImpl-->delete():JSONObject="+jo.toString() +", gid="+ galleryId);
		
		ResponseMap message = new ResponseMap();
		
		if(galleryId == 0){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.某些参数为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.某些参数为空.value);
			return message.getMap();
		}
		GalleryBean galleryBean = galleryMapper.findById(GalleryBean.class, galleryId);
		
		//检验是否是管理员或者创建者权限
		checkAdmin(user, galleryBean.getCreateUserId());
		
		boolean result = galleryMapper.deleteById(GalleryBean.class, galleryId) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库删除数据失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库删除数据失败.value);
		}
		
		//保存操作日志
		String subject = user.getAccount() + "删除图库ID为："+galleryId+"的数据"+StringUtil.getSuccessOrNoStr(result);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "delete()", StringUtil.changeBooleanToInt(result) , EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public List<Map<String, Object>> all(JSONObject jo,
			UserBean user, HttpRequestInfoBean request) {	
		long start = System.currentTimeMillis();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int uid = JsonUtil.getIntValue(jo, "uid", user.getId()); //操作的用户的id
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int lastId = JsonUtil.getIntValue(jo, "last_id"); //开始的页数
		int firstId = JsonUtil.getIntValue(jo, "first_id"); //结束的页数
		int categoryId = JsonUtil.getIntValue(jo, "category", -1); //结束的页数
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
		
		logger.info("GalleryServiceImpl-->getGalleryByLimit():JSONObject="+jo.toString());
		
		StringBuffer sql = new StringBuffer();
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select g.id, g.path, g.width, g.height, g.length, g.create_user_id, date_format(g.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" , g.gallery_desc, u.account");
			sql.append(" from "+DataTableType.图库.value+" g inner join "+DataTableType.用户.value+" u on u.id = g.create_user_id where g.status = ? ");
			sql.append(" and g.create_user_id = ?");
			sql.append(getCategorySql(categoryId));
			sql.append(" order by g.id desc limit 0,?");
			rs = galleryMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, uid, pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			sql.append("select g.id, g.path, g.width, g.height, g.length, g.create_user_id, date_format(g.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" , g.gallery_desc, u.account");
			sql.append(" from "+DataTableType.图库.value+" g inner join "+DataTableType.用户.value+" u on u.id = g.create_user_id where g.status = ? ");
			sql.append(" and g.create_user_id = ?");
			sql.append(getCategorySql(categoryId));
			sql.append(" and g.id < ? order by g.id desc limit 0,? ");
			rs = galleryMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, uid, lastId, pageSize);
		//上刷新
		}else if("uploading".equalsIgnoreCase(method)){
			sql.append("select g.id, g.path, g.width, g.height, g.length, g.create_user_id, date_format(g.create_time,'%Y-%m-%d %H:%i:%s') create_time");
			sql.append(" , g.gallery_desc, u.account");
			sql.append(" from "+DataTableType.图库.value+" g inner join "+DataTableType.用户.value+" u on u.id = g.create_user_id where g.status = ? ");
			sql.append(" and g.create_user_id = ?");
			sql.append(getCategorySql(categoryId));
			sql.append(" and g.id > ? limit 0,?  ");
			rs = galleryMapper.executeSQL(sql.toString(), ConstantsUtil.STATUS_NORMAL, uid, firstId, pageSize);
		}
		
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"获取用户ID为"+uid +"的用户的图库列表", "getGalleryByLimit()", ConstantsUtil.STATUS_NORMAL, 0);
		
		long end = System.currentTimeMillis();
		logger.info("获取图库列表总计耗时：" +(end - start) +"毫秒");
		return rs;
	}

	@Override
	public Map<String, Object> paging(JSONObject jo,
									  UserBean user, HttpRequestInfoBean request){
		logger.info("EventServiceImpl-->paging():jo=" +jo.toString());
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int pageSize = JsonUtil.getIntValue(jo, "limit", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jo, "page", 0); //当前的索引页
		int total = JsonUtil.getIntValue(jo, "total", 0); //当前的索引页
		int start = SqlUtil.getPageStart(currentIndex -1, pageSize, total);
		ResponseMap message = new ResponseMap();
		rs = galleryMapper.paging(user.getId(), start, pageSize, ConstantsUtil.STATUS_NORMAL);
		if(CollectionUtil.isNotEmpty(rs)){
			for(Map<String, Object> map: rs){
//				map.put("path", "<img src='"+ StringUtil.changeNotNull(map.get("path")) +"'/>");
				map.put("format-length", FileUtil.fileSizeFormat(StringUtil.changeNotNull(map.get("length"))));
				CategoryBean categoryBean = categoryHandler.getCategoryBean(StringUtil.changeObjectToInt(map.get("category_id")));
				map.put("category", categoryBean != null ? categoryBean.getText(): "");
			}

		}
		message.put("data", rs);
		message.put("count", SqlUtil.getTotalByList(galleryMapper.getTotal(DataTableType.图库.value, " e where create_user_id="+ user.getId())));
		message.put("msg", "");
		message.put("code", 0);
		return message.getMap();
	}

	/**
	 * 构建查询分类的sql语句
	 * @param categoryId
	 * @return
	 */
	public String getCategorySql(int categoryId){
		if(categoryId < 0)
			return "";
		return " and category_id = "+ categoryId +" ";
	}
	
}
