package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class OtcWalletModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *用户
	 */
	
	private Long userId;
	
	/**
	 *可用
	 */
	
	private BigDecimal using;
	
	/**
	 *冻结
	 */
	
	private BigDecimal freeze;




}
