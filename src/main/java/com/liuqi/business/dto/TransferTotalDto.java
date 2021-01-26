package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class TransferTotalDto implements Serializable{

    private Long currencyId;

    private Integer times;

    private BigDecimal total;
}
