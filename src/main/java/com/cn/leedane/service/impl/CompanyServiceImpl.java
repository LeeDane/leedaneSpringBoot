package com.cn.leedane.service.impl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cn.leedane.model.CompanyBean;
import com.cn.leedane.service.CompanyService;
/**
 * 公司service的实现类
 * @author LeeDane
 * 2016年7月12日 下午1:32:42
 * Version 1.0
 */
@Service("companyService")
public class CompanyServiceImpl implements CompanyService<CompanyBean>{
	Logger logger = Logger.getLogger(getClass());
}
