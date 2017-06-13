package com.cn.leedane.service.impl.circle;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.mapper.circle.CircleCreateLimitMapper;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.mapper.circle.MemberMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.MemberBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.circle.CircleService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.JsonUtil;
import com.cn.leedane.utils.RelativeDateFormat;
import com.cn.leedane.utils.ResponseMap;
import com.cn.leedane.utils.SqlUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 圈子service实现类
 * @author LeeDane
 * 2017年5月30日 下午8:13:59
 * version 1.0
 */
@Service("circleService")
public class CircleServiceImpl extends AdminRoleCheckService implements CircleService<CircleBean>{
	Logger logger = Logger.getLogger(getClass());
	
	/**
	 * 圈子创建者状态
	 */
	public static final int CIRCLE_CREATER = 1;
	
	/**
	 * 圈子管理者状态
	 */
	public static final int CIRCLE_MANAGER = 2;
	
	/**
	 * 圈子普通成员状态
	 */
	public static final int CIRCLE_NORMAL = 0;
	
	@Autowired
	private CircleMapper circleMapper;
	
	@Autowired
	private MemberMapper memberMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private CircleHandler circleHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private CircleCreateLimitMapper circleCreateLimitMapper;
	
	@Override
	public Map<String, Object> init(UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->init(), user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		message.put("score", 10003); //获取总的贡献值
		
		List<CircleBean> circleBeans = circleHandler.getAllCircles(user);
		List<Map<String, Object>> allCircles = new ArrayList<Map<String,Object>>(4);
		int myCircleNumber = 0, allCircleNumber = 0;
		
		if(CollectionUtil.isNotEmpty(circleBeans)){
			allCircleNumber = circleBeans.size();
			int count = 0;
			for(CircleBean circleBean: circleBeans){
				Map<String, Object> cc = new HashMap<String, Object>();
				cc.put("cid", circleBean.getId());
				cc.put("cname", circleBean.getName());
				if(circleBean.getCreateUserId() == user.getId()){
					myCircleNumber ++;
				}
				if(count < 4)
					allCircles.add(cc);
				
				count++;
			}
		}
		
		message.put("myCircleNumber", myCircleNumber); //获取我的圈子数量		
		message.put("allCircleNumber", allCircleNumber); //获取所有的圈子数量
		message.put("allCircles", allCircles); //获取所有的圈子
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取自己所有圈子的初始化数据"), "init()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}

