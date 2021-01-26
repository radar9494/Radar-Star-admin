package com.liuqi.business.dto.stat;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WaitExtractStatDto implements Serializable {

    /**
     * 总数
     */
    private BigDecimal total;
    /**
     * 笔数
     */
    private Integer quantity;

    private Long currencyId;

    private String currencyName;

}
