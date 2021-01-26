package com.liuqi.business.model;

import com.liuqi.business.enums.BuySellEnum;
import lombok.Data;

@Data
public class TradeRecordUserModelDto extends TradeRecordUserModel{


    private String tradeTypeStr;

    public String getTradeTypeStr() {
        return BuySellEnum.getName(super.getTradeType());
    }

    private String userName;
    private String tradeName;

    private boolean limit;
    private Integer count;

}
