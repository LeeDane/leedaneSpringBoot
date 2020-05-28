package com.cn.leedane.mapper.letter;

import com.cn.leedane.mapper.BaseMapper;
import com.cn.leedane.model.letter.FutureLetterBean;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 未来信件mapper接口类
 * @author LeeDane
 * 2020年5月23日 上午10:39:24
 * Version 1.0
 */
public interface FutureLetterMapper extends BaseMapper<FutureLetterBean>{

	/**
	 * 获取所有未到期的信件列表
	 * @return
	 */
	public List<FutureLetterBean> all();

	/**
	 * 分页获取未来信件列表
	 * @param userId 用户ID
	 * @return
	 */
	public List<Map<String, Object>> list(@Param("userId") long userId, @Param("start") int start, @Param("limit") int rows);
}