	@Override
	public Map<String, Object> check(JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->check(), user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		int number = circleCreateLimitMapper.getNumber(user.getId(), ConstantsUtil.STATUS_NORMAL);
		if(number < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.超出您能创建的圈子数量.value));
			message.put("responseCode", EnumUtil.ResponseCode.超出您能创建的圈子数量.value);
			return message.getMap();
		}
		
		message.put("isSuccess", true);
		message.put("message", number);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> create(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->create():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		String name = JsonUtil.getStringValue(jo, "name"); //圈子的名称
		String describe = JsonUtil.getStringValue(jo, "describe");
		
		ResponseMap message = new ResponseMap();
		if(StringUtil.isNull(name)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.圈子名称不能为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.圈子名称不能为空.value);
			return message.getMap();
		}
		
		//判断名称是否存在，避免页面报异常
		if(CollectionUtil.isNotEmpty(circleMapper.isExists(name))){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子名称已经被占用啦.value));
			message.put("responseCode", EnumUtil.ResponseCode.该圈子名称已经被占用啦.value);
			return message.getMap();
		}
		
		Date createTime = new Date();
		
		CircleBean circleBean = new CircleBean();
		circleBean.setCreateTime(createTime);
		circleBean.setCreateUserId(user.getId());
		circleBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		circleBean.setCircleDesc(describe);
		circleBean.setName(name);
		
		boolean result = circleMapper.save(circleBean) > 0;
		if(result){
			//添加一条成员记录
			MemberBean member = new MemberBean();
			member.setCreateTime(createTime);
			member.setCreateUserId(user.getId());
			member.setCircleId(circleBean.getId());
			member.setMemberId(user.getId());
			member.setRoleType(CIRCLE_CREATER);
			member.setStatus(ConstantsUtil.STATUS_NORMAL);
			
			result = memberMapper.save(member) > 0;
			if(result){
				message.put("isSuccess", true);
				message.put("message", "您已成功创建名称为《"+ name +"》的圈子。");
				message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
			}else{
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
				message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
			}
			
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"创建名称为：", name,"的圈子，结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "create()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}


	@Override
	public Map<String, Object> update(JSONObject jo, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->update():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		int cid = JsonUtil.getIntValue(jo, "cid", 0);
		if(cid < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
			return message.getMap();
		}
		CircleBean circleBean = circleMapper.findById(CircleBean.class, cid);
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		//判断是否是圈主
		if(circleBean.getCreateUserId() != user.getId()/* && circleHandler.getRoleCode(user, cid) != CIRCLE_MANAGER*/)
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));
		
		//自动填充更新的bean
		SqlUtil sqlUtil = new SqlUtil();
		sqlUtil.getUpdateBean(jo, circleBean);
		circleBean.setModifyTime(new Date());
		circleBean.setModifyUserId(user.getId());
		
		boolean result = circleMapper.update(circleBean) > 0;
		if(result){
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.修改成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		return message.getMap();
	}


	@Override
	public Map<String, Object> delete(int cid, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->delete():cid=" +cid +", user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		CircleBean circleBean = circleMapper.findById(CircleBean.class, cid);
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		//判断是否是圈主
		if(circleBean.getCreateUserId() != user.getId()/* && circleHandler.getRoleCode(user, cid) != CIRCLE_MANAGER*/)
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));

		//这里是逻辑删除
		circleBean.setStatus(ConstantsUtil.STATUS_DELETE);
		boolean result = circleMapper.update(circleBean) > 0;
		if(result){
			//通知所有的成员该圈子已经被删除
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> paging(JSONObject jsonObject, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->paging():jo="+jsonObject.toString());
		ResponseMap message = new ResponseMap();
		int pageSize = JsonUtil.getIntValue(jsonObject, "page_size", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		int currentIndex = JsonUtil.getIntValue(jsonObject, "current", 0); //当前的索引
		int total = JsonUtil.getIntValue(jsonObject, "total", 0); //总数
		int start = SqlUtil.getPageStart(currentIndex, pageSize, total);
		List<Map<String, Object>> rs = circleMapper.paging(user.getId(), CIRCLE_MANAGER, start, pageSize, ConstantsUtil.STATUS_NORMAL);
		if(CollectionUtil.isNotEmpty(rs)){
			int createUserId = 0;			
			for(int i = 0; i < rs.size(); i++){
				createUserId = StringUtil.changeObjectToInt(rs.get(i).get("create_user_id"));
				rs.get(i).put("create_user_name", userHandler.getUserName(createUserId));
				rs.get(i).put("create_time", RelativeDateFormat.format(DateUtil.stringToDate(StringUtil.changeNotNull(rs.get(i).get("create_time")))));
				if(rs.get(i).get("admins") != null){
					String strs = StringUtil.changeNotNull(rs.get(i).get("admins"));
					if(StringUtil.isNotNull(strs)){
						Map<String, Object> mp = new HashMap<String, Object>();
						String[] strArray = strs.split(",");
						for(String s: strArray)
							mp.put(s, userHandler.getUserName(Integer.parseInt(s)));
						
						rs.get(i).put("adminMap", mp);
					}
				}
			}
			message.put("total", circleMapper.getAllCircles(createUserId, ConstantsUtil.STATUS_NORMAL).size());
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取圈子列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public CircleBean findById(int cid) {
		logger.info("CircleServiceImpl-->findById():cid="+cid);
		return circleMapper.findById(CircleBean.class, cid);
	}
}
