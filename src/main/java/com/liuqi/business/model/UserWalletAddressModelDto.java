package com.liuqi.business.model;

import lombok.Data;


@Data
public class UserWalletAddressModelDto extends UserWalletAddressModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	private String userName;
	private String realName;
	private String currencyName;


}
