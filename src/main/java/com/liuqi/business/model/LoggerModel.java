package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class LoggerModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *类型（1.添加 2.更新）
	 */
	private Integer type;
	
	/**
	 *标题
	 */
	private String title;
	
	/**
	 *内容
	 */
	private String content;
	
	/**
	 *管理员ID
	 */
	private Long adminId;



}
