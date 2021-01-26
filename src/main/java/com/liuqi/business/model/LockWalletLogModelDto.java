package com.liuqi.business.model;

import com.liuqi.business.enums.LockWalletLogTypeEnum;
import lombok.Data;


@Data
public class LockWalletLogModelDto extends LockWalletLogModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String userName;
	private String realName;
	private String currencyName;
	private String typeStr;


	public String getTypeStr() {
		return LockWalletLogTypeEnum.getName(super.getType());
	}
}
