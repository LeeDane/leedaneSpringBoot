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
	public ResponseModel addItem(JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->addItem():json="+json);
		SqlUtil sqlUtil = new SqlUtil();
		S_HomeItemBean homeItemBean = (S_HomeItemBean) sqlUtil.getBean(json, S_HomeItemBean.class);
		if(homeItemBean.getCategoryId() < 1 || homeItemBean.getNumber() > 10 || homeItemBean.getNumber() < 1)
			throw new ParameterUnspecificationException("分类为空或者数量不合法。");

		checkMallAdmin(user);
		ResponseModel responseModel = new ResponseModel();
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
			responseModel.ok().message(returnMsg);
		}else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value)).code(EnumUtil.ResponseCode.数据库保存失败.value);

		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"发布新的分类:", homeItemBean.getId() , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "addItem()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}
	
	@Override
	public ResponseModel updateItem(long itemId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->updateItem():json="+json +", itemId="+ itemId);
		S_HomeItemBean homeItemBean = homeItemMapper.findById(S_HomeItemBean.class, itemId);
		if(homeItemBean == null)
			throw new ParameterUnspecificationException("分类项为空或者数量不合法。");

		checkMallAdmin(user);
		ResponseModel responseModel = new ResponseModel();
		String returnMsg = "分类项修改成功！";
		if(json.containsKey("number"))
			homeItemBean.setNumber(JsonUtil.getIntValue(json, "number"));
		
		if(json.containsKey("order"))
			homeItemBean.setCategoryOrder(JsonUtil.getIntValue(json, "order"));
		
		boolean result = homeItemMapper.update(homeItemBean) > 0;
		if(result){
			homeItemHandler.deleteItemShowCache(itemId);
			responseModel.ok().message(returnMsg);
		}else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value)).code(EnumUtil.ResponseCode.数据库修改失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"修改分类项ID为:", itemId , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "addItem()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}
	
	@Override
	public ResponseModel getItem(long categoryId, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->getItem():categoryId="+categoryId);
		S_HomeItemShowBean homeItemShowBean = homeItemHandler.getCategory(categoryId);
//		message.put("isNew", homeItemShowBean == null);
		//没有找到分类记录，说明是还没有处理的
		if(homeItemShowBean == null){
			
		}
		return new ResponseModel().ok().message(homeItemShowBean);
	}
	
	@Override
	public ResponseModel deleteItem(long itemId, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->deleteItem():itemId="+itemId);
		S_HomeItemShowBean homeItemShowBean = homeItemHandler.getCategory(itemId);
		if(homeItemShowBean == null || homeItemShowBean.getItemId() < 1)
			throw new NullPointerException("该分类项已经不存在或被删除了！");

		ResponseModel responseModel = new ResponseModel();
		checkMallAdmin(user);
		
		homeItemProductMapper.deleteProducts(itemId);
		
		boolean result = homeItemMapper.deleteById(S_HomeItemBean.class, itemId) > 0;
		
		if(result){
			homeItemHandler.deleteCategoryListCache();
			homeItemHandler.deleteItemShowCache(itemId);
			responseModel.ok().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
		}else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value)).code(EnumUtil.ResponseCode.删除失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除分类展示项ID为", itemId , "结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "deleteItem()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}
	
	@Override
	public ResponseModel showCategoryList() {
		logger.info("S_HomeItemServiceImpl-->showCategoryList()");
		S_HomeItemBeans itemBeans = new S_HomeItemBeans();
		itemBeans.setHomeItemBeans(homeItemHandler.showCategoryList());
		return new ResponseModel().ok().message(itemBeans);
	}
	
	@Override
	public ResponseModel matchingCategory(long itemId, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->showCategoryList()");
		List<S_HomeItemBean>  s_HomeItemBeans = homeItemHandler.showCategoryList();
		if(CollectionUtil.isEmpty(s_HomeItemBeans) || !inCategoryList(s_HomeItemBeans, itemId)){
			throw new NullPointerException("该分类项不存在，请确定是否已经被删除啦！");
		}
		
		S_HomeItemShowBean s_HomeItemShowBean = homeItemHandler.getCategory(itemId);
		if(s_HomeItemShowBean == null || s_HomeItemShowBean.getItemId() < 1){
			throw new NullPointerException("该分类项不存在，请确定是否已经被删除啦！");
		}		
		//获取总的子分类
		ResponseModel responseModel = categoryService.children(true, s_HomeItemShowBean.getCategoryId(), user, request);
		
		//已经分配的子分类
		List<KeyValueBean> already = s_HomeItemShowBean.getChildrens();
		if(already == null)
			already = new ArrayList<KeyValueBean>();
		
		//还未分类的子分类
		List<KeyValueBean> no = new ArrayList<KeyValueBean>();
		if(responseModel.isSuccess()){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> listCategory = (List<Map<String, Object>>)responseModel.getMessage();
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
		return new ResponseModel().ok().message(mp);
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
	private boolean inCategoryList(List<S_HomeItemBean>  s_HomeItemBeans, long itemId) {
		for(S_HomeItemBean homeItemBean: s_HomeItemBeans){
			if(homeItemBean.getId() == itemId)
				return true;
		}
		return false;
	}
	
	@Override
	public ResponseModel updateCategory(long itemId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->updateCategory()");
		ResponseModel responseModel = new ResponseModel();
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
			responseModel.ok().message("该分类的子分类修改成功！");
		}else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库修改失败.value)).code(EnumUtil.ResponseCode.数据库修改失败.value);
		return responseModel;
	}

	@Override
	public ResponseModel addProduct(long itemId, JSONObject json, UserBean user, HttpRequestInfoBean request) {
		long productId = JsonUtil.getLongValue(json, "product_id");
		S_ProductBean productBean = productHandler.getNormalProductBean(productId);
		if(productBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));
		
		S_HomeItemProductBean homeItemProductBean = new S_HomeItemProductBean();
		homeItemProductBean.setProductId(productId);
		homeItemProductBean.setItemId(itemId);

		ResponseModel responseModel = new ResponseModel();
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
			responseModel.ok().message(returnMsg);
		}else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value)).code(EnumUtil.ResponseCode.数据库保存失败.value);

		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"为分类项ID为", itemId, "发布新的商品ID为:", productId , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}

	@Override
	public ResponseModel deleteProduct(long itemId, long productId, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->deleteProduct():itemId="+itemId +", productId="+ productId);
		ResponseModel responseModel = new ResponseModel();
		checkMallAdmin(user);
		S_HomeItemShowBean homeItemShowBean = homeItemHandler.getCategory(itemId);
		if(homeItemShowBean == null || homeItemShowBean.getItemId() < 1)
			throw new NullPointerException("该分类项已经不存在或被删除了！");
		boolean result = homeItemProductMapper.deleteById(S_HomeItemProductBean.class, productId) > 0;
		if(result){
			homeItemHandler.deleteItemShowCache(itemId);
			responseModel.ok().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除成功.value));
		}else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value)).code(EnumUtil.ResponseCode.删除失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除分类展示项ID为", itemId , "结果是：", StringUtil.getSuccessOrNoStr(true)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
		return responseModel;
	}

	@Override
	public ResponseModel noList(JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->showCategoryList()");
		List<S_HomeItemBean> s_HomeItemBeans = homeItemHandler.showCategoryList();	
		//获取总的子分类
		ResponseModel responseModel = categoryService.children(true, MALL_HOME_CATEGORY_ID, user, request);
		
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
		if(responseModel.isSuccess()){
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> listCategory = (List<Map<String, Object>>)responseModel.getMessage();
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
		return new ResponseModel().ok().message(mp);
	}

	@Override
	public ResponseModel getItems(UserBean user,
			HttpRequestInfoBean request) {
		logger.info("S_HomeItemServiceImpl-->getItems()");
		List<S_HomeItemBean> categoryList = homeItemHandler.showCategoryList();
		List<S_HomeItemShowBean> homeItemShowBeans = new ArrayList<S_HomeItemShowBean>();
		for(S_HomeItemBean category: categoryList)
			homeItemShowBeans.add(homeItemHandler.getCategory(category.getId()));
		return new ResponseModel().ok().message(homeItemShowBeans);
	}
}
