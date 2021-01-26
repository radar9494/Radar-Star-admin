package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class ServiceChargeDetailModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *订单id
	 */
	private Long orderId;
	
	/**
	 *订单类型 1交易
	 */
	private Integer type;
	
	/**
	 *币种id
	 */
	private Long currencyId;
	
	/**
	 *手续费
	 */
	private BigDecimal charge;
	


}
