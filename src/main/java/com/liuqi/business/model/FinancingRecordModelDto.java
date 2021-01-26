package com.liuqi.business.model;

import lombok.Data;
import com.liuqi.business.enums.FinancingRecordStatusEnum;

@Data
public class FinancingRecordModelDto extends FinancingRecordModel{

							
	private String statusStr;

    public String getStatusStr(){
    	return FinancingRecordStatusEnum.getName(super.getStatus());
	}
	

	private String userName;
	private String currencyName;
	private String financingCurrencyName;
}
