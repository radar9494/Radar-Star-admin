package com.liuqi.business.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class OtcWalletModelDto extends OtcWalletModel {


    private String userName;

    private String realName;

    private String currencyName;


    @JsonIgnore
    private String sortName = "create_time desc,t.id";
    @JsonIgnore
    private String sortType = "desc";

    private List<Long> currencyList;

    private BigDecimal cnyQuantity;

    private BigDecimal total;
}
