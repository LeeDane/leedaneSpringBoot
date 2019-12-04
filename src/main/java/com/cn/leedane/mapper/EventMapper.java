package com.cn.leedane.mapper;

import com.cn.leedane.model.EventBean;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 大事件mapper接口类
 * @author LeeDane
 * 2019年7月19日 下午19:12:40
 * Version 1.0
 */
@Repository
public interface EventMapper extends BaseMapper<EventBean>{

	public List<Map<String, Object>> events(
			@Param("start")int start,
			@Param("pageSize")int pageSize,
			@Param("status") int status);


	public List<Map<String, Object>> all();

}
