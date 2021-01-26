package com.liuqi.business.dto;

import com.liuqi.business.model.AlertsModelDto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CurrencyDto {

    private String currencyName;

    private BigDecimal total;
    private BigDecimal rechargeTotal;
    private BigDecimal extractTotal;
    private BigDecimal buyTotal;
    private BigDecimal sellTotal;


    private BigDecimal walletUsing;
    private BigDecimal walletFreeze;
    private BigDecimal fWalletUsing;
    private BigDecimal fWalletFreeze;

    private BigDecimal otcUsing;
    private BigDecimal otcFreeze;
    private BigDecimal fOtcUsing;
    private BigDecimal fOtcFreeze;


    private BigDecimal miningUsing;
    private BigDecimal miningFreeze;
    private BigDecimal fMiningUsing;
    private BigDecimal fMiningFreeze;


    private BigDecimal pledgeUsing;
    private BigDecimal pledgeFreeze;
    private BigDecimal fPledgeUsing;
    private BigDecimal fPledgeFreeze;

}
