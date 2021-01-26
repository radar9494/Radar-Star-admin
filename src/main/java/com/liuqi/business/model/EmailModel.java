package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class EmailModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *区域
	 */
	
	private String regionId;
	
	/**
	 *key
	 */
	
	private String accessKeyId;
	
	/**
	 *私钥
	 */
	
	private String secret;
	
	/**
	 *账户
	 */
	
	private String accountName;
	
	/**
	 *标签
	 */
	
	private String tag;
	
	/**
	 *0未启用 1启用
	 */
	
	private Integer status;
	
	/**
	 *总条数
	 */
	
	private Integer count;
	


}
