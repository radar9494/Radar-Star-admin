package com.liuqi.business.dto.api.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author tanyan
 * @create 2020-07=20
 * @description
 */
@Data
public class OrderDto {
    /**
     * 交易对
     */
    @NotNull(message = "订单id不能为空")
    private Long orderId;

    private Long userId;
}
