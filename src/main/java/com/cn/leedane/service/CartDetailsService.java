package com.cn.leedane.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;

/**
 * 购物车清单的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:30:44
 * Version 1.0
 */
@Transactional
public interface CartDetailsService<T extends IDBean>{

	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void addCartDetails();
}
