package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author tanyan
 * @create 2020-02=09
 * @description
 */
@Data
public class MiningSubDto implements Serializable {
    private String name;
    private Double quantity;
    private Double teamQuantity;

    private Double computing;
}
