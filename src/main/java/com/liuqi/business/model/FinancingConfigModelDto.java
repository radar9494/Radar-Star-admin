package com.liuqi.business.model;

import com.liuqi.business.enums.FinancingConfigGrantTypeEnum;
import com.liuqi.business.enums.FinancingConfigStatusEnum;
import lombok.Data;

@Data
public class FinancingConfigModelDto extends FinancingConfigModel{

										
	private String grantTypeStr;

    public String getGrantTypeStr(){
    	return FinancingConfigGrantTypeEnum.getName(super.getGrantType());
	}
		
	private String statusStr;

    public String getStatusStr(){
		return FinancingConfigStatusEnum.getName(super.getStatus());
	}

	private String currencyName;
	private String financingCurrencyName;


}
