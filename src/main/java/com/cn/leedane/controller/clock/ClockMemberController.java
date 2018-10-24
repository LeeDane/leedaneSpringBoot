package com.cn.leedane.controller.clock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cn.leedane.controller.BaseController;
import com.cn.leedane.model.clock.ClockMemberBean;
import com.cn.leedane.service.clock.ClockMemberService;
import com.cn.leedane.utils.ControllerBaseNameUtil;

/**
 * 任务成员接口controller
 * @author LeeDane
 * 2018年9月11日 下午9:52:13
 * version 1.0
 */
@RestController
@RequestMapping(value = ControllerBaseNameUtil.clockMember)
public class ClockMemberController extends BaseController{

	protected final Log log = LogFactory.getLog(getClass());

	@Autowired
	private ClockMemberService<ClockMemberBean> clockMemberService;
	
}
