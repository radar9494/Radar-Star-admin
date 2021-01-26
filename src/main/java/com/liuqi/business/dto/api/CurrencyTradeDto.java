package com.liuqi.business.dto.api;

import lombok.Builder;
import lombok.Data;

/**
 * description: CurrencyTradeDto <br>  获取所有交易对
 * date: 2020/5/20 16:00 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */

@Builder
@Data
public class CurrencyTradeDto {

    //交易对名称
    private String symbol;
    //交易货币币种
    private String currency;
    //计价货币币种
    private String quoteCurrency;

    public String getSymbol() {
        return currency+"-"+quoteCurrency;
    }
}
