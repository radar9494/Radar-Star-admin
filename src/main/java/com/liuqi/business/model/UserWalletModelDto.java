package com.liuqi.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class UserWalletModelDto extends UserWalletModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String userName;
	private String realName;
	private String currencyName;

	/**
	 *提币开关
	 */
	private boolean extractSwitch;

	/**
	 *充值开关
	 */
	private boolean rechargeSwitch;

	private BigDecimal cnyQuantity;


	/**
	 * 总数量(可用加冻结)
	 */
	private BigDecimal total;

	private BigDecimal price;

	private List<Long> currencyList;

	private Integer position;

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}
