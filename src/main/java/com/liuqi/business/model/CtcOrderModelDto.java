package com.liuqi.business.model;

import com.liuqi.business.enums.BuySellEnum;
import com.liuqi.business.enums.CtcOrderStatusEnum;
import lombok.Data;

@Data
public class CtcOrderModelDto extends CtcOrderModel{

										
    private String tradeTypeStr;

    public String getTradeTypeStr() {
        return BuySellEnum.getName(super.getTradeType());
    }

    private String statusStr;
    public String getStatusStr() {
        return CtcOrderStatusEnum.getName(super.getStatus());
    }


    private String currencyName;
    private String userName;
    private String realName;
    private String storeName;

}
