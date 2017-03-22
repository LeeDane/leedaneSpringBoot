package com.cn.leedane.handler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

/**
 * mybatis查询的时候如果字段null，将其转化成""
 * @author LeeDane
 * 2016年12月29日 下午12:23:28
 * Version 1.0
 */
public class EmptyStringIfNullHandler implements TypeHandler<String>{

	@Override
	public String getResult(ResultSet rs, String columnName) throws SQLException {
		return (rs.getString(columnName) == null) ? "" : rs.getString(columnName); 
	}

	@Override
	public String getResult(ResultSet rs, int columnIndex) throws SQLException {
		return (rs.getString(columnIndex) == null) ? "" : rs.getString(columnIndex);
	}

	@Override
	public String getResult(CallableStatement cs, int columnIndex)
			throws SQLException {
		return (cs.getString(columnIndex) == null) ? "" : cs.getString(columnIndex);
	}

	@Override
	public void setParameter(PreparedStatement arg0, int arg1, String arg2,
			JdbcType arg3) throws SQLException {
		
	}
}
