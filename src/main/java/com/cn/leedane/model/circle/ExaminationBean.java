package com.cn.leedane.model.circle;

import com.cn.leedane.model.RecordTimeBean;

/**
 * 考核试题实体bean
 * @author LeeDane
 * 2017年6月1日 下午2:41:24
 * version 1.0
 */
public class ExaminationBean extends RecordTimeBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7736865424176281023L;
	
	/**
	 * 总的积分(由于每个问题都有积分，如果不想计算，最好不填，程序自动计算所有问题获取总积分，
	 * 否则填写的要是以下方的问题不服，将以填写的为准，问题的积分将作废！最终会导致用户某些问题填写错误无法计算积分。)
	 */
	private int score;

	//private int 
}
