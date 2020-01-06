package com.cn.leedane.service.impl.manage;

import com.cn.leedane.echarts.graph.*;
import com.cn.leedane.handler.UserHandler;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.mapper.mall.PromotionSeatApplyMapper;
import com.cn.leedane.mapper.mall.ReferrerMapper;
import com.cn.leedane.mapper.mall.ReferrerRecordMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.IDBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.ReferrerRelationBean;
import com.cn.leedane.model.mall.S_PromotionSeatApplyBean;
import com.cn.leedane.model.mall.S_ReferrerBean;
import com.cn.leedane.model.mall.S_ReferrerRecordBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.manage.ManageMallService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 我的商城设置的service的实现类
 * @author LeeDane
 * 2017年12月6日 下午7:52:25
 * version 1.0
 */
@Service("manageMallService")
public class ManageMallServiceImpl implements ManageMallService<IDBean> {
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Autowired
	private PromotionSeatApplyMapper promotionSeatApplyMapper;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private UserHandler userHandler;

	@Autowired
	private ReferrerMapper referrerMapper;

	@Autowired
	private ReferrerRecordMapper referrerRecordMapper;
	@Override
	public ResponseModel promotionApply(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMallServiceImpl-->promotionApply():jo="+jo);
		String platform = JsonUtil.getStringValue(jo, "platform");
		ParameterUnspecificationUtil.checkNullString(platform, "platform must not null.");

		S_PromotionSeatApplyBean promotionSeatApplyBean = new S_PromotionSeatApplyBean();
		promotionSeatApplyBean.setPlatform(platform);
		promotionSeatApplyBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		promotionSeatApplyBean.setCreateUserId(user.getId());
		promotionSeatApplyBean.setCreateTime(new Date());
		promotionSeatApplyBean.setModifyUserId(user.getId());
		promotionSeatApplyBean.setModifyTime(new Date());
		boolean success = promotionSeatApplyMapper.save(promotionSeatApplyBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"申请", platform, "推广位， 结果是：", StringUtil.getSuccessOrNoStr(success)).toString(), "promotionApply()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success)
			return new ResponseModel().ok().message("申请成功，请稍等管理员审核！");

		return new ResponseModel().error().message("申请失败，请稍后重试。");
	}

	@Override
	public ResponseModel buildReferrerCode(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMallServiceImpl-->buildReferrerCode():jo="+jo);
		//先查找是否存在推荐记录
		S_ReferrerRecordBean recordBean = referrerRecordMapper.findReferrer(user.getId());
		if(recordBean == null){
			return new ResponseModel().error().message("请先绑定推荐人");
		}

		//先查找是否有推荐码记录
		S_ReferrerBean referrerBean = referrerMapper.findReferrerCode(user.getId());
		if(referrerBean != null){
			return new ResponseModel().error().message("您已经有推荐码："+ referrerBean.getCode()).code(EnumUtil.ResponseCode.您已经有推荐码.value);
		}

		String code = ShareCodeUtil.toSerialCode(user.getId());
		if(StringUtil.isNull(code))
			return new ResponseModel().error().message("系统无法生成推荐码，请稍后重试！");
		referrerBean = new S_ReferrerBean();
		referrerBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		referrerBean.setCreateUserId(user.getId());
		referrerBean.setCreateTime(new Date());
		referrerBean.setModifyUserId(user.getId());
		referrerBean.setModifyTime(new Date());
		referrerBean.setCode(code);
		boolean success = referrerMapper.save(referrerBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"生成商城推广码：", code, "， 结果是：", StringUtil.getSuccessOrNoStr(success)).toString(), "buildReferrerCode()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success)
			return new ResponseModel().ok().message("您的推荐码已经生成！");

		return new ResponseModel().error().message("推荐码保存生成失败，请稍后重试！").code(EnumUtil.ResponseCode.数据库保存失败.value);
	}

