package com.cn.leedane.service.impl;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.cn.leedane.model.OptionBean;
import com.cn.leedane.service.OptionService;

/**
 * 选项service的实现类
 * @author LeeDane
 * 2016年7月12日 下午2:04:03
 * Version 1.0
 */
@Service("optionService")
public class OptionServiceImpl implements OptionService<OptionBean>{
	Logger logger = Logger.getLogger(getClass());
}
