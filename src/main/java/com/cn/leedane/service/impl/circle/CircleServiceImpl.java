package com.cn.leedane.service.impl.circle;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.exception.RE404Exception;
import com.cn.leedane.handler.NotificationHandler;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.handler.circle.CircleHandler;
import com.cn.leedane.handler.circle.CircleMemberHandler;
import com.cn.leedane.handler.circle.CirclePostHandler;
import com.cn.leedane.mapper.circle.CircleClockInMapper;
import com.cn.leedane.mapper.circle.CircleContributionMapper;
import com.cn.leedane.mapper.circle.CircleCreateLimitMapper;
import com.cn.leedane.mapper.circle.CircleMapper;
import com.cn.leedane.mapper.circle.CircleMemberMapper;
import com.cn.leedane.mapper.circle.CircleSettingMapper;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.VisitorBean;
import com.cn.leedane.model.circle.CircleBean;
import com.cn.leedane.model.circle.CircleMemberBean;
import com.cn.leedane.model.circle.CircleSettingBean;
import com.cn.leedane.model.circle.CircleUserPostsBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.VisitorService;
import com.cn.leedane.service.circle.CircleService;
import com.cn.leedane.utils.CollectionUtil;
import com.cn.leedane.utils.ConstantsUtil;
import com.cn.leedane.utils.DateUtil;
import com.cn.leedane.utils.EnumUtil;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import com.cn.leedane.utils.EnumUtil.NotificationType;
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
	private CircleMemberMapper circleMemberMapper;
	
	@Autowired
	private CircleSettingMapper circleSettingMapper;
	
	@Autowired
	private CircleClockInMapper circleClockInMapper;
	
	@Autowired
	private CircleContributionMapper circleContributionMapper;

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private CircleHandler circleHandler;
	
	@Autowired
	private CircleMemberHandler circleMemberHandler;
	
	@Autowired
	private CirclePostHandler circlePostHandler;
	
	@Autowired
	private UserHandler userHandler;
	
	@Autowired
	private CircleCreateLimitMapper circleCreateLimitMapper;
	
	@Autowired
	private NotificationHandler notificationHandler;
	
	@Autowired
	private VisitorService<VisitorBean> visitorService;
	
	@Override
	public Map<String, Object> init(UserBean user, HttpServletRequest request) {
		logger.info("CircleServiceImpl-->init(), user=" +(user != null ? user.getAccount(): "用户还未登录"));
		ResponseMap message = new ResponseMap();
		message.put("score", 10003); //获取总的贡献值
		
		if(user != null){
			List<CircleBean> circleBeans = circleHandler.getAllCircles(user);
			List<Map<String, Object>> allCircles = new ArrayList<Map<String,Object>>(4);
			int myCircleNumber = 0, allCircleNumber = 0;
			
			if(CollectionUtil.isNotEmpty(circleBeans)){
				allCircleNumber = circleBeans.size();
				int count = 0;
				for(CircleBean circleBean: circleBeans){
					Map<String, Object> cc = new HashMap<String, Object>();
					cc.put("id", circleBean.getId());
					cc.put("name", circleBean.getName());
					if(StringUtil.isNull(circleBean.getCirclePath())){
						cc.put("path", ConstantsUtil.DEFAULT_NO_PIC_PATH);
					}else{
						cc.put("path", circleBean.getCirclePath());
					}
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
			
			//获取该用户的帖子列表
			CircleUserPostsBean circleUserPosts = circlePostHandler.getUserCirclePosts(user.getId());
			message.put("circleUserPosts", circleUserPosts);
		}
		
		try {
			message.put("hotests", circleHandler.getHostest().getCircleBeans());//获取热门的圈子
			message.put("newests", circleHandler.getNestest().getCircleBeans()); //获取最新的圈子
			message.put("recommends", circleHandler.getRecommend().getCircleBeans()); //获取推荐的圈子
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
				
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr((user != null ? user.getAccount(): "用户还未登录"), "获取自己所有圈子的初始化数据"), "init()", ConstantsUtil.STATUS_NORMAL, 0);
				
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> main(CircleBean circle, UserBean user, HttpServletRequest request) {
		logger.info("CircleServiceImpl-->main(), user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		int circleId = circle.getId();
		if(user.getId() == circle.getCreateUserId()){
			message.put("isCreater", true);
			message.put("inMember", true);
    	}else{
    		message.put("isCreater", false);
    		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
    		message.put("inMember", SqlUtil.getBooleanByList(members));
    		message.put("isCircleAdmin", SqlUtil.getBooleanByList(members) &&  members.get(0).getRoleType() == CIRCLE_MANAGER);
    	}
		
		//获取我的贡献值以及总贡献值
		List<Map<String, Object>> contributes = circleContributionMapper.getContribute(circleId, user.getId());
		if(CollectionUtil.isNotEmpty(contributes)){
			message.putAll(contributes.get(0));
		}
		
		if(!message.containsKey("myContribute") || StringUtil.changeObjectToInt(message.get("myContribute")) < 1){
			message.put("myContribute", 0);
		}
	
		if(!message.containsKey("allContribute") || StringUtil.changeObjectToInt(message.get("allContribute")) < 1){
			message.put("allContribute", 0);
		}
		
		//判断当天是否有签到
		message.put("isClockIn", CollectionUtil.isNotEmpty(circleClockInMapper.getClockInBean(circleId, user.getId(), ConstantsUtil.STATUS_NORMAL, DateUtil.DateToString(new Date(), "yyyy-MM-dd"))));
		
		//获取管理员
		message.put("admins", circleMemberMapper.getMembersByRoleType(circleId, CIRCLE_MANAGER, ConstantsUtil.STATUS_NORMAL));
		message.put("circle", circle);
		message.put("createTime", RelativeDateFormat.format(circle.getCreateTime()));
		message.put("createName", userHandler.getUserName(circle.getCreateUserId()));
		message.put("createId", circle.getCreateUserId());
		
		message.put("memberNumber", SqlUtil.getTotalByList(circleMemberMapper.getTotal(DataTableType.圈子成员.value, " where circle_id= "+ circle.getId())));
		try {
			message.put("hotestMembers", getCircleMembers(circleMemberHandler.getHostest(circleId).getCircleMemberBeans()));//获取圈子热门的成员
			message.put("newestMembers", getCircleMembers(circleMemberHandler.getNestest(circleId).getCircleMemberBeans())); //获取圈子最新的成员
			message.put("recommendMembers", getCircleMembers(circleMemberHandler.getRecommend(circleId).getCircleMemberBeans())); //获取圈子推荐的成员
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		//获取该圈子该用户的帖子列表
		CircleUserPostsBean circleUserPosts = circlePostHandler.getUserPostPosts(circleId, user.getId());
		message.put("circleUserPosts", circleUserPosts);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> memberListInit(CircleBean circle, UserBean user, HttpServletRequest request) {
		logger.info("CircleServiceImpl-->memberListInit(), user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		
		int circleId = circle.getId();
		if(user.getId() == circle.getCreateUserId()){
			message.put("canAdmin", true);
    	}else{
    		List<CircleMemberBean> members = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
    		message.put("canAdmin", SqlUtil.getBooleanByList(members) &&  members.get(0).getRoleType() == CIRCLE_MANAGER);
    	}
		return message.getMap();
	}

	
	/**
	 * 获取数据
	 * @param members
	 * @return
	 */
	private List<Map<String, Object>> getCircleMembers(List<Map<String, Object>> members){
	
		if(CollectionUtil.isNotEmpty(members)){
			for(Map<String, Object> member: members){
				int toUserId = StringUtil.changeObjectToInt(member.get("member_id"));
				member.putAll(userHandler.getBaseUserInfo(toUserId));
			}
		}
		return members;
	}
	
	@Override
	public Map<String, Object> check(JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->check(), user=" +user.getAccount());
		ResponseMap message = new ResponseMap();
		int number = circleCreateLimitMapper.getNumber(user.getId(), ConstantsUtil.STATUS_NORMAL);
		if(number < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.已超出您能创建的圈子数量.value));
			message.put("responseCode", EnumUtil.ResponseCode.已超出您能创建的圈子数量.value);
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
		circleBean.setCirclePath(ConstantsUtil.DEFAULT_NO_PIC_PATH);
		
		boolean result = circleMapper.save(circleBean) > 0;
		if(result){
			//添加一条成员记录
			CircleMemberBean member = new CircleMemberBean();
			member.setCreateTime(createTime);
			member.setCreateUserId(user.getId());
			member.setCircleId(circleBean.getId());
			member.setMemberId(user.getId());
			member.setRoleType(CIRCLE_CREATER);
			member.setStatus(ConstantsUtil.STATUS_NORMAL);
			
			result = circleMemberMapper.save(member) > 0;
			
			//保存基本的设置
			CircleSettingBean setting = new CircleSettingBean();
			setting.setCreateUserId(user.getId());
			setting.setCreateTime(createTime);
			setting.setCircleId(circleBean.getId());
			setting.setStatus(ConstantsUtil.STATUS_NORMAL);
			setting.setAddMember(true);
			result = circleSettingMapper.save(setting) > 0;
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
		
		int circleId = JsonUtil.getIntValue(jo, "cid", 0);
		if(circleId < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
			return message.getMap();
		}
		CircleBean circleBean = circleHandler.getCircleBean(circleId);
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该圈子不存在.value));
		
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
			//清除该圈子的缓存
			circleHandler.deleteCircleBeanCache(circleBean.getId());
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
		
		CircleBean circleBean = circleHandler.getCircleBean(cid);
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		//判断是否是圈主
		if(circleBean.getCreateUserId() != user.getId()/* && circleHandler.getRoleCode(user, cid) != CIRCLE_MANAGER*/)
			throw new UnauthorizedException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作权限.value));

		//这里是逻辑删除
		circleBean.setStatus(ConstantsUtil.STATUS_DELETE);
		boolean result = circleMapper.update(circleBean) > 0;
		if(result){
			//清除该圈子的缓存
			circleHandler.deleteCircleBeanCache(circleBean.getId());
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
		}
		message.put("total", circleMapper.getAllCircles(user.getId(), ConstantsUtil.STATUS_NORMAL).size());
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取圈子列表").toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> joinCheck(JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->joinCheck():jo="+json.toString());
		ResponseMap message = new ResponseMap();
		int cid = JsonUtil.getIntValue(json, "cid", 0);
		if(cid < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
			return message.getMap();
		}
		
		List<CircleSettingBean> circleSettingBeans = circleSettingMapper.getSetting(cid, ConstantsUtil.STATUS_NORMAL);
		if(CollectionUtil.isEmpty(circleSettingBeans))
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		CircleSettingBean circleSettingBean = circleSettingBeans.get(0);
		
		if(SqlUtil.getBooleanByList(circleMemberMapper.getMember(user.getId(), cid, ConstantsUtil.STATUS_NORMAL))){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.您已经在圈子中.value));
			message.put("responseCode", EnumUtil.ResponseCode.您已经在圈子中.value);
			return message.getMap();
		}
		
		//获取是否需要回答问题
		if(StringUtil.isNotNull(circleSettingBean.getQuestionTitle()) && StringUtil.isNotNull(circleSettingBean.getQuestionAnswer())){
			message.put("message", true);
			message.put("question", circleSettingBean.getQuestionTitle());
		}else{
			message.put("message", false);
		}
		
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	
	@Override
	public Map<String, Object> join(JSONObject json, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->join():jo="+json.toString());
		ResponseMap message = new ResponseMap();
		int cid = JsonUtil.getIntValue(json, "cid", 0);
		if(cid < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
			message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
			return message.getMap();
		}
		List<CircleSettingBean> circleSettingBeans = circleSettingMapper.getSetting(cid, ConstantsUtil.STATUS_NORMAL);
		if(CollectionUtil.isEmpty(circleSettingBeans))
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		CircleSettingBean circleSettingBean = circleSettingBeans.get(0);
		if(SqlUtil.getBooleanByList(circleMemberMapper.getMember(user.getId(), cid, ConstantsUtil.STATUS_NORMAL))){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.您已经在圈子中.value));
			message.put("responseCode", EnumUtil.ResponseCode.您已经在圈子中.value);
			return message.getMap();
		}
		
		//判断是否需要回到问题
		if(StringUtil.isNotNull(circleSettingBean.getQuestionTitle())){
			String answer = JsonUtil.getStringValue(json, "answer");
			if(StringUtil.isNull(answer)){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.缺少请求参数.value));
				message.put("responseCode", EnumUtil.ResponseCode.缺少请求参数.value);
				return message.getMap();
			}
			
			if(!answer.equals(circleSettingBean.getQuestionAnswer())){
				message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.回答的答案不正确.value));
				message.put("responseCode", EnumUtil.ResponseCode.回答的答案不正确.value);
				return message.getMap();
			}
		}
		CircleMemberBean member = new CircleMemberBean();
		member.setCreateTime(new Date());
		member.setCreateUserId(user.getId());
		member.setCircleId(cid);
		member.setMemberId(user.getId());
		member.setRoleType(CIRCLE_NORMAL);
		member.setStatus(ConstantsUtil.STATUS_NORMAL);
		circleMemberMapper.save(member);
		
		String msg = "恭喜您加入圈子";
		//判断是否有返回
		if(StringUtil.isNotNull(circleSettingBean.getWelcomeMember())){
			msg = circleSettingBean.getWelcomeMember();
		}
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"申请加入圈子--", cid).toString(), "join()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("isSuccess", true);
		message.put("message", msg);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		return message.getMap();
	}
	
	@Override
	public void saveVisitLog(int circleId, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->saveVisitLog() , circleId= "+ circleId +", --" + (user == null ? "" : user.getAccount()));
		visitorService.saveVisitor(user, "web网页端", DataTableType.圈子.value, circleId, ConstantsUtil.STATUS_NORMAL);
	}
	
	@Override
	public Map<String, Object> leave(int circleId, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->leave():circleId="+circleId);
		ResponseMap message = new ResponseMap();
		
		CircleBean circleBean = circleMapper.findById(CircleBean.class, circleId);
		
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));

		List<CircleMemberBean> circleMemberBeans = circleMemberMapper.getMember(user.getId(), circleId, ConstantsUtil.STATUS_NORMAL);
		if(!SqlUtil.getBooleanByList(circleMemberBeans)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.用户已经不在该圈子中.value));
			message.put("responseCode", EnumUtil.ResponseCode.用户已经不在该圈子中.value);
			return message.getMap();
		}
		
		
		boolean result = circleMemberMapper.deleteById(CircleMemberBean.class, (circleMemberBeans.get(0)).getId()) > 0;
		if(result){
			//保存操作日志
			operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"离开圈子--", circleId).toString(), "leave()", ConstantsUtil.STATUS_NORMAL, 0);		
			message.put("isSuccess", true);
			message.put("message", "退出圈子成功");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> admins(int cid, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->admins():cid="+cid);
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> rs = circleMemberMapper.getAllMembers(cid, ConstantsUtil.STATUS_NORMAL);
				
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取圈子id为", cid, "的管理员列表").toString(), "roles()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("message", rs);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> allot(int circleId, String admins, UserBean user,
			HttpServletRequest request) {
		logger.info("CircleServiceImpl-->allot():circleId="+circleId +", admins="+ admins);
		
		ResponseMap message = new ResponseMap();
		
		CircleBean circleBean = circleMapper.findById(CircleBean.class, circleId);
		
		if(circleBean == null)
			throw new RE404Exception(EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有操作实例.value));
		
		Set<Integer> deleteAdmins = new HashSet<Integer>();
		Set<Integer> addAdmins = new HashSet<Integer>();
		
		//先请空该圈子的管理员列表
		List<Map<String, Object>> adminMember = circleMemberMapper.getMembersByRoleType(circleId, CIRCLE_MANAGER, ConstantsUtil.STATUS_NORMAL);
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> admin: adminMember){
			circleMemberMapper.updateSql(CircleMemberBean.class, " set role_type=? where id = ?", CIRCLE_NORMAL, admin.get("id"));
			deleteAdmins.add(StringUtil.changeObjectToInt(admin.get("member_id")));
		}
		
		//根据权限id去删除所有相关的角色
		String[] adminArray = admins.split(",");
		int[] adminIds = new int[adminArray.length];
		for(int i = 0; i < adminArray.length; i++){
			adminIds[i] = StringUtil.changeObjectToInt(adminArray[i]);
		}
		
		List<CircleMemberBean> addMembers = circleMemberMapper.findByMemberIds(adminIds, circleId, ConstantsUtil.STATUS_NORMAL);
		for(CircleMemberBean circleMember: addMembers){
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", circleMember.getId());
			data.add(map);
			addAdmins.add(circleMember.getMemberId());
		}
		deleteAdmins.removeAll(addAdmins);
		addAdmins.removeAll(deleteAdmins);
		
		if(CollectionUtil.isNotEmpty(addMembers))
			circleMemberMapper.updateByBatch(data, CIRCLE_MANAGER, circleId);
		
		//发送删除管理员的通知
		notificationHandler.sendNotificationByIds(false, user, deleteAdmins, "已经被"+user.getAccount()+"移除圈子《"+ circleBean.getName()+ "》的管理员权限", NotificationType.通知, "t_circle", circleBean.getId(), null);
		//发送授权管理员的通知
		notificationHandler.sendNotificationByIds(false, user, addAdmins, "已经被"+user.getAccount()+"授予圈子《"+ circleBean.getName()+ "》的管理员权限", NotificationType.通知, "t_circle", circleBean.getId(), null);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"给圈子ID为"+ circleId +",分配管理员权限，成员为ids"+admins).toString(), "allot()", ConstantsUtil.STATUS_NORMAL, 0);		
		message.put("message", "操作成功");
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		message.put("isSuccess", true);
		
		return message.getMap();
	}
}
