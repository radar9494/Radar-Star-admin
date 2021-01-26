package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class UserWalletLogModel extends BaseModel{

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
	 * WalletLogTypeEnum枚举
	 *类型 1交易  2转账  3充值  4提现
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
