package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MiningWalletModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户
	 */
	
	private Long userId;
	
	/**
	 *数量
	 */
	
	private BigDecimal using;
	
	/**
	 *
	 */
	
	private Long currencyId;
	


	private BigDecimal freeze;

	private BigDecimal total;

	  private BigDecimal cnyQuantity;

}
