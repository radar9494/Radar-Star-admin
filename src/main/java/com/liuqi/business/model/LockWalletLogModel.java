package com.liuqi.business.model;

import com.liuqi.base.BaseModel;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LockWalletLogModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *用户id
	 */
	private Long userId;
	
	/**
	 *币种id
	 */
	private Long currencyId;
	
	/**
	 *金额
	 */
	private BigDecimal money;
	
	/**
	 * LockWalletLogTypeEnum枚举
	 */
	private Integer type;

	/**
	 *金额
	 */
	private BigDecimal balance;
	/**
	 *金额
	 */
	private String snapshot;

	/**
	 * 订单
	 */
	private Long orderId;
}
