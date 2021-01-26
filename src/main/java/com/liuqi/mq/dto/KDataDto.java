package com.liuqi.mq.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class KDataDto implements Serializable {


    private Long tradeId;
    private BigDecimal tradePrice;
    private BigDecimal tradeQuantity;

    public KDataDto() {

    }

    public KDataDto(Long tradeId, BigDecimal tradePrice, BigDecimal tradeQuantity) {
        this.tradeId = tradeId;
        this.tradePrice = tradePrice;
        this.tradeQuantity = tradeQuantity;
    }
}
