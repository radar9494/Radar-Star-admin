package com.liuqi.business.model;

import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.WalletDoEnum;
import lombok.Data;

@Data
public class LockReleaseModelDto extends LockReleaseModel{

    private String statusStr;

    public String getStatusStr() {
        return WalletDoEnum.getName(super.getStatus());
    }
    private String tradeTypeStr;

    public String getTradeTypeStr() {
        return BuySellEnum.getName(super.getTradeType());
    }
    private String userName;
    private String realName;
    private String currencyName;


}
