package com.liuqi.business.dto.api;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * description: CurrencyTradeDto <br>  获取所有交易对
 * date: 2020/5/20 16:00 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */

@Builder
@Data
public class AllWalletDto {

    //交易对名称
    private String currencyName;
    private BigDecimal using;
    //计价货币币种
    private BigDecimal freeze;
    private String userName;
}
