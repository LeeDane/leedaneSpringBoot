package com.cn.leedane.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.CartMapper;
import com.cn.leedane.mapper.ProductMapper;
import com.cn.leedane.model.CartBean;
import com.cn.leedane.model.CartDetailsBean;
import com.cn.leedane.model.ProductBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.CartDetailsService;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 购物车相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:09:11
 * Version 1.0
 */
public class CartTest extends BaseTest {
	
	@Resource
	private CartMapper cartMapper;
	
	@Resource
	private UserService<UserBean> userService;
	
	@Resource
	private ProductMapper productMapper;
	/**
	 * 系统级别的缓存对象
	 */
	private SystemCache systemCache;
	
	
	@Resource
	private CartDetailsService<CartDetailsBean> cartDetailsService;
	@Test
	public void addCart() throws Exception{
		systemCache = (SystemCache) SpringUtil.getBean("systemCache");
		String adminId = (String) systemCache.getCache("admin-id");
		int aid = 1;
		if(!StringUtil.isNull(adminId)){
			aid = Integer.parseInt(adminId);
		}
		UserBean user = userService.findById(aid);
		CartBean bean = new CartBean();
		bean.setCreateUserId(user.getId());
		bean.setCreateTime(new Date());
		
		List<CartDetailsBean> ls = new ArrayList<CartDetailsBean>();
		CartDetailsBean details1 = new CartDetailsBean();
		details1.setCreateUser(user);
		details1.setInventory(10);
		details1.setName("华为手机");
		details1.setNumber(2);
		details1.setOriginPrice(1999f);
		ProductBean product1 = new ProductBean();
		product1.setName("华为手机商品");
		details1.setPrice(1099f);
		details1.setTotalPrice(2198f);
		details1.setCart(bean);
		details1.setProduct(productMapper.findById(ProductBean.class, 1));
		ls.add(details1);
		
		CartDetailsBean details2 = new CartDetailsBean();
		details2.setCreateUser(user);
		details2.setInventory(10);
		details2.setName("魅族手机");
		details2.setNumber(2);
		details2.setOriginPrice(2499f);
		ProductBean product2 = new ProductBean();
		product2.setName("魅族手机商品");
		details2.setPrice(1999f);
		details2.setTotalPrice(3998f);
		details2.setCart(bean);
		ls.add(details2);
		details2.setProduct(productMapper.findById(ProductBean.class, 2));
		bean.setDetails(ls);
		cartMapper.save(bean);
	}	
	@Test
	public void loadCart(){
		List<CartBean> beans = cartMapper.getBeans("select * from t_cart");
		for(CartBean bean: beans){
			System.out.println(bean.getDetails().get(0).getName());
		}
		
	}
	
	@Test
	public void loadById(){
		cartMapper.findById(CartBean.class, 1);
	}
}