	@Override
	public ResponseModel bindReferrer(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMallServiceImpl-->bindReferrer():jo="+jo);

		//先查找是否存在推荐记录
		S_ReferrerRecordBean recordBean = referrerRecordMapper.findReferrer(user.getId());
		if(recordBean != null){
			return new ResponseModel().error().message("您已经绑定过推荐人:"+ userHandler.getUserName(recordBean.getUserId())).code(EnumUtil.ResponseCode.已经绑定过推荐人.value);
		}
		String code = JsonUtil.getStringValue(jo, "code");
		UserBean toUser = null;
		if(StringUtil.isNull(code)){
			toUser = OptionUtil.adminUser;//没有code绑定管理员
		}else{
			S_ReferrerBean referrerBean = referrerMapper.findReferrerByCode(code);
			if(referrerBean == null){
				return new ResponseModel().error().message("推荐码无效！");
			}

			toUser = userMapper.findById(UserBean.class, referrerBean.getCreateUserId());
			if(toUser == null){
				return new ResponseModel().error().message("该推荐人不存在！");
			}
		}

		if(toUser.getId() == user.getId()){
			return new ResponseModel().error().message("推荐人不能是您自己");
		}
		recordBean = new S_ReferrerRecordBean();
		recordBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		recordBean.setCreateUserId(user.getId());
		recordBean.setCreateTime(new Date());
		recordBean.setModifyUserId(user.getId());
		recordBean.setModifyTime(new Date());
		recordBean.setCode(code);
		recordBean.setUserId(toUser.getId());
		boolean success = referrerRecordMapper.save(recordBean) > 0;
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"绑定推荐人:"+ toUser.getAccount() +"，推荐码是：", code, "， 结果是：", StringUtil.getSuccessOrNoStr(success)).toString(), "bindReferrer()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		if(success)
			return new ResponseModel().ok().message("您已经成功绑定推荐人："+ toUser.getAccount());

