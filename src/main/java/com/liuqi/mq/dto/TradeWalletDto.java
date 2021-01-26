package com.liuqi.mq.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeWalletDto implements Serializable {

    private Long recordId;
    private boolean buy;
    private boolean sell;
    public TradeWalletDto() {

    }

    public TradeWalletDto(Long recordId,boolean buy,boolean sell) {
        this.recordId = recordId;
        this.buy = buy;
        this.sell = sell;
    }
}
