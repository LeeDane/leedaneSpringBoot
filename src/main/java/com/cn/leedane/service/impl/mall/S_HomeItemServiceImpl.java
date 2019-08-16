package com.cn.leedane.service.impl.mall;

import com.cn.leedane.exception.ParameterUnspecificationException;
import com.cn.leedane.handler.mall.S_HomeItemHandler;
import com.cn.leedane.handler.mall.S_ProductHandler;
import com.cn.leedane.mapper.mall.S_HomeItemMapper;
import com.cn.leedane.mapper.mall.S_HomeItemProductMapper;
import com.cn.leedane.model.*;
import com.cn.leedane.model.mall.*;
import com.cn.leedane.service.CategoryService;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.S_HomeItemService;
import com.cn.leedane.utils.*;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 首页分类商品的service的实现类
 * @author LeeDane
 * 2017年12月28日 上午10:28:02
 * version 1.0
 */
@Service("S_HomeItemService")
public class S_HomeItemServiceImpl extends MallRoleCheckService implements S_HomeItemService<S_HomeItemBean>{
	Logger logger = Logger.getLogger(getClass());
	
	@Autowired
	private S_ProductHandler productHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private S_HomeItemHandler homeItemHandler;

	
	@Autowired
	private S_HomeItemMapper homeItemMapper;
	
	@Autowired
	private CategoryService<CategoryBean> categoryService;
	
	@Autowired
	private S_HomeItemProductMapper homeItemProductMapper;

	@Value("${constant.mall.home.category.id}")
    public int MALL_HOME_CATEGORY_ID;
	
