package com.liuqi.business.model;

import com.liuqi.business.enums.WalletDoEnum;
import lombok.Data;

@Data
public class ChargeAwardModelDto extends ChargeAwardModel{


    private String statusStr;

    public String getStatusStr() {
        return WalletDoEnum.getName(super.getStatus());
    }

    private String userName;
    private String realName;
    private String currencyName;
    private String snapChargeCurrencyName;

}
