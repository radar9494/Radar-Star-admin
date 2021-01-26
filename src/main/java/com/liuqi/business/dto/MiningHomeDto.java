package com.liuqi.business.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * description: MiningHomeDto <br>
 * date: 2020/6/9 14:11 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MiningHomeDto implements Serializable {


    private long bestHoldingCurrency;  //最佳持币
    private long worstHoldingCurrency; //最低持币
    private BigDecimal staticQuantity; // 持币收益
    private BigDecimal dynamicQuantity;  //推广
    private BigDecimal timestampQuantity; //时间戳
    private BigDecimal total; //累计收益


    private Long currencyId;

    private String currencyName;

    private String image;

    private BigDecimal freeze;

    private BigDecimal outQuantity;

}
