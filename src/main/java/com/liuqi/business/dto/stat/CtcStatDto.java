package com.liuqi.business.dto.stat;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CtcStatDto implements Serializable {

    //商户
    private Long storeId;
    //币种
    private Long currencyId;
    //状态
    private Integer tradeType;
    //状态
    private Integer status;
    //金额
    private BigDecimal money;
}
