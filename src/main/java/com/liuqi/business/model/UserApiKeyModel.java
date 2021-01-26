package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class UserApiKeyModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	
	private Long userId;
	
	/**
	 *
	 */
	
	private String apiKey;
	
	/**
	 *
	 */
	
	private String secretKey;
	
	/**
	 *状态0未启用 1启用
	 */
	
	private Integer status;
	

	private Long currencyId;

}
