package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MiningConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种
	 */
	private Long currencyId;
	
	/**
	 *转入最小值
	 */
	private BigDecimal transferMin;
	/**
	 *转出最小值
	 */
	private BigDecimal outMin;
	/**
	 * 基数
	 */
	private BigDecimal cardinality;


	private Integer type;

	private BigDecimal staticRate;

	private BigDecimal dynamicRate;

   private BigDecimal best;

   private BigDecimal worst;

   private BigDecimal redemptionUsdt;

   private BigDecimal outQuantity;
}
