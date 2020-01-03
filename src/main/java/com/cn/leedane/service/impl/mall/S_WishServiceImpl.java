package com.cn.leedane.service.impl.mall;

import com.cn.leedane.handler.mall.S_ProductHandler;
import com.cn.leedane.handler.mall.S_WishHandler;
import com.cn.leedane.mapper.mall.S_WishMapper;
import com.cn.leedane.model.HttpRequestInfoBean;
import com.cn.leedane.model.OperateLogBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.model.mall.S_ProductBean;
import com.cn.leedane.model.mall.S_WishBean;
import com.cn.leedane.service.OperateLogService;
import com.cn.leedane.service.mall.MallRoleCheckService;
import com.cn.leedane.service.mall.S_WishService;
import com.cn.leedane.utils.*;
import com.cn.leedane.utils.EnumUtil.DataTableType;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 购物商品的service的实现类
 * @author LeeDane
 * 2017年11月7日 下午4:48:03
 * version 1.0
 */
@Service("S_WishService")
public class S_WishServiceImpl extends MallRoleCheckService implements S_WishService<S_WishBean>{
	Logger logger = Logger.getLogger(getClass());

	@Autowired
	private S_ProductHandler productHandler;
	
	@Autowired
	private S_WishHandler wishHandler;
	
	@Autowired
	private OperateLogService<OperateLogBean> operateLogService;
	
	@Autowired
	private S_WishMapper wishMapper;
	
	@Override
	public ResponseModel add(JSONObject json, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_WishServiceImpl-->add():json="+json);
		long productId = JsonUtil.getLongValue(json, "product_id");
		S_ProductBean productBean = productHandler.getNormalProductBean(productId);
		if(productBean == null)
			throw new NullPointerException(EnumUtil.getResponseValue(EnumUtil.ResponseCode.该商品不存在或已被删除.value));

		S_WishBean wishBean = new S_WishBean();
		String returnMsg = "已成功添加到心愿单！";
		wishBean.setStatus(ConstantsUtil.STATUS_NORMAL);
		Date createTime = new Date();
		wishBean.setProductId(productId);
		wishBean.setCreateTime(createTime);
		wishBean.setCreateUserId(user.getId());
		boolean result = false;
		try{
			result = wishMapper.save(wishBean) > 0;
			if(result)
				wishHandler.deleteWishCache(user.getId());
		}catch(DuplicateKeyException e){ //唯一键约束异常不做处理
			result = true;
		}
		ResponseModel responseModel = new ResponseModel();
		if(result)
			responseModel.ok().message(returnMsg);
		else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.数据库保存失败.value)).code(EnumUtil.ResponseCode.数据库保存失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"把商品ID为", productId, "添加到心愿单:", productBean.getTitle() , "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "add()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return responseModel;
	}
	
	@Override
	public ResponseModel getWishNumber(UserBean user, HttpRequestInfoBean request) {
		logger.info("S_WishServiceImpl-->getWishNumber():user="+user.getId());
		return new ResponseModel().ok().message(wishHandler.getWishNumber(user.getId()));
	}
	
	@Override
	public ResponseModel delete(long wishId, UserBean user, HttpRequestInfoBean request) {
		logger.info("S_WishServiceImpl-->delete():wishId="+wishId);
		String returnMsg = "已成功删除该心愿单！";
		boolean result = wishMapper.deleteById(S_WishBean.class, wishId) > 0;
		ResponseModel responseModel = new ResponseModel();
		if(result){
			responseModel.ok().message(returnMsg);
			wishHandler.deleteWishCache(user.getId());
		}else
			responseModel.error().message(EnumUtil.getResponseValue(EnumUtil.ResponseCode.删除失败.value)).code(EnumUtil.ResponseCode.删除失败.value);
		//保存操作日志
		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"删除心愿单ID为", wishId, "结果是：", StringUtil.getSuccessOrNoStr(result)).toString(), "delete()", ConstantsUtil.STATUS_NORMAL, EnumUtil.LogOperateType.内部接口.value);
				
		return responseModel;
	}
	
	
	@Override
	public LayuiTableResponseModel paging(int current, int pageSize, UserBean user, HttpRequestInfoBean request){
		logger.info("S_WishServiceImpl-->paging():current=" +current +", pageSize="+ pageSize);
		LayuiTableResponseModel responseModel = new LayuiTableResponseModel();
		List<Map<String, Object>> rs = new ArrayList<Map<String, Object>>();
		int start = SqlUtil.getPageStart(current, pageSize, 0);
		rs = wishMapper.paging(user.getId(), ConstantsUtil.STATUS_NORMAL, start, pageSize);
		
		if(CollectionUtil.isNotEmpty(rs)){
			for(Map<String, Object> m: rs){
				int productId = StringUtil.changeObjectToInt(m.get("product_id"));
				if(productId > 0){
					S_ProductBean productBean = productHandler.getProductBean(productId);
					if(productBean != null){
						m.put("code", productBean.getCode());
						m.put("title", productBean.getTitle());
						m.put("subtitle", productBean.getSubtitle());
						m.put("digest", productBean.getDigest());
						m.put("platform", productBean.getPlatform());
						m.put("price", productBean.getPrice());
						m.put("oldPrice", productBean.getOldPrice());
						m.put("cashBackRatio", productBean.getCashBackRatio());
						BigDecimal b = new BigDecimal((productBean.getPrice() * productBean.getCashBackRatio()) / 100);  
						double f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();  
						m.put("cashBack", f1);
						m.put("shopId", productBean.getShopId());
						if(productBean.getShop() != null)
							m.put("shopName", productBean.getShop().getName());
						m.put("mainImgLink", productBean.getMainImgLinks().split(";")[0]);
						m.put("category", productBean.getCategory());
					}
				}
			}
		}
		//保存操作日志
//		operateLogService.saveOperateLog(user, request, null, StringUtil.getStringBufferStr(user.getAccount(),"获取心愿单列表，current="+ current, ", pageSize=", pageSize).toString(), "paging()", ConstantsUtil.STATUS_NORMAL, 0);
		responseModel.setData(rs).setCount(wishHandler.getWishNumber(user.getId())).code(0);
		return responseModel;
	}

	@Override
	public int getWishTotal(long productId, String toDayString){
		logger.info("S_WishServiceImpl-->getWishTotal():productId=" + productId +", toDayString ="+ toDayString);
		return SqlUtil.getTotalByList(wishMapper.getTotal(DataTableType.商品心愿单.value, "where product_id = "+ productId +" and DATE_FORMAT(create_time, '%Y-%c-%d') = '" + toDayString +"'"));
	}
}
