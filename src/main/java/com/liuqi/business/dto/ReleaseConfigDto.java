package com.liuqi.business.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ReleaseConfigDto {
    /**
     *每日最大释放次数
     */

    private Integer times;

    /**
     *每次释放百分比%
     */

    private BigDecimal timesRate;

    /**
     *每天释放总百分比%
     */

    private BigDecimal dayRate;

    /**
     *每天释放最大值
     */

    private BigDecimal dayMax;

    /**
     * 已释放总数key
     */
    private String alreadyTotalKey;
    /**
     *适当次数key
     */
    private String trusteeIdsKey;
}
