package com.liuqi.business.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CurrencyCountDto {

    //币种
    private String currencyName;
    //笔数
    private int total;
    //总数量
    private BigDecimal totalQuantity;
}
