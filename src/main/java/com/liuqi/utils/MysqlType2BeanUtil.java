package com.liuqi.utils;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

public class MysqlType2BeanUtil {
	private static Map<String,String> typeMap=new HashMap<String,String>();
	private static Map<String,String> numMap=new HashMap<String,String>();
	static{
		//类型
		typeMap.put("INT", "Integer");
		typeMap.put("VARCHAR", "String");
		typeMap.put("CHAR", "String");
		typeMap.put("BLOB", "byte[]");
		typeMap.put("TEXT", "String");
		typeMap.put("INTEGER", "Long");
		typeMap.put("TINYINT", "Integer");
		typeMap.put("SMALLINT", "Integer");
		typeMap.put("MEDIUMINT", "Integer");
		typeMap.put("BIT", "Boolean");
		typeMap.put("BIGINT", "Long");
		typeMap.put("FLOAT", "Float");
		typeMap.put("DOUBLE", "Double");
		typeMap.put("DECIMAL", "BigDecimal");
		typeMap.put("BOOLEAN", "Integer");
		typeMap.put("ID", "Integer");
		typeMap.put("DATE", "Date");
		typeMap.put("TIME", "Date");
		typeMap.put("DATETIME", "Date");
		typeMap.put("TIMESTAMP", "Date");
		typeMap.put("YEAR", "Date");


		//数字类型
		numMap.put("INT", "Integer");
		numMap.put("INTEGER", "Long");
		numMap.put("TINYINT", "Integer");
		numMap.put("SMALLINT", "Integer");
		numMap.put("MEDIUMINT", "Integer");
		numMap.put("BIGINT", "Long");
		numMap.put("FLOAT", "Float");
		numMap.put("DOUBLE", "Double");
		numMap.put("BOOLEAN", "Integer");
		numMap.put("ID", "Integer");
	}
	
	/**
	 * 通过对应关系返回java中的数据类型
	 * @param mysqlType
	 * @return 没有查询到返回String
	 */
	public static String getBeanType(String mysqlType){
		String type=typeMap.get(mysqlType.toUpperCase());
		if(StringUtils.isEmpty(type)){
			type="String";
		}
		return type;
	}

	/**
	 * 是否数字类型
	 * @param mysqlType
	 * @return 没有查询到返回String
	 */
	public static boolean isNum(String mysqlType){
		return  numMap.containsKey(mysqlType.toUpperCase());
	}

}
