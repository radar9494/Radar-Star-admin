package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UserTotalDto implements Serializable{

    private Long userId;

    private BigDecimal quantity;
}
