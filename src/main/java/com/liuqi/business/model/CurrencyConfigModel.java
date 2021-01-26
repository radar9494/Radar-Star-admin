package com.liuqi.business.model;

import lombok.Data;
import java.util.Date;
import java.math.BigDecimal;
import com.liuqi.base.BaseModel;

@Data
public class CurrencyConfigModel extends BaseModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	/**
	 *币种id
	 */
	private Long currencyId;
	/********提币****************************************/
	/**
	 *提币开关（0关 1开）
	 */
	private Integer extractSwitch;

	/**
	 *提币每天最大数量
	 */
	private BigDecimal extractMaxDay;

	/**
	 *提币每天最大数量开关（0关 1开）
	 */
	private Integer extractMaxDaySwitch;
	/**
	 *提币手续费
	 */
	private BigDecimal extractRate;
	/**
	 *是否百分比 1是百分比 2不是百分比
	 */
	private Integer percentage;

	/**
	 *最低提币数量
	 */
	private BigDecimal extractMin;

	/**
	 *最高提币数量
	 */
	private BigDecimal extractMax;

	/*****充值*******************************************/
	/**
	 *充值开关（0关 1开）
	 */
	private Integer rechargeSwitch;
	/**
	 * 充值最小数量
	 */
	private BigDecimal rechargeMinQuantity;
	/**
	 *充值地址
	 */
	private String rechargeAddress;


	//提现 0可用 1锁仓
	private Integer walletType;

	private Long rateCurrencyId;


}
