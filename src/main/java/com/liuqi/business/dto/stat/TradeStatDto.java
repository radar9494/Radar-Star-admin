package com.liuqi.business.dto.stat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TradeStatDto implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    private BigDecimal quantity;

    private Long tradeId;

    private String areaName;

    private String tradeName;
}
