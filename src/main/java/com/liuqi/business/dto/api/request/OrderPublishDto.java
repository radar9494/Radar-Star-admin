package com.liuqi.business.dto.api.request;

import io.swagger.annotations.ApiImplicitParam;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
@Data
public class OrderPublishDto {
    /**
     * 交易对
     */
    @NotNull(message = "交易对不能为空")
    private String symbol;
    /**
     * 交易类型0买  1卖
     */
    private int tradeType;
    /**
     * 数量
     */
    @NotNull(message = "数量不能为空")
    @DecimalMin(value = "0",message = "数量必须大于0")
    private BigDecimal quantity;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 0 限价交易 1 市价交易
     */
    private int transactionType;

    private Long userId;
}
