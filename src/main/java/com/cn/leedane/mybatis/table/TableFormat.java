package com.cn.leedane.mybatis.table;

public interface TableFormat {
	public String getId();

	public String getTableName(String beanName);

	public String getColumnName(String beanPropertyName);
}
