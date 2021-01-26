package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;
import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class OtcWalletLogModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	
	private Long currencyId;
	
	/**
	 *金额
	 */
	
	private BigDecimal money;
	
	/**
	 *类型
	 */
	
	private Integer type;
	
	/**
	 *余额
	 */
	
	private BigDecimal balance;
	
	/**
	 *快照
	 */
	
	private String snapshot;
	
	/**
	 *订单id
	 */
	
	private Long orderId;
	


	private Long userId;

}
