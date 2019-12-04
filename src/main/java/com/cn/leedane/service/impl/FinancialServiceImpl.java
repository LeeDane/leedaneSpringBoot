package com.cn.leedane.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.cn.leedane.mapper.FinancialMapper;
import com.cn.leedane.model.FinancialBean;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.AdminRoleCheckService;
import com.cn.leedane.service.FinancialService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
/**
 * 记账相关service的实现类
 * @author LeeDane
 * 2016年7月22日 上午9:20:42
 * Version 1.0
 */
@Service("financialService")
public class FinancialServiceImpl extends AdminRoleCheckService implements FinancialService<FinancialBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private FinancialMapper financialMapper;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;

	@Override
	public Map<String, Object> save(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FinancialServiceImpl-->save():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		FinancialBean financialBean = getValue(jo, "data", user);
		
		ResponseMap message = new ResponseMap();
		if(financialBean == null){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有要同步的数据.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有要同步的数据.value);
			message.put("isSuccess", true);
			return message.getMap();
		}
		
		boolean result = false;
		if(financialBean.getId() > 0){//说明是更新
			result = financialMapper.update(financialBean) > 0;
		}else{
			String imei = JsonUtil.getStringValue(jo, "imei");
			if(checkExists(imei, financialBean.getLocalId(), financialBean.getAdditionTime())){
				message.put("message", "您已添加过该记账记录，请勿重复操作！");
				message.put("responseCode", EnumUtil.ResponseCode.添加的记录已经存在.value);
				message.put("isSuccess", true);
				return message.getMap();
			}
			result = financialMapper.save(financialBean) > 0;
		}
		
		if(result){
			Map<String, Long> r = new HashMap<String, Long>();
			r.put("local_id", financialBean.getLocalId());
			r.put("id", financialBean.getId());
			message.put("isSuccess", true);
			message.put("message", r);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"新添一条记账,ID为：", financialBean.getId(), StringUtil.getSuccessOrNoStr(result)).toString(), "save()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	/**
	 * 判断记录是否已经存在
	 * @return
	 */
	private boolean checkExists(String imei, long localId, String additionTime){
		
		//暂时对没有imei或者additionTime 不做处理
		if(StringUtil.isNull(imei) || StringUtil.isNull(additionTime)){
			return false;
		}
		
		List<Map<String, Object>> financialBeans = financialMapper.executeSQL("select add_day from  t_financial where imei = ? and local_id =? limit 1", imei, localId);
		if(!CollectionUtils.isEmpty(financialBeans)){
			if(additionTime.substring(0, 10).equals(String.valueOf(financialBeans.get(0).get("add_day")))){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Map<String, Object> update(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FinancialServiceImpl-->update():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		FinancialBean financialBean = getValue(jo, "data", user);
		
		ResponseMap message = new ResponseMap();
		if(financialBean == null || financialBean.getId() < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有要同步的数据.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有要同步的数据.value);
			message.put("isSuccess", true);
			return message.getMap();
		}
		
		boolean result = financialMapper.update(financialBean) > 0;
		if(result){
			message.put("isSuccess", true);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据更新失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据更新失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"更新一条记账，ID为", financialBean.getId(), StringUtil.getSuccessOrNoStr(result)).toString(), "update()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> delete(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FinancialServiceImpl-->delete():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		long fid = JsonUtil.getLongValue(jo, "fid");
		
		ResponseMap message = new ResponseMap();
		if(fid < 1){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.参数不存在或为空.value));
			message.put("responseCode", EnumUtil.ResponseCode.参数不存在或为空.value);
			return message.getMap();
		}
		
		FinancialBean bean = financialMapper.findById(FinancialBean.class, fid);
		
		checkAdmin(user, bean.getCreateUserId());
		
		boolean result = financialMapper.deleteById(FinancialBean.class, fid) > 0;
		if(result){
			message.put("isSuccess", true);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除一条记账,Id为：", fid, StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> synchronous(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FinancialServiceImpl-->synchronous():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		List<FinancialBean> appFinancialBeans = getListValue(jo, "datas");
		
		ResponseMap message = new ResponseMap();
		if(CollectionUtils.isEmpty(appFinancialBeans)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有要同步的数据.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有要同步的数据.value);
			message.put("isSuccess", true);
			return message.getMap();
		}
		
		//设置imei值
		String imei = JsonUtil.getStringValue(jo, "imei");
		for(FinancialBean fBean: appFinancialBeans){
			fBean.setImei(imei);
		}
		
		List<Map<String, Long>> inserts = new ArrayList<Map<String,Long>>();//此次同步插入的数据ID
		List<Map<String, Long>> updates = new ArrayList<Map<String,Long>>(); //此次同步发现数据有更新的数据ID
		List<Map<String, Long>> deletes = new ArrayList<Map<String,Long>>();//此次同步发现数据被删的数据ID
		int total = 0; //此次同步总共处理的数量

		long fId = 0;
		FinancialBean webBean;
		for(FinancialBean appFinancialBean: appFinancialBeans){
			fId = appFinancialBean.getId();
			//ID大于0说明是服务器上的数据
			if(fId > 0){
				webBean = financialMapper.findById(FinancialBean.class, fId);
				if(webBean == null){
					//deletes.add(fId);
					Map<String, Long> map = new HashMap<String, Long>();
					map.put("localId", appFinancialBean.getLocalId());
					map.put("id", appFinancialBean.getId());
					deletes.add(map);
				}else{
					if(hasChange(appFinancialBean, webBean)){
						Map<String, Long> map = new HashMap<String, Long>();
						map.put("localId", appFinancialBean.getLocalId());
						map.put("id", appFinancialBean.getId());
						if(financialMapper.update(appFinancialBean) > 0){
							updates.add(map);
						}
					}
				}
			}else{
				FinancialBean financialBean = (FinancialBean)appFinancialBean;
				financialBean.setCreateTime(new Date());
				financialBean.setCreateUserId(1);
				
				//校验记录是否存在
				if(!checkExists(imei, financialBean.getLocalId(), financialBean.getAdditionTime())){
					if(financialMapper.save(financialBean) > 0){
						Map<String, Long> map = new HashMap<String, Long>();
						map.put("localId", appFinancialBean.getLocalId());
						map.put("id", financialBean.getId());
						inserts.add(map);
						total = total + 1;
					}
				}
				
			}
		}
		
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("inserts", inserts);
		jsonObject.put("updates", updates);
		jsonObject.put("deletes", deletes);
		jsonObject.put("total", total);
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"同步记账数据，").toString(), "synchronous()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		//message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据同步成功.value));
		message.put("message", jsonObject);
		message.put("isSuccess", true);
		return message.getMap();
	}

	@Override
	public Map<String, Object> force(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FinancialServiceImpl-->force():jsonObject=" +jo.toString() +", user=" +user.getAccount());
		
		int type = JsonUtil.getIntValue(jo, "type"); //1表示强制以客户端数据为主，2表示强制以服务器端数据为主
		List<FinancialBean> financialBeans = getListValue(jo, "datas");
		ResponseMap message = new ResponseMap();
		if(type == 0 || CollectionUtils.isEmpty(financialBeans)){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.没有要同步的数据.value));
			message.put("responseCode", EnumUtil.ResponseCode.没有要同步的数据.value);
			message.put("isSuccess", true);
			return message.getMap();
		}

		long fId = 0;
		List<FinancialBean> returnBeans = new ArrayList<FinancialBean>();
		FinancialBean newFinancialBean = null;
		FinancialBean webBean;
		for(FinancialBean FinancialBean: financialBeans){
			fId = FinancialBean.getId();
			//ID大于0说明是服务器上的数据
			if(fId > 0){
				if(type == 1){
					webBean = new FinancialBean();
					webBean = FinancialBean;
					webBean.setModifyTime(new Date());
					//以客户端为主
					if(financialMapper.update(webBean)>0){
						newFinancialBean = FinancialBean;
					}
				}else{ //以服务器的为主
					
					webBean = financialMapper.findById(FinancialBean.class, fId);
					newFinancialBean = (FinancialBean) webBean;
					newFinancialBean.setLocalId(FinancialBean.getLocalId());
				}
				returnBeans.add(newFinancialBean);
			}
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"强制同步记录").toString(), "force()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		message.put("message", returnBeans);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回[]
	 * @param object json对象
	 * @param key  键名称
	 * @return
	 */
	private static List<FinancialBean> getListValue(JSONObject object, String key){				
		if(JsonUtil.hasKey(object, key)){
			String s = String.valueOf(object.get(key));
			if(StringUtil.isNotNull(s)){
				return JSONArray.parseArray(StringUtil.parseJSONToString(s), FinancialBean.class);
			}
			return new ArrayList<FinancialBean>();
		}
		return new ArrayList<FinancialBean>();
	}
	/*public static Object jsonToObject(String json, Class cls)
			throws JsonGenerationException, JsonMappingException, IOException {
			Object obj = null;
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
			obj = mapper.readValue(json, cls);
			return obj;
			}*/
	
	/**
	 * 从json对象中获取key对应的值，没有该key返回[]
	 * @param object json对象
	 * @param key  键名称
	 * @return
	 */
	private FinancialBean getValue(JSONObject object, String key, UserBean user){				
		if(JsonUtil.hasKey(object, key)){
			String s = String.valueOf(object.get(key));
			if(StringUtil.isNotNull(s)){
				FinancialBean financialBean = JSON.parseObject(StringUtil.parseJSONToString(s), FinancialBean.class);
				if(financialBean != null){
					financialBean.setCreateUserId(user.getId());
					financialBean.setCreateTime(new Date());
					financialBean.setModifyUserId(user.getId());
					financialBean.setModifyTime(new Date());
					if(!JsonUtil.hasKey(object, "imei") && financialBean.getLocalId() < 1){
						financialBean.setImei(FinancialWebImeiUtil.DEFAULT_IMEI);
						financialBean.setLocalId(FinancialWebImeiUtil.getInstance().getLocalId());
						FinancialWebImeiUtil.getInstance().addLocalId();
					}else
						financialBean.setImei(JsonUtil.getStringValue(object, "imei"));
				}
				return financialBean;
			}
			return null;
		}
		return null;
	}
	
	/**
	 * 判断数据是否改变
	 * @param app
	 * @param web
	 * @return
	 */
	private boolean hasChange(FinancialBean app, FinancialBean web){
		/*boolean result = true;
		if(app.getId() == web.getId()
				&& app.getModel() == web.getModel()
				&& app.isHasImg() == web.isHasImg()
				&& app.getCreateUserId() == web.getCreateUserId()
				&& app.getFinancialDesc().equals(web.getFinancialDesc())
				&& app.getLocation().equals(web.getLocation())
				&& app.getMoney() == web.getMoney()
				&& app.getCreateUserId() == web.getCreateUserId()
				&& app.getStatus() == web.getStatus()
				&& app.getOneLevel() == web.getOneLevel()
				&& app.getTwoLevel() == web.getTwoLevel()){
			result = false;
		}
		
		return result;*/
		return !app.equals(web);
	}

	@Override
	public Map<String, Object> getByYear(JSONObject jsonObject,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("FinancialServiceImpl-->getByYear():jsonObject=" +jsonObject.toString() +", user=" +user.getAccount());
		
		int year = JsonUtil.getIntValue(jsonObject, "year"); //年份
		ResponseMap message = new ResponseMap();
		if(year < 1990){
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.系统不支持查找该年份的数据.value));
			message.put("responseCode", EnumUtil.ResponseCode.系统不支持查找该年份的数据.value);
			return message.getMap();
		}
		
		List<Map<String, Object>> list = financialMapper.getLimit(year, ConstantsUtil.STATUS_NORMAL, user.getId());
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取", year, "年的记账数据").toString(), "getByYear()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		message.put("message", list);
		message.put("isSuccess", true);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getAll(JSONObject jsonObject,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("FinancialServiceImpl-->getAll():jsonObject=" +jsonObject.toString() +", user=" +user.getAccount());
		
		ResponseMap message = new ResponseMap();
		List<Map<String, Object>> list = financialMapper.getAll(ConstantsUtil.STATUS_NORMAL, user.getId());
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取全部的记账数据").toString(), "getAll()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		message.put("message", list);
		message.put("isSuccess", true);
		return message.getMap();
	}

	@Override
	public List<FinancialBean> getByTimeRange(long createUserId, int status,
			Date startTime, Date endTime) {
		return this.financialMapper.getByTimeRange(createUserId, status, startTime, endTime);
	}

	@Override
	public Map<String, Object> query(JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FinancialServiceImpl-->query():jsonObject=" +json.toString() +", user=" +user.getAccount());
		
		String key = JsonUtil.getStringValue(json, "key");
		String start = JsonUtil.getStringValue(json, "start");
		String end = JsonUtil.getStringValue(json, "end");
		String level = JsonUtil.getStringValue(json, "levels");
		ResponseMap message = new ResponseMap();
		StringBuffer sqlBuffer = new StringBuffer();
		sqlBuffer.append("select local_id, id, status, model, money, one_level, two_level, has_img, path");
		sqlBuffer.append("	, location, longitude, latitude, financial_desc, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, date_format(addition_time,'%Y-%m-%d %H:%i:%s') addition_time");
		sqlBuffer.append(" from t_financial ");
		sqlBuffer.append(" where create_user_id=? ");
		if(StringUtil.isNotNull(key))
			sqlBuffer.append(" and (one_level like '%"+ key +"%' or two_level like '%"+ key +"%' or location like '%"+ key +"%' or financial_desc like '%"+ key +"%')");
		if(StringUtil.isNotNull(start))
			sqlBuffer.append(" and addition_time >= date_format('" + start+ "', '%Y-%m-%d')");
		if(StringUtil.isNotNull(end))
			sqlBuffer.append(" and addition_time <= date_format('" + end+ "', '%Y-%m-%d')");
		
		if(StringUtil.isNotNull(level)){
			String[] levels = level.split(">>>");
			sqlBuffer.append(" and one_level = '" + levels[0] +"'");
			sqlBuffer.append(" and two_level = '" + levels[1] +"'");
		}
		if(StringUtil.isNull(key) && StringUtil.isNull(start) && StringUtil.isNull(end) && StringUtil.isNull(level)){
			sqlBuffer.append(" order by addition_time desc");
			sqlBuffer.append(" limit 15");
		}else {
			sqlBuffer.append(" and status = " + ConstantsUtil.STATUS_NORMAL);
			sqlBuffer.append(" order by addition_time desc");
		}
		List<Map<String, Object>> list = financialMapper.executeSQL(sqlBuffer.toString(), user.getId());
		
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"查询记账列表，查询条件：", json.toString()).toString(), "query()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		message.put("message", list);
		message.put("isSuccess", true);
		return message.getMap();
	}

	@Override
	public Map<String, Object> paging(JSONObject jo, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("FinancialServiceImpl-->paging():jo=" +jo.toString());
		long start = System.currentTimeMillis();
		List<Map<String, Object>> rs = new ArrayList<Map<String,Object>>();
		int pageSize = JsonUtil.getIntValue(jo, "pageSize", ConstantsUtil.DEFAULT_PAGE_SIZE); //每页的大小
		long lastId = JsonUtil.getLongValue(jo, "last_id"); //开始的页数
		String lastAdditionTime = JsonUtil.getStringValue(jo, "last_addition_time"); //开始的时间
		if(StringUtil.isNull(lastAdditionTime)){
			lastAdditionTime = DateUtil.DateToString(new Date(), "yyyy-MM-dd HH:mm") + ":00";
		}
		String method = JsonUtil.getStringValue(jo, "method", "firstloading"); //操作方式
				
		StringBuffer sql = new StringBuffer();
		ResponseMap message = new ResponseMap();
		
		if("firstloading".equalsIgnoreCase(method)){
			sql.append("select local_id, id, status, model, money, one_level, two_level, has_img, path");
			sql.append("	, location, longitude, latitude, financial_desc, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, date_format(addition_time,'%Y-%m-%d %H:%i:%s') addition_time");
			sql.append(" from t_financial ");
			sql.append(" where create_user_id = ?");
			sql.append(" order by addition_time desc, id desc limit 0,?");
			rs = financialMapper.executeSQL(sql.toString(), user.getId(), pageSize);
		//下刷新
		}else if("lowloading".equalsIgnoreCase(method)){
			
			//这个方法的缺欠之处在于多请求了一次。
			sql.append("select local_id, id, status, model, money, one_level, two_level, has_img, path");
			sql.append("	, location, longitude, latitude, financial_desc, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, date_format(addition_time,'%Y-%m-%d %H:%i:%s') addition_time");
			sql.append(" from t_financial ");
			sql.append(" where create_user_id = ?");
			sql.append(" and id < ? and addition_time = str_to_date(?,'%Y-%m-%d %H:%i:%s') order by id desc");
			rs = financialMapper.executeSQL(sql.toString(), user.getId(), lastId,  lastAdditionTime);
			
			if(rs.size() < pageSize){
				sql = new StringBuffer();
				sql.append("select local_id, id, status, model, money, one_level, two_level, has_img, path");
				sql.append("	, location, longitude, latitude, financial_desc, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, date_format(addition_time,'%Y-%m-%d %H:%i:%s') addition_time");
				sql.append(" from t_financial ");
				sql.append(" where create_user_id = ?");
				sql.append(" and addition_time < str_to_date(?,'%Y-%m-%d %H:%i:%s')  order by addition_time desc, id desc limit 0,? ");
				rs.addAll(financialMapper.executeSQL(sql.toString(), user.getId(), lastAdditionTime, pageSize));
			}
			
			
			//下面方法的缺陷就是获取的记录的条数比请求的页数大
			/*sql.append("(select local_id, id, status, model, money, one_level, two_level, has_img, path");
			sql.append("	, location, longitude, latitude, financial_desc, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, date_format(addition_time,'%Y-%m-%d %H:%i:%s') addition_time");
			sql.append(" from t_financial ");
			sql.append(" where create_user_id = ?");
			sql.append(" and id < ? and addition_time = str_to_date(?,'%Y-%m-%d %H:%i:%s'))");
			sql.append(" union all ");
			sql.append("(select local_id, id, status, model, money, one_level, two_level, has_img, path");
			sql.append("	, location, longitude, latitude, financial_desc, create_user_id, date_format(create_time,'%Y-%m-%d %H:%i:%s') create_time, date_format(addition_time,'%Y-%m-%d %H:%i:%s') addition_time");
			sql.append(" from t_financial ");
			sql.append(" where create_user_id = ?");
			sql.append(" and addition_time < str_to_date(?,'%Y-%m-%d %H:%i:%s')  order by addition_time desc, id desc limit 0,?) ");
			rs = financialMapper.executeSQL(sql.toString(), user.getId(), lastId,  lastAdditionTime, user.getId(), lastAdditionTime, pageSize);*/
		}

		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, user.getAccount()+"获取记账列表：", "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		
		long end = System.currentTimeMillis();
		logger.info("获取记账列表总计耗时：" +(end - start) +"毫秒, 总数是："+rs.size());
		message.put("message", rs);
		message.put("isSuccess", true);
		return message.getMap();
	}
}
