package com.cn.leedane.utils.circle;

/**
 * 圈子相关的工具类
 * @author LeeDane
 * 2017年6月1日 下午2:43:23
 * version 1.0
 */
public class CircleUtil {

	
	/**
	 * 支持的问题的类型
	 * @author LeeDane
	 * 2017年6月1日 下午2:44:52
	 * version 1.0
	 */
	public enum SupportQuestionType{
		单选题(1),
		多选题(2),
		判断题(3),
		填空题(4);
		private SupportQuestionType(int value) {
			this.value = value;
		}
	
		public final int value;
	}
}
