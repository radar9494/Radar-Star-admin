package com.liuqi.business.model;

import com.liuqi.business.enums.ApiTransferTypeEnum;
import com.liuqi.business.enums.WalletDoEnum;
import lombok.Data;

@Data
public class ApiTransferModelDto extends ApiTransferModel{

					
	private String typeStr;

    public String getTypeStr(){
    	return ApiTransferTypeEnum.getName(super.getType());
	}


	private String statusStr;

	public String getStatusStr() {
		return WalletDoEnum.getName(super.getStatus());
	}

	private String userName;
	private String currencyName;

}
