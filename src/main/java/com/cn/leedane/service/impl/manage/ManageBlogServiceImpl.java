package com.cn.leedane.service.impl.manage;

import com.cn.leedane.handler.BlogHandler;
import com.cn.leedane.mapper.BlogMapper;
import com.cn.leedane.mapper.OperateLogMapper;
import com.cn.leedane.model.BlogBean;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.manage.ManageBlogService;
import com.cn.leedane.springboot.ElasticSearchUtil;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.elasticsearch.client.transport.TransportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 我的博客管理信息的service的实现类
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Service("manageBlogService")
public class ManageBlogServiceImpl extends AdminRoleCheckService implements ManageBlogService<BlogBean> {
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private OperateLogMapper operateLogMapper;

	@Autowired
	private TransportClient transportClient;

	@Autowired
	private BlogMapper blogMapper;

	@Autowired
	private ElasticSearchUtil elasticSearchUtil;

	@Autowired
	private BlogHandler blogHandler;

	@Override
	public LayuiTableResponseModel list(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageBlogServiceImpl-->list():jo="+ jsonObject);
		int current = JsonUtil.getIntValue(jsonObject, "page", 0);
		int rows = JsonUtil.getIntValue(jsonObject, "limit", 10);
		int start = SqlUtil.getPageStart(current -1, rows, 0);
		List<Map<String, Object>> rs = blogMapper.myList(user.getId(), start, rows);
		if(rs !=null && rs.size() > 0){
			long id;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				Map<String, Object> blog = rs.get(i);
				//在非获取指定表下的评论列表的情况下的前面35个字符
				id = StringUtil.changeObjectToLong(blog.get("id"));
				blog.put("link", "<a href='/dt/"+id+"' target='_blank' style='color: green;'>查看详情</a>");
			}
		}
		LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
		responseModel.setData(rs).setCount(SqlUtil.getTotalByList(blogMapper.getTotalByUser(EnumUtil.DataTableType.博客.value, user.getId()))).code(0);
		return responseModel;
	}

	@Override
	public LayuiTableResponseModel draft(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageBlogServiceImpl-->draft():jo="+ jsonObject);
		int current = JsonUtil.getIntValue(jsonObject, "page", 0);
		int rows = JsonUtil.getIntValue(jsonObject, "limit", 10);
		int start = SqlUtil.getPageStart(current -1, rows, 0);
		List<Map<String, Object>> rs = blogMapper.myDraft(user.getId(), start, rows);
		/*if(rs !=null && rs.size() > 0){
			long id;
			//为名字备注赋值
			for(int i = 0; i < rs.size(); i++){
				Map<String, Object> blog = rs.get(i);
				//在非获取指定表下的评论列表的情况下的前面35个字符
				id = StringUtil.changeObjectToLong(blog.get("id"));
				blog.put("link", "<a href='/dt/"+id+"' target='_blank' style='color: green;'>查看详情</a>");
			}
		}*/
		LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
		responseModel.setData(rs).setCount(SqlUtil.getTotalByList(blogMapper.myDraftTotal(user.getId()))).code(0);
		return responseModel;
	}

	@Override
	public ResponseModel delete(JSONObject jsonObject, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageBlogServiceImpl-->delete():jo="+ jsonObject);
		long id = JsonUtil.getLongValue(jsonObject, "id", 0);
		ParameterUnspecificationUtil.checkLong(id , "id 必须大于0");

		BlogBean blogBean = blogMapper.findById(BlogBean.class, id);
		if(blogBean == null){
			//删除缓存
			elasticSearchUtil.delete(EnumUtil.DataTableType.博客.value, id);
			throw new NullPointerException("博客记录不存在");
		}
		checkAdmin(user, blogBean.getCreateUserId());

		//检查是否是自己的记录
		if(blogBean.getCreateUserId() != user.getId())
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		boolean result = this.blogMapper.deleteById(BlogBean.class, id) > 0;
		ResponseModel responseModel = new ResponseModel();
		if(result){
			//删除es缓存
			elasticSearchUtil.delete(EnumUtil.DataTableType.博客.value, id);

			//删除redis相关联的缓存
			blogHandler.delete(id);
			responseModel.ok().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作成功.value));
		}else{
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.操作失败.value)).code(EnumUtil.ResponseCode.操作失败.value);
		}

		String subject = user.getAccount() + "删除了博客《"+ blogBean.getTitle() + "》" + StringUtil.getSuccessOrNoStr(result);
		this.operateLogService.saveOperateLog(user, request, new Date(), subject, "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}
}
