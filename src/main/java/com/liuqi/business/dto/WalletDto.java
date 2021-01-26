package com.liuqi.business.dto;

import lombok.Data;

import javax.swing.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class WalletDto implements Serializable{

    private Long currencyId;

    private BigDecimal using;

    private BigDecimal freeze;
}
