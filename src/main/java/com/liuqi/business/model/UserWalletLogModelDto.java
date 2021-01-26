package com.liuqi.business.model;

import com.liuqi.business.enums.WalletLogTypeEnum;
import lombok.Data;


@Data
public class UserWalletLogModelDto extends UserWalletLogModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	private String userName;
	private String realName;
	private String currencyName;
	private String typeStr;


	public String getTypeStr() {
		if(super.getType()!=null){
			typeStr= WalletLogTypeEnum.getName(super.getType());
		}
		return typeStr;
	}
}
