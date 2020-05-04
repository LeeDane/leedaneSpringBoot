package com.cn.leedane.test;

import java.util.Date;

import javax.annotation.Resource;

import com.cn.leedane.handler.UserHandler;
import org.junit.Test;

import com.cn.leedane.cache.SystemCache;
import com.cn.leedane.mapper.CompanyMapper;
import com.cn.leedane.mapper.ProductMapper;
import com.cn.leedane.model.CompanyBean;
import com.cn.leedane.model.ProductBean;
import com.cn.leedane.model.UserBean;
import com.cn.leedane.service.UserService;
import com.cn.leedane.springboot.SpringUtil;
import com.cn.leedane.utils.StringUtil;

/**
 * 商品相关的测试类
 * @author LeeDane
 * 2016年7月12日 下午3:28:06
 * Version 1.0
 */
public class ProductTest extends BaseTest {
	
	@Resource
	private ProductMapper productMapper;
	
	@Resource
	private UserHandler userHandler;

	@Resource
	private CompanyMapper companyMapper;
	/**
	 * 缓存
	 */
	private SystemCache systemCache;
	/*@Resource
	private CartDetailsService<CartDetailsBean> cartDetailsService;*/
	@Test
	public void addProduct() throws Exception{
		
		systemCache = (SystemCache) SpringUtil.getBean("systemCache");
		String adminId = (String) systemCache.getCache("admin-id");
		int aid = 1;
		if(!StringUtil.isNull(adminId)){
			aid = Integer.parseInt(adminId);
		}
		UserBean user = userHandler.getUserBean(aid);
		CompanyBean company1 = companyMapper.findById(CompanyBean.class, 1);
		CompanyBean company2 = companyMapper.findById(CompanyBean.class, 2);
		
		ProductBean product1 = new ProductBean();
		product1.setCreateUserId(user.getId());
		product1.setArea("广东深圳");
		product1.setName("华为手机");
		product1.setCode("SB10001");
		product1.setOriginPrice(1999f);
		product1.setName("华为手机商品");
		//details1.setProduct(product1);
		product1.setPrice(1099f);
		product1.setColor("黑色");
		product1.setCreateTime(new Date());		
		product1.setCompany(company1);
		product1.setDesc("这是一部性价比还不错的手机");
		product1.setNumber(100000);
		
		ProductBean product2 = new ProductBean();
		product2.setCreateUserId(user.getId());
		product2.setArea("广东珠海");
		product2.setName("魅族手机");
		product2.setCode("TH10001");
		product2.setOriginPrice(2499f);
		product2.setName("魅族手机商品");
		product2.setPrice(1999f);
		product2.setColor("金色");
		product2.setCreateTime(new Date());
		product2.setCompany(company2);
		product2.setDesc("这个青年良品");
		product2.setNumber(200000);
		
		productMapper.save(product1);
		productMapper.save(product2);
	}	
}
