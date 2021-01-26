package com.liuqi.business.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class MiningWalletModelDto extends MiningWalletModel{

    private String userName;
    private String currencyName;


    private BigDecimal cnyQuantity;
    private List<Long> currencyList;

    private Integer teamType;

    private List<Long> userIds;
}
