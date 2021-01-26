package com.liuqi.business.dto.api;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

/**
 * description: CurrencyTradeDto <br>  交易对24小时行情
 * date: 2020/5/20 16:00 <br>
 * author: chenX <br>
 * version: 1.0 <br>
 */

@Builder
@Data
public class TradeQuotationDto {

    private String amount;

    private String openPrice;

    /**
     * 当前成交价格
     */
    private String currentPrice;

    /**
     * 今日交易的最高价格
     */
    private String tradeMaxPrice;
    /**
     * 今日交易的最低价格
     */
    private String tradeMinPrice;
}
