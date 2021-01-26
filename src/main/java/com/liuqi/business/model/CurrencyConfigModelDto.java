package com.liuqi.business.model;

import com.liuqi.business.enums.SwitchEnum;
import com.liuqi.business.enums.WalletTypeEnum;
import com.liuqi.business.enums.YesNoEnum;
import lombok.Data;


@Data
public class CurrencyConfigModelDto extends CurrencyConfigModel{

	/**
	 *serialVersionUID
	 */
	private static final long serialVersionUID = 1L;


	private String rateCurrencyName;
	private String extractSwitchStr;
	private String rechargeSwitchStr;
	private String tradeSwitchStr;
	private String extractMaxDaySwitchStr;
	private String percentageStr;


	private Integer position;
	//币种名称
	private String currencyName;

	public String getExtractSwitchStr() {
		return SwitchEnum.getName(super.getExtractSwitch());
	}

	public String getRechargeSwitchStr() {
		return SwitchEnum.getName(super.getRechargeSwitch());
	}

	public String getExtractMaxDaySwitchStr() {
		return SwitchEnum.getName(super.getExtractMaxDaySwitch());
	}

	public String getPercentageStr() { return YesNoEnum.getName( super.getPercentage());}
	private String walletTypeStr;
	public String getWalletTypeStr() { return WalletTypeEnum.getName( super.getWalletType());}
}
