package com.cn.leedane.service;
import org.springframework.transaction.annotation.Transactional;

import com.cn.leedane.model.IDBean;
/**
 * 公司的Service类
 * @author LeeDane
 * 2016年7月12日 上午11:32:20
 * Version 1.0
 */
@Transactional("txManager")
public interface CompanyService<T extends IDBean>{

}
