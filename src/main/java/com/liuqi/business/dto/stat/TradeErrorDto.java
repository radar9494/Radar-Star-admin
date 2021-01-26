package com.liuqi.business.dto.stat;

import lombok.Data;

import java.io.Serializable;

@Data
public class TradeErrorDto implements Serializable {

    private Long tradeId;

    private Integer count;

    private String tradeName;
}
