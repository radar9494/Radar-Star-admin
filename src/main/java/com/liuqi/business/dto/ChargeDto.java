package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class ChargeDto implements Serializable {

    private Long currencyId;

    private BigDecimal charge;
}
