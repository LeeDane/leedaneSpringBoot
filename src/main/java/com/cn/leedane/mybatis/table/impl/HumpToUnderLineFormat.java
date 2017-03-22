package com.cn.leedane.mybatis.table.impl;

import java.util.Map;

import org.springframework.util.ConcurrentReferenceHashMap;

import com.cn.leedane.mybatis.table.TableFormat;


public class HumpToUnderLineFormat implements TableFormat {

    private String tablePrefix = "T_";

    public static Map<String, String> tableCache = new ConcurrentReferenceHashMap<String, String>();

    public static Map<String, String> columnCache = new ConcurrentReferenceHashMap<String, String>();

    public String getTableName(String beanName) {
        String tableName = tableCache.get(beanName);
        if (tableName == null) {
            StringBuilder tableNameBuilder = new StringBuilder(tablePrefix);
            String[] beanNames = beanName.split("(?<!^)(?=[A-Z])");
            for (int i = 0; i < beanNames.length; i++) {
                tableNameBuilder.append(beanNames[i].toUpperCase());
                if (i != beanNames.length - 1) {
                    tableNameBuilder.append("_"); 
                }
            }
            tableName = tableNameBuilder.toString();
            tableCache.put(beanName, tableName);
        }

        return tableName;
    }

    public String getColumnName(String beanPropertyName) {
        String tableColumn = tableCache.get(beanPropertyName);
        if (tableColumn == null) {
            StringBuilder tableColumnNameBuilder = new StringBuilder();
            String[] beanNames = beanPropertyName.split("(?<!^)(?=[A-Z])");
            for (int i = 0; i < beanNames.length; i++) {
                tableColumnNameBuilder.append(beanNames[i].toUpperCase());
                if (i != beanNames.length - 1) {
                    tableColumnNameBuilder.append("_");
                }
            }
            tableColumn = tableColumnNameBuilder.toString();
            tableCache.put(beanPropertyName, tableColumn);
        }

        return tableColumn;
    }

	public String getId() {
		return "ID";
	}
}
