package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class ReConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *url地址
	 */
	
	private String url;
	
	/**
	 *商户编码
	 */
	
	private String storeNo;
	
	/**
	 *key
	 */
	
	private String key;
	


}
