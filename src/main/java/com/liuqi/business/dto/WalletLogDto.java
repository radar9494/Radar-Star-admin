package com.liuqi.business.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WalletLogDto implements Serializable{

    private Long currencyId;

    private String currencyName;

    private BigDecimal money;

    private String remark;
}
