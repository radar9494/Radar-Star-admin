package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class CurrencyDataModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *中文名
	 */
	
	private String ch;
	
	/**
	 *英文名
	 */
	
	private String en;
	
	/**
	 *简介
	 */
	
	private String introduction;
	
	/**
	 *发行时间
	 */
	private String issue;
	
	/**
	 *发行数量
	 */
	
	private String issueQuantity;
	
	/**
	 *流通总量
	 */
	
	private String circulate;
	
	/**
	 *众筹价格
	 */
	
	private String price;
	
	/**
	 *白皮书
	 */
	
	private String white;
	
	/**
	 *官网
	 */
	
	private String website;
	


}
