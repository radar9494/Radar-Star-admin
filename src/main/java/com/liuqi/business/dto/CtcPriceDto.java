package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CtcPriceDto implements Serializable{

    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
}