	@Override
	public Map<String, Object> addItem(JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->addItem():json="+json);
		SqlUtil sqlUtil = new SqlUtil();
		S_HomeItemBean homeItemBean = (S_HomeItemBean) sqlUtil.getBean(json, S_HomeItemBean.class);
		if(homeItemBean.getCategoryId() < 1 || homeItemBean.getNumber() > 10 || homeItemBean.getNumber() < 1)
			throw new ParameterUnspecificationException("分类为空或者数量不合法。");
		
		checMallkAdmin(user);

		ResponseMap message = new ResponseMap();
		String returnMsg = "新的分类已经发布成功！";
		homeItemBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		Date createTime = new Date();
		homeItemBean.setCreateTime(createTime);
		homeItemBean.setCreateUserId(user.getId());
		//设置默认排序是1
		if(homeItemBean.getCategoryId() < 1)
			homeItemBean.setCategoryOrder(1);
		boolean result = homeItemMapper.save(homeItemBean) > 0;
		if(result){
			homeItemHandler.deleteCategoryListCache();
			message.put("isSuccess", true);
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"发布新的分类:", homeItemBean.getId() , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "addItem()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> updateItem(int itemId, JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->updateItem():json="+json +", itemId="+ itemId);
		S_HomeItemBean homeItemBean = homeItemMapper.findById(S_HomeItemBean.class, itemId);
		if(homeItemBean == null)
			throw new ParameterUnspecificationException("分类项为空或者数量不合法。");
		
		checMallkAdmin(user);

		ResponseMap message = new ResponseMap();
		String returnMsg = "分类项修改成功！";
		
		if(json.containsKey("number"))
			homeItemBean.setNumber(JsonUtil.getIntValue(json, "number"));
		
		if(json.containsKey("order"))
			homeItemBean.setCategoryOrder(JsonUtil.getIntValue(json, "order"));
		
		boolean result = homeItemMapper.update(homeItemBean) > 0;
		if(result){
			homeItemHandler.deleteItemShowCache(itemId);
			message.put("isSuccess", true);
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改分类项ID为:", itemId , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "addItem()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> getItem(int categoryId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->getItem():categoryId="+categoryId);
		ResponseMap message = new ResponseMap();
		
		S_HomeItemShowBean homeItemShowBean = homeItemHandler.getCategory(categoryId);
		message.put("isNew", homeItemShowBean == null);
		//没有找到分类记录，说明是还没有处理的
		if(homeItemShowBean == null){
			
		}
		message.put("isSuccess", true);
		message.put("message", homeItemShowBean);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> deleteItem(int itemId, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->deleteItem():itemId="+itemId);
		
		S_HomeItemShowBean homeItemShowBean = homeItemHandler.getCategory(itemId);
		if(homeItemShowBean == null || homeItemShowBean.getItemId() < 1)
			throw new NullPointerException("该分类项已经不存在或被删除了！");
		
		ResponseMap message = new ResponseMap();
		checMallkAdmin(user);
		
		homeItemProductMapper.deleteProducts(itemId);
		
		boolean result = homeItemMapper.deleteById(S_HomeItemBean.class, itemId) > 0;
		
		if(result){
			homeItemHandler.deleteCategoryListCache();
			homeItemHandler.deleteItemShowCache(itemId);
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除分类展示项ID为", itemId , "结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "deleteItem()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> showCategoryList() {
		logger.info("S_HomeItemServiceImpl-->showCategoryList()");
		ResponseMap message = new ResponseMap();
		S_HomeItemBeans itemBeans = new S_HomeItemBeans();
		itemBeans.setHomeItemBeans(homeItemHandler.showCategoryList());
		message.put("isSuccess", true);
		message.put("message", itemBeans);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		
		return message.getMap();
	}
	
	@Override
	public Map<String, Object> matchingCategory(int itemId, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->showCategoryList()");
		ResponseMap message = new ResponseMap();
		
		List<S_HomeItemBean>  s_HomeItemBeans = homeItemHandler.showCategoryList();
		if(CollectionUtil.isEmpty(s_HomeItemBeans) || !inCategoryList(s_HomeItemBeans, itemId)){
			throw new NullPointerException("该分类项不存在，请确定是否已经被删除啦！");
		}
		
		S_HomeItemShowBean s_HomeItemShowBean = homeItemHandler.getCategory(itemId);
		if(s_HomeItemShowBean == null || s_HomeItemShowBean.getItemId() < 1){
			throw new NullPointerException("该分类项不存在，请确定是否已经被删除啦！");
		}		
		//获取总的子分类
		Map<String, Object>  caMap = categoryService.children(true, s_HomeItemShowBean.getCategoryId(), user, request);
		
		//已经分配的子分类
		List<KeyValueBean> already = s_HomeItemShowBean.getChildrens();
		if(already == null)
			already = new ArrayList<KeyValueBean>();
		
		//还未分类的子分类
		List<KeyValueBean> no = new ArrayList<KeyValueBean>();
		if(StringUtil.changeObjectToBoolean(caMap.get("isSuccess"))){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> listCategory = (List<Map<String, Object>>)caMap.get("message");
			for(Map<String, Object> mm: listCategory){
				String ctId = StringUtil.changeNotNull(mm.get("id"));
				if(!ifAleardy(ctId, already)){
					KeyValueBean keyValueBean = new KeyValueBean();
					keyValueBean.setKey(ctId);
					keyValueBean.setValue(StringUtil.changeNotNull(mm.get("text")));
					no.add(keyValueBean);
				}
			}
		}
		
		Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("already", already);
		mp.put("no", no);
		message.put("isSuccess", true);
		message.put("message", mp);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		
		return message.getMap();
	}

	/**
	 * 判断是否已经存在
	 * @param categoryId
	 * @param already
	 * @return
	 */
	private boolean ifAleardy(String categoryId, List<KeyValueBean> already) {
		for(KeyValueBean kValueBean: already){
			if(kValueBean.getKey().equals(categoryId))
				return true;
		}
		return false;
	}

	/**
	 * 判断该分类是否存在
	 * @param itemId
	 * @return
	 */
	private boolean inCategoryList(List<S_HomeItemBean>  s_HomeItemBeans, int itemId) {
		for(S_HomeItemBean homeItemBean: s_HomeItemBeans){
			if(homeItemBean.getId() == itemId)
				return true;
		}
		return false;
	}
	
	@Override
	public Map<String, Object> updateCategory(int itemId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->updateCategory()");
		ResponseMap message = new ResponseMap();
		
		/*List<S_HomeItemBean>  s_HomeCategoryBeans = homeItemHandler.showCategoryList();
		if(CollectionUtil.isEmpty(s_HomeCategoryBeans) || !inCategoryList(s_HomeCategoryBeans, itemId)){
			throw new NullPointerException("该分类项不存在，请确定是否已经被删除啦！");
		}
		
		S_HomeItemShowBean s_HomeItemShowBean = homeItemHandler.getCategory(itemId);
		if(s_HomeItemShowBean == null || s_HomeItemShowBean.getItemId() < 1){
			throw new NullPointerException("该分类项不存在，请确定是否已经被删除啦！");
		}		
		*/
		S_HomeItemBean homeItemBean = homeItemMapper.findById(S_HomeItemBean.class, itemId);
		if(homeItemBean == null)
			throw new NullPointerException("该分类项不存在，请确定是否已经被删除啦！");
		
		homeItemBean.setChildren(json.optJSONArray("data").toString());
		boolean result = homeItemMapper.update(homeItemBean) > 0;
		if(result){
			homeItemHandler.deleteItemShowCache(itemId);
			message.put("isSuccess", true);
			message.put("message", "该分类的子分类修改成功！");
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库修改失败.value);
		}
		return message.getMap();
	}

	@Override
	public Map<String, Object> addProduct(int itemId,
			JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		int productId = JsonUtil.getIntValue(json, "product_id");
		S_ProductBean productBean = productHandler.getNormalProductBean(productId);
		if(productBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));
		
		S_HomeItemProductBean homeItemProductBean = new S_HomeItemProductBean();
		homeItemProductBean.setProductId(productId);
		homeItemProductBean.setItemId(itemId);
		
		ResponseMap message = new ResponseMap();
		String returnMsg = "已经为该分类添加商品！";
		homeItemProductBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		homeItemProductBean.setCreateTime(new Date());
		homeItemProductBean.setCreateUserId(user.getId());
		//设置默认排序是1
		if(homeItemProductBean.getProductOrder() < 1)
			homeItemProductBean.setProductOrder(1);
		
		boolean result = homeItemProductMapper.save(homeItemProductBean) > 0;
		if(result){
			homeItemHandler.deleteItemShowCache(itemId);
			message.put("isSuccess", true);
			message.put("message", returnMsg);
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.数据库保存失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为分类项ID为", itemId, "发布新的商品ID为:", productId , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> deleteProduct(int itemId, int productId,
			UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->deleteProduct():itemId="+itemId +", productId="+ productId);
		ResponseMap message = new ResponseMap();
		checMallkAdmin(user);
		S_HomeItemShowBean homeItemShowBean = homeItemHandler.getCategory(itemId);
		if(homeItemShowBean == null || homeItemShowBean.getItemId() < 1)
			throw new NullPointerException("该分类项已经不存在或被删除了！");
		
		boolean result = homeItemProductMapper.deleteById(S_HomeItemProductBean.class, productId) > 0;
		
		if(result){
			homeItemHandler.deleteItemShowCache(itemId);
			message.put("isSuccess", true);
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
			message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		}else{
			message.put("message", EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value));
			message.put("responseCode", EnumUtil.ResponseCode.删除失败.value);
		}
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除分类展示项ID为", itemId , "结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return message.getMap();
	}

	@Override
	public Map<String, Object> noList(JSONObject json, UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->showCategoryList()");
		ResponseMap message = new ResponseMap();
		
		List<S_HomeItemBean> s_HomeItemBeans = homeItemHandler.showCategoryList();	
		//获取总的子分类
		Map<String, Object>  caMap = categoryService.children(true, MALL_HOME_CATEGORY_ID, user, request);
		
		//已经分配的分类
		List<KeyValueBean> already = new ArrayList<KeyValueBean>();
		for(S_HomeItemBean homeItemBean: s_HomeItemBeans){
			KeyValueBean keyValueBean = new KeyValueBean();
			keyValueBean.setKey(homeItemBean.getCategoryId()+"");
			keyValueBean.setValue(homeItemBean.getCategoryText());
			already.add(keyValueBean);
		}
		
		//还未分类的子分类
		List<KeyValueBean> no = new ArrayList<KeyValueBean>();
		if(StringUtil.changeObjectToBoolean(caMap.get("isSuccess"))){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> listCategory = (List<Map<String, Object>>)caMap.get("message");
			for(Map<String, Object> mm: listCategory){
				String ctId = StringUtil.changeNotNull(mm.get("id"));
				if(!ifAleardy(ctId, already)){
					KeyValueBean keyValueBean = new KeyValueBean();
					keyValueBean.setKey(ctId);
					keyValueBean.setValue(StringUtil.changeNotNull(mm.get("text")));
					no.add(keyValueBean);
				}
			}
		}
		
		Map<String, Object> mp = new HashMap<String, Object>();
		mp.put("already", already);
		mp.put("no", no);
		message.put("isSuccess", true);
		message.put("message", mp);
		message.put("responseCode", EnumUtil.ResponseCode.请求返回成功码.value);
		
		return message.getMap();
	}

	@Override
	public Map<String, Object> getItems(UserBean user,
			HttpRequestInfoBean request) {
		
		logger.info("S_HomeItemServiceImpl-->getItems()");
		ResponseMap message = new ResponseMap();
		List<S_HomeItemBean> categoryList = homeItemHandler.showCategoryList();
		List<S_HomeItemShowBean> homeItemShowBeans = new ArrayList<S_HomeItemShowBean>();
		for(S_HomeItemBean category: categoryList)
			homeItemShowBeans.add(homeItemHandler.getCategory(category.getId()));
		
		message.put("message", homeItemShowBeans);
		message.put("isSuccess", true);
		return message.getMap();
	}
}
