package com.liuqi.business.dto.stat;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class RechargeExtractStatDto  implements Serializable {

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date date;

    private Long currencyId;

    private String currencyName;

    private BigDecimal total;
}