		return new ResponseModel().error().message("绑定推荐人失败，请稍后重试！").code(EnumUtil.ResponseCode.数据库保存失败.value);
	}

	@Override
	public ResponseModel referrerRelation(JSONObject jo, UserBean user, HttpRequestInfoBean request){
		logger.info("ManageMallServiceImpl-->referrerRelation():jo="+jo);

		//先查找是否有推荐码记录
		S_ReferrerBean referrerBean = referrerMapper.findReferrerCode(user.getId());
		if(referrerBean == null){
			return new ResponseModel().error().message("请先生成推荐码！");
		}
		List<ReferrerRelationBean> relations =  referrerRecordMapper.getReferrerRelation(user.getId(), 2);
		//根据规则，第一个是自己，第二个是上级，第三个是上上级
		ReferrerRelation referrerRelation = new ReferrerRelation();
		if(CollectionUtil.isNotEmpty(relations)){
			ReferrerRelationBean mySelf  = relations.get(0);
			referrerRelation.setNodes(buildNodes(mySelf, relations));
			referrerRelation.setLinks(buildLinks(referrerRelation.getNodes(), relations));
			ResponseModel responseModel = new ResponseModel();
			return responseModel.ok().message(referrerRelation);
		}

		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取推荐关系， 结果是：", StringUtil.getSuccessOrNoStr(success)).toString(), "referrerRelation()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return new ResponseModel().error().message("绑定推荐人失败，请稍后重试！").code(EnumUtil.ResponseCode.数据库保存失败.value);
	}

	/**
	 * 构建他们之间的关系
	 * @param nodes
	 * @return
	 */
	private List<Link> buildLinks(List<Node> nodes, List<ReferrerRelationBean> relations) {
		//将List转化成Map<String, String> key=id, value=parent
		Map<String, String> map = new HashMap<>();
		for(int i = 0; i < nodes.size(); i++){
			map.put(nodes.get(i).getSourceId()+"", nodes.get(i).getId()+"");
		}

		List<Link> links = new ArrayList<>();
		for(int i = 0; i < nodes.size(); i++){
			Link link = new Link();
			link.setId(i + 1);
			link.setLineStyle(getLineStyle());
			link.setName(null);
			link.setSource(nodes.get(i).getId() + "");
			link.setTarget(map.get(nodes.get(i).getParentId()+""));
			links.add(link);
		}

		return links;
	}

	private LineStyle getLineStyle() {
		LineStyle lineStyle = new LineStyle();
		lineStyle.setColor(null);
		lineStyle.setWidth(2);
		return lineStyle;
	}

	private List<Node> buildNodes(ReferrerRelationBean mySelf, List<ReferrerRelationBean> relations) {
		List<Node> nodes = new ArrayList<>();
		for(int i = 0; i < relations.size(); i++) {
			ReferrerRelationBean relation = relations.get(i);
			String name = relation.getAccount();
			Node node = new Node();
			node.setCategory(getCategory(mySelf, relation, relations)); //设置级别
			node.setId(i);
			node.setDraggable(true);
			node.setItemStyle(getItemStyle(node.getCategory()));
			node.setName(getNodeName(node.getCategory(), name));
			node.setSymbolSize(getSymbolSize(node.getCategory()));
			node.setValue(0);
			node.setSourceId(relation.getId());
			node.setParentId(relation.getParent());
			nodes.add(node);
		}
		return nodes;
	}

	/**
	 * 获取名称
	 * @param category
	 * @param name
	 * @return
	 */
	private String getNodeName(int category, String name) {
		String str = "";
		switch (category){
			case 0:
				str = "上上级";
				break;
			case 1:
				str = "上级";
				break;
			case 2:
				str = "自己";
				break;
			case 3:
				str = "下级";
				break;
			case 4:
				str = "下下级";
				break;
		}
		return str + "-" + name;
	}

	/**
	 * 获取大小
	 * @param category
	 * @return
	 */
	private int getSymbolSize(int category) {
		int size = 0;
		switch (category){
			case 0:
				size = 18;
				break;
			case 1:
				size = 14;
				break;
			case 2:
				size = 25;
				break;
			case 3:
				size = 12;
				break;
			case 4:
				size = 10;
				break;
		}
		return size;
	}

	/**
	 * 获取ItemStyle
	 * @param category
	 * @return
	 */
	private ItemStyle getItemStyle(int category) {
		ItemStyle itemStyle = new ItemStyle();
		Normal normal = new Normal();
		switch (category){
			case 0:
				normal.setColor("rgb(0,134,139)");
				break;
			case 1:
				normal.setColor("rgb(123,104,238)");
				break;
			case 2:
				normal.setColor("rgb(255,0,0)");
				break;
			case 3:
				normal.setColor("rgb(205,92,92)");
				break;
			case 4:
				normal.setColor("rgb(255,0,255)");
				break;
		}

		itemStyle.setNormal(normal);
		return itemStyle;
	}

	/**
	 * 计算所属的分类
	 * @param mySelf
	 * @param current
	 * @return
	 */
	private int getCategory(ReferrerRelationBean mySelf, ReferrerRelationBean current, List<ReferrerRelationBean> relations) {
		if(mySelf.getId() == current.getId())
			return 2;
		//将List转化成Map<String, String> key=id, value=parent
		Map<String, String> map = new HashMap<>();
		for(int i = 0; i < relations.size(); i++){
			map.put(relations.get(i).getId()+"", relations.get(i).getParent()+"");
		}

		//以登录用户向上走
		String parent1Id = map.get(mySelf.getParent()+""); //获取上级ID
		if(mySelf.getParent() == current.getId())
			return 1;

		//String parent2 = map.get(parent1Id+""); //获取上上级ID
		if(StringUtil.changeObjectToLong(map.get(mySelf.getParent()+"")) == current.getId())
			return 0;

		//以当前对象向上
		if(current.getParent() == mySelf.getId())
			return 3;
		return 4;
	}
}
