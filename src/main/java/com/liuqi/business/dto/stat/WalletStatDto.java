package com.liuqi.business.dto.stat;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WalletStatDto implements Serializable {

    private Long currencyId;

    private String currencyName;

    private BigDecimal using=BigDecimal.ZERO;

    private BigDecimal freeze=BigDecimal.ZERO;

    private BigDecimal locking=BigDecimal.ZERO;

    private BigDecimal lockFreeze=BigDecimal.ZERO;
}
