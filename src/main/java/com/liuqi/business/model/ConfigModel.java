package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class ConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *名称
	 */
	private String name;
	
	/**
	 *
	 */
	private String val;
	
	/**
	 *
	 */
	private String remarks;
	
	/**
	 *1.系统 2.开关 3.利率
	 */
	private Integer type;
	


}
