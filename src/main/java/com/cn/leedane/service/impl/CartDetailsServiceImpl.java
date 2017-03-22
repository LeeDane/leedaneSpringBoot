package com.cn.leedane.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cn.leedane.mapper.CartMapper;
import com.cn.leedane.mapper.UserMapper;
import com.cn.leedane.model.CartBean;
import com.cn.leedane.model.CartDetailsBean;
import com.cn.leedane.model.ProductBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.CartDetailsService;
import com.cn.leedane.utils.EnumUtil.DataTableType;
/**
 * 购物车清单service的实现类
 * @author LeeDane
 * 2016年7月12日 下午12:28:12
 * Version 1.0
 */
@Service("cartDetailsService")
public class CartDetailsServiceImpl implements CartDetailsService<CartDetailsBean>{
	Logger logger = Logger.getLogger(getClass());
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private CartMapper cartMapper;
	
	
	@Override
	public void addCartDetails(){
		UserBean user = userMapper.findById(UserBean.class, 1);
		CartBean bean = new CartBean();
		bean.setCreateUserId(user.getId());
		bean.setCreateTime(new Date());
		
		List<CartDetailsBean> ls = new ArrayList<CartDetailsBean>();
		CartDetailsBean details1 = new CartDetailsBean();
		details1.setCreateUser(user);
		details1.setInventory(10);
		details1.setName("中兴手机");
		details1.setNumber(2);
		details1.setOriginPrice(1999f);
		ProductBean product1 = new ProductBean();
		product1.setName("中兴手机商品");
		//details1.setProduct(product1);
		details1.setPrice(1099f);
		details1.setTotalPrice(2198f);
		ls.add(details1);
		
		CartDetailsBean details2 = new CartDetailsBean();
		details2.setCreateUser(user);
		details2.setInventory(10);
		details2.setName("魅族手机");
		details2.setNumber(2);
		details2.setOriginPrice(2499f);
		ProductBean product2 = new ProductBean();
		product2.setName("魅族手机商品");
		//details2.setProduct(product1);
		details2.setPrice(1999f);
		details2.setTotalPrice(3998f);
		ls.add(details2);
		bean.setDetails(ls);	
		cartMapper.save(bean);
	}
}
