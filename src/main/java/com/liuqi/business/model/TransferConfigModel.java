package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class TransferConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种
	 */
	
	private Long currencyId;
	
	/**
	 *矿工费
	 */
	
	private BigDecimal rate;
	
	/**
	 *最小数量
	 */
	
	private BigDecimal minQuantity;
	
	/**
	 *最大数量
	 */
	
	private BigDecimal maxQuantity;
	


}
