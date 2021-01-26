package com.liuqi.business.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceNumsDto {

    //价格
    private BigDecimal price=BigDecimal.ZERO;

    //数量
    private BigDecimal nums=BigDecimal.ZERO;

    private BigDecimal ratio;

    public PriceNumsDto() {
    }

    public PriceNumsDto(BigDecimal price, BigDecimal nums) {
        this.price = price;
        this.nums = nums;
    }
}
